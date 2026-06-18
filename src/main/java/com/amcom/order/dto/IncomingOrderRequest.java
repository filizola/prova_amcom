package com.amcom.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record IncomingOrderRequest(
        @NotBlank @Size(max = 100) String externalId,
        @Size(max = 255) String customerName,
        @Size(min = 3, max = 3) String currency,
        @NotEmpty @Valid List<IncomingOrderItemRequest> items
) {
}
