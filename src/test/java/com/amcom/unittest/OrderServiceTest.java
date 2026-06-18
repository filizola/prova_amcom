package com.amcom.unittest;

import com.amcom.order.domain.model.Order;
import com.amcom.order.domain.model.OrderStatus;
import com.amcom.order.domain.repository.OrderRepository;
import com.amcom.order.dto.IncomingOrderItemRequest;
import com.amcom.order.dto.IncomingOrderRequest;
import com.amcom.order.dto.IngestOrderResponse;
import com.amcom.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldReturnExistingOrderWhenDuplicate() {
        Order existing = Order.createReceived("EXT-1", "Cliente", "BRL");
        existing.markProcessed(new BigDecimal("10.00"));

        when(orderRepository.findByExternalId("EXT-1")).thenReturn(Optional.of(existing));

        IngestOrderResponse response = orderService.ingestOrder(new IncomingOrderRequest(
                "EXT-1",
                "Cliente",
                "BRL",
                List.of(new IncomingOrderItemRequest("P1", "Produto", 1, new BigDecimal("10.00")))
        ));

        assertThat(response.duplicate()).isTrue();
        assertThat(response.externalId()).isEqualTo("EXT-1");
    }

    @Test
    void shouldProcessNewOrder() {
        when(orderRepository.findByExternalId("EXT-NEW")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IngestOrderResponse response = orderService.ingestOrder(new IncomingOrderRequest(
                "EXT-NEW",
                "Cliente Novo",
                "BRL",
                List.of(
                        new IncomingOrderItemRequest("P1", "Item 1", 2, new BigDecimal("5.00")),
                        new IncomingOrderItemRequest("P2", "Item 2", 1, new BigDecimal("3.50"))
                )
        ));

        assertThat(response.duplicate()).isFalse();
        assertThat(response.status()).isEqualTo(OrderStatus.PROCESSED);
        assertThat(response.totalAmount()).isEqualByComparingTo("13.50");
        verify(orderRepository).save(any(Order.class));
    }
}
