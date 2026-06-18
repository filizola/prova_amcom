package com.amcom.order.controller;

import com.amcom.order.config.IntegrationProperties;
import com.amcom.order.dto.IncomingOrderRequest;
import com.amcom.order.dto.IngestOrderResponse;
import com.amcom.order.exception.UnauthorizedIntegrationException;
import com.amcom.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/integration/system-a")
public class SystemAIntegrationController {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final OrderService orderService;
    private final IntegrationProperties integrationProperties;

    public SystemAIntegrationController(OrderService orderService, IntegrationProperties integrationProperties) {
        this.orderService = orderService;
        this.integrationProperties = integrationProperties;
    }

    @PostMapping("/orders")
    public ResponseEntity<IngestOrderResponse> receiveOrder(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @Valid @RequestBody IncomingOrderRequest request) {

        validateApiKey(apiKey, integrationProperties.getSystemAApiKey());

        IngestOrderResponse response = orderService.ingestOrder(request);
        HttpStatus status = response.duplicate() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    private void validateApiKey(String provided, String expected) {
        if (!Objects.equals(provided, expected)) {
            throw new UnauthorizedIntegrationException("API Key inválida para Produto Externo A");
        }
    }
}
