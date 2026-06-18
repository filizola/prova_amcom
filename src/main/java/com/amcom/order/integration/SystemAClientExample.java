package com.amcom.order.integration;

import com.amcom.order.dto.IncomingOrderItemRequest;
import com.amcom.order.dto.IncomingOrderRequest;
import com.amcom.order.dto.IngestOrderResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

public class             SystemAClientExample {

    private static final String BASE_URL = System.getenv().getOrDefault("ORDER_SERVICE_URL", "http://localhost:8080");
    private static final String API_KEY = System.getenv().getOrDefault("SYSTEM_A_API_KEY", "dev-system-a-key");

    public static void main(String[] args) {
        RestClient client = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-API-Key", API_KEY)
                .build();

        IncomingOrderRequest request = new IncomingOrderRequest(
                "EXT-A-" + System.currentTimeMillis(),
                "Cliente Exemplo A",
                "BRL",
                List.of(
                        new IncomingOrderItemRequest("SKU-001", "Cerveja Premium 350ml", 12, new BigDecimal("4.99")),
                        new IncomingOrderItemRequest("SKU-002", "Refrigerante 2L", 6, new BigDecimal("8.50"))
                )
        );

        IngestOrderResponse response = client.post()
                .uri("/api/v1/integration/system-a/orders")
                .body(request)
                .retrieve()
                .body(IngestOrderResponse.class);

        System.out.println(response);
    }
}
