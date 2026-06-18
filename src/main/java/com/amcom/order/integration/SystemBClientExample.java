package com.amcom.order.integration;

import com.amcom.order.domain.model.OrderStatus;
import com.amcom.order.dto.OrderResponse;
import com.amcom.order.dto.OrderSummaryResponse;
import com.amcom.order.dto.PageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class SystemBClientExample {

    private static final String BASE_URL = System.getenv().getOrDefault("ORDER_SERVICE_URL", "http://localhost:8080");
    private static final String API_KEY = System.getenv().getOrDefault("SYSTEM_B_API_KEY", "dev-system-b-key");

    public static void main(String[] args) {
        RestClient client = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-API-Key", API_KEY)
                .build();

        PageResponse<OrderSummaryResponse> page = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/integration/system-b/orders")
                        .queryParam("status", OrderStatus.PROCESSED.name())
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<PageResponse<OrderSummaryResponse>>() {});

        System.out.println(page);

        if (page.content().isEmpty()) {
            return;
        }

        OrderSummaryResponse first = page.content().getFirst();
        OrderResponse detail = client.get()
                .uri("/api/v1/integration/system-b/orders/{id}", first.id())
                .retrieve()
                .body(OrderResponse.class);

        System.out.println(detail);
    }
}
