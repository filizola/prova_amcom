package com.amcom.order.dto;

import com.amcom.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String externalId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String customerName,
        Instant createdAt,
        Instant updatedAt,
        Instant processedAt,
        List<OrderItemResponse> items
) {
}
