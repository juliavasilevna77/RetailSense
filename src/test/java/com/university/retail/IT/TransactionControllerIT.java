package com.university.retail.IT;

import com.university.retail.config.TestDataInitializer;
import com.university.retail.domain.TransactionDTO;
import com.university.retail.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String managerToken;
    private UUID transaction1Id;
    private UUID customer1Id;
    private UUID product1Id;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        transaction1Id = testData.transaction1Id();
        customer1Id = testData.customer1Id();
        product1Id = testData.product1Id();
        managerToken = testData.managerToken();
    }

    @Test
    void shouldRetrieveAllTransactions() {
        webTestClient.get()
                .uri("/api/v1/transactions")
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDTO.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertEquals(2, response.getResponseBody().size()); // Проверяем, что загружены 2 транзакции из тестов
                });
    }

    @Test
    void shouldRetrieveTransactionById() {
        webTestClient.get()
                .uri("/api/v1/transactions/{id}", transaction1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDTO.class)
                .consumeWith(response -> {
                    TransactionDTO transaction = response.getResponseBody();
                    assertNotNull(transaction);
                    assertEquals(customer1Id, transaction.getCustomerId());
                    assertEquals(product1Id, transaction.getProductId());
                    assertEquals("CARD", transaction.getPaymentMethod());
                });
    }

    @Test
    void shouldFailToRetrieveNonexistentTransaction() {
        UUID nonexistentTransactionId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/transactions/{id}", nonexistentTransactionId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Transaction not found with id: " + nonexistentTransactionId);
    }

    @Test
    void shouldCreateNewTransaction() {
        TransactionDTO newTransaction = TransactionDTO.builder()
                .customerId(customer1Id)
                .productId(product1Id)
                .date(LocalDateTime.now())
                .paymentMethod("CASH")
                .totalAmount(999.99)
                .build();

        webTestClient.post()
                .uri("/api/v1/transactions")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newTransaction)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDTO.class)
                .consumeWith(response -> {
                    TransactionDTO transaction = response.getResponseBody();
                    assertNotNull(transaction);
                    assertEquals(customer1Id, transaction.getCustomerId());
                    assertEquals(product1Id, transaction.getProductId());
                    assertEquals("CASH", transaction.getPaymentMethod());
                });
    }

    @Test
    void shouldDeleteTransactionSuccessfully() {
        webTestClient.delete()
                .uri("/api/v1/transactions/{id}", transaction1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteNonexistentTransaction() {
        UUID nonexistentTransactionId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/transactions/{id}", nonexistentTransactionId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Transaction not found with id: " + nonexistentTransactionId);
    }
}
