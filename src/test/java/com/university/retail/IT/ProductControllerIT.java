package com.university.retail.IT;

import com.university.retail.config.TestDataInitializer;
import com.university.retail.domain.ProductDTO;
import com.university.retail.exception.ProductNotFoundException;
import com.university.retail.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    @Autowired
    private TransactionRepository transactionRepository;

    private String managerToken;
    private UUID product1Id;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        product1Id = testData.product1Id();
        managerToken = testData.managerToken();
    }

    @Test
    void shouldRetrieveAllProducts() {
        webTestClient.get()
                .uri("/api/v1/products")
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertEquals(2, response.getResponseBody().size()); // Проверяем, что загружены 2 продукта из тестов
                });
    }

    @Test
    void shouldRetrieveProductById() {
        webTestClient.get()
                .uri("/api/v1/products/{id}", product1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .consumeWith(response -> {
                    ProductDTO product = response.getResponseBody();
                    assertNotNull(product);
                    assertEquals("Laptop", product.getName());
                    assertEquals("Electronics", product.getCategory());
                    assertEquals(1200.00, product.getPrice());
                });
    }

    @Test
    void shouldFailToRetrieveNonexistentProduct() {
        UUID nonexistentProductId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/products/{id}", nonexistentProductId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Product not found with id: " + nonexistentProductId);
    }

    @Test
    void shouldCreateNewProduct() {
        ProductDTO newProduct = ProductDTO.builder()
                .name("Tablet")
                .category("Electronics")
                .price(600.00)
                .stockStatus(true)
                .build();

        webTestClient.post()
                .uri("/api/v1/products")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .consumeWith(response -> {
                    ProductDTO product = response.getResponseBody();
                    assertNotNull(product);
                    assertEquals("Tablet", product.getName());
                    assertEquals("Electronics", product.getCategory());
                });
    }


    @Test
    void shouldUpdateProduct() {
        ProductDTO updatedProduct = ProductDTO.builder()
                .name("Updated Laptop")
                .category("Electronics")
                .price(1100.00)
                .stockStatus(true)
                .build();

        webTestClient.put()
                .uri("/api/v1/products/{id}", product1Id)
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .consumeWith(response -> {
                    ProductDTO product = response.getResponseBody();
                    assertNotNull(product);
                    assertEquals("Updated Laptop", product.getName());
                    assertEquals(1100.00, product.getPrice());
                });
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        transactionRepository.deleteAll();

        webTestClient.delete()
                .uri("/api/v1/products/{id}", product1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteNonexistentProduct() {
        UUID nonexistentProductId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/products/{id}", nonexistentProductId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Product not found with id: " + nonexistentProductId);
    }
}
