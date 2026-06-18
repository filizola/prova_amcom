package com.amcom.order.controller;

import com.amcom.order.config.IntegrationProperties;
import com.amcom.order.domain.model.OrderStatus;
import com.amcom.order.dto.OrderResponse;
import com.amcom.order.dto.OrderStatusResponse;
import com.amcom.order.dto.OrderSummaryResponse;
import com.amcom.order.dto.PageResponse;
import com.amcom.order.exception.UnauthorizedIntegrationException;
import com.amcom.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integration/system-b")
public class SystemBIntegrationController {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final OrderService orderService;
    private final IntegrationProperties integrationProperties;

    public SystemBIntegrationController(OrderService orderService, IntegrationProperties integrationProperties) {
        this.orderService = orderService;
        this.integrationProperties = integrationProperties;
    }

    @GetMapping("/orders")
    public PageResponse<OrderSummaryResponse> listOrders(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        validateApiKey(apiKey, integrationProperties.getSystemBApiKey());
        return orderService.listOrders(status, pageable);
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrderById(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @PathVariable UUID id) {

        validateApiKey(apiKey, integrationProperties.getSystemBApiKey());
        return orderService.getOrderById(id);
    }

    @GetMapping("/orders/external/{externalId}")
    public OrderResponse getOrderByExternalId(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @PathVariable String externalId) {

        validateApiKey(apiKey, integrationProperties.getSystemBApiKey());
        return orderService.getOrderByExternalId(externalId);
    }

    @GetMapping("/orders/{id}/status")
    public OrderStatusResponse getOrderStatus(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @PathVariable UUID id) {

        validateApiKey(apiKey, integrationProperties.getSystemBApiKey());
        return orderService.getOrderStatus(id);
    }

    private void validateApiKey(String provided, String expected) {
        if (!Objects.equals(provided, expected)) {
            throw new UnauthorizedIntegrationException("API Key inválida para Produto Externo B");
        }
    }
}
