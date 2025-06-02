package com.university.retail.IT;

import com.university.retail.config.TestDataInitializer;
import com.university.retail.domain.CustomerDTO;
import com.university.retail.domain.enums.Gender;
import com.university.retail.exception.CustomerNotFoundException;
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
public class CustomerControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    @Autowired
    private TransactionRepository transactionRepository;

    private String managerToken;
    private UUID customer1Id;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        customer1Id = testData.customer1Id();

        managerToken = testData.managerToken();
    }

    @Test
    void shouldRetrieveAllCustomers() {
        webTestClient.get()
                .uri("/api/v1/customers")
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDTO.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertEquals(2, response.getResponseBody().size()); // Проверяем, что загружены 2 клиента из тестов
                });
    }

    @Test
    void shouldRetrieveCustomerById() {
        webTestClient.get()
                .uri("/api/v1/customers/{id}", customer1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(response -> {
                    CustomerDTO customer = response.getResponseBody();
                    assertNotNull(customer);
                    assertEquals("customer1", customer.getUsername());
                    assertEquals("customer1@example.com", customer.getEmail());
                });
    }

    @Test
    void shouldFailToRetrieveNonexistentCustomer() {
        UUID nonexistentCustomerId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/customers/{id}", nonexistentCustomerId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Customer not found with id: " + nonexistentCustomerId);
    }

    @Test
    void shouldCreateNewCustomer() {
        CustomerDTO newCustomer = CustomerDTO.builder()
                .username("newCustomer")
                .email("newcustomer@example.com")
                .age(28)
                .gender(Gender.MALE)
                .build();

        webTestClient.post()
                .uri("/api/v1/customers")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(response -> {
                    CustomerDTO customer = response.getResponseBody();
                    assertNotNull(customer);
                    assertEquals("newCustomer", customer.getUsername());
                    assertEquals("newcustomer@example.com", customer.getEmail());
                });
    }

    @Test
    void shouldFailToCreateCustomerWithInvalidEmail() {
        CustomerDTO invalidCustomer = CustomerDTO.builder()
                .username("invalidUser")
                .email("invalid_email")
                .age(25)
                .gender(Gender.FEMALE)
                .build();

        webTestClient.post()
                .uri("/api/v1/customers")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid email format");
    }

    @Test
    void shouldUpdateCustomer() {
        CustomerDTO updatedCustomer = CustomerDTO.builder()
                .username("updatedCustomer")
                .email("updatedcustomer@example.com")
                .age(35)
                .gender(Gender.MALE)
                .build();

        webTestClient.put()
                .uri("/api/v1/customers/{id}", customer1Id)
                .header("Authorization", "Bearer " + managerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(response -> {
                    CustomerDTO customer = response.getResponseBody();
                    assertNotNull(customer);
                    assertEquals("updatedCustomer", customer.getUsername());
                    assertEquals("updatedcustomer@example.com", customer.getEmail());
                });
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        transactionRepository.deleteAll();

        webTestClient.delete()
                .uri("/api/v1/customers/{id}", customer1Id)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteNonexistentCustomer() {
        UUID nonexistentCustomerId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/customers/{id}", nonexistentCustomerId)
                .header("Authorization", "Bearer " + managerToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Customer not found with id: " + nonexistentCustomerId);
    }
}
