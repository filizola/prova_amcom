package com.amcom.order.service;

import com.amcom.order.domain.model.Order;
import com.amcom.order.domain.model.OrderItem;
import com.amcom.order.domain.model.OrderStatus;
import com.amcom.order.domain.repository.OrderRepository;
import com.amcom.order.dto.IncomingOrderItemRequest;
import com.amcom.order.dto.IncomingOrderRequest;
import com.amcom.order.dto.IngestOrderResponse;
import com.amcom.order.dto.OrderMapper;
import com.amcom.order.dto.OrderResponse;
import com.amcom.order.dto.OrderStatusResponse;
import com.amcom.order.dto.OrderSummaryResponse;
import com.amcom.order.dto.PageResponse;
import com.amcom.order.exception.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public IngestOrderResponse ingestOrder(IncomingOrderRequest request) {
        return orderRepository.findByExternalId(request.externalId())
                .map(existing -> OrderMapper.toIngestResponse(existing, true))
                .orElseGet(() -> createAndProcessOrder(request));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + id));
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByExternalId(String externalId) {
        Order order = orderRepository.findWithItemsByExternalId(externalId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + externalId));
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse getOrderStatus(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + id));
        return OrderMapper.toStatusResponse(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> listOrders(OrderStatus status, Pageable pageable) {
        Page<Order> page = orderRepository.findAllFiltered(status, pageable);
        return OrderMapper.toPageResponse(page.map(OrderMapper::toSummary));
    }

    private IngestOrderResponse createAndProcessOrder(IncomingOrderRequest request) {
        Order order = Order.createReceived(
                request.externalId(),
                request.customerName(),
                request.currency()
        );

        for (IncomingOrderItemRequest itemRequest : request.items()) {
            order.addItem(OrderItem.of(
                    itemRequest.productCode(),
                    itemRequest.productName(),
                    itemRequest.quantity(),
                    itemRequest.unitPrice()
            ));
        }

        order.markProcessing();

        try {
            BigDecimal total = calculateTotal(order);
            order.markProcessed(total);
            Order saved = orderRepository.save(order);
            log.info("Pedido {} processado: {}", saved.getExternalId(), saved.getTotalAmount());
            return OrderMapper.toIngestResponse(saved, false);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Pedido duplicado em concorrência: {}", request.externalId());
            Order existing = orderRepository.findByExternalId(request.externalId())
                    .orElseThrow(() -> ex);
            return OrderMapper.toIngestResponse(existing, true);
        } catch (Exception ex) {
            order.markFailed();
            orderRepository.save(order);
            throw ex;
        }
    }

    private BigDecimal calculateTotal(Order order) {
        return order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
