package com.amcom.integration;

import com.amcom.order.OrderApplication;
import com.amcom.order.domain.model.OrderStatus;
import com.amcom.order.dto.IncomingOrderItemRequest;
import com.amcom.order.dto.IncomingOrderRequest;
import com.amcom.order.dto.IngestOrderResponse;
import com.amcom.order.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OrderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldIngestOrderAndQueryFromSystemB() {
        String externalId = "TEST-ORDER-001";
        IncomingOrderRequest request = new IncomingOrderRequest(
                externalId,
                "Cliente Teste",
                "BRL",
                List.of(
                        new IncomingOrderItemRequest("P1", "Produto 1", 2, new BigDecimal("10.00")),
                        new IncomingOrderItemRequest("P2", "Produto 2", 1, new BigDecimal("25.50"))
                )
        );

        IngestOrderResponse ingestResponse = webTestClient.post()
                .uri("/api/v1/integration/system-a/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", "dev-system-a-key")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(IngestOrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(ingestResponse).isNotNull();
        assertThat(ingestResponse.duplicate()).isFalse();
        assertThat(ingestResponse.status()).isEqualTo(OrderStatus.PROCESSED);
        assertThat(ingestResponse.totalAmount()).isEqualByComparingTo("45.50");

        OrderResponse detailResponse = webTestClient.get()
                .uri("/api/v1/integration/system-b/orders/external/{externalId}", externalId)
                .header("X-API-Key", "dev-system-b-key")
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(detailResponse).isNotNull();
        assertThat(detailResponse.items()).hasSize(2);
        assertThat(detailResponse.totalAmount()).isEqualByComparingTo("45.50");
    }

    @Test
    void shouldDetectDuplicateOrders() {
        String externalId = "TEST-DUP-001";
        IncomingOrderRequest request = new IncomingOrderRequest(
                externalId,
                "Cliente Dup",
                "BRL",
                List.of(new IncomingOrderItemRequest("P1", "Produto", 1, new BigDecimal("9.99")))
        );

        webTestClient.post()
                .uri("/api/v1/integration/system-a/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", "dev-system-a-key")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        IngestOrderResponse duplicateResponse = webTestClient.post()
                .uri("/api/v1/integration/system-a/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", "dev-system-a-key")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IngestOrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(duplicateResponse).isNotNull();
        assertThat(duplicateResponse.duplicate()).isTrue();
    }

    @Test
    void shouldListProcessedOrders() {
        webTestClient.get()
                .uri("/api/v1/integration/system-b/orders?status=PROCESSED")
                .header("X-API-Key", "dev-system-b-key")
                .exchange()
                .expectStatus().isOk();
    }
}
