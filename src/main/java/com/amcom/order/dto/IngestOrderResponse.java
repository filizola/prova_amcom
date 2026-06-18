package com.amcom.order.dto;

import com.amcom.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record IngestOrderResponse(
        UUID id,
        String externalId,
        OrderStatus status,
        BigDecimal totalAmount,
        boolean duplicate
) {
}
