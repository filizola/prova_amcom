package com.amcom.order.dto;

import com.amcom.order.domain.model.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderStatusResponse(
        UUID id,
        String externalId,
        OrderStatus status,
        Instant updatedAt,
        Instant processedAt
) {
}
