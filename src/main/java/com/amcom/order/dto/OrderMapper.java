package com.amcom.order.dto;

import com.amcom.order.domain.model.Order;
import com.amcom.order.domain.model.OrderItem;
import org.springframework.data.domain.Page;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getExternalId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCustomerName(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getProcessedAt(),
                order.getItems().stream().map(OrderMapper::toItemResponse).toList()
        );
    }

    public static OrderSummaryResponse toSummary(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getExternalId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCustomerName(),
                order.getCreatedAt(),
                order.getProcessedAt(),
                order.getItemsCount()
        );
    }

    public static OrderStatusResponse toStatusResponse(Order order) {
        return new OrderStatusResponse(
                order.getId(),
                order.getExternalId(),
                order.getStatus(),
                order.getUpdatedAt(),
                order.getProcessedAt()
        );
    }

    public static IngestOrderResponse toIngestResponse(Order order, boolean duplicate) {
        return new IngestOrderResponse(
                order.getId(),
                order.getExternalId(),
                order.getStatus(),
                order.getTotalAmount(),
                duplicate
        );
    }

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductCode(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
