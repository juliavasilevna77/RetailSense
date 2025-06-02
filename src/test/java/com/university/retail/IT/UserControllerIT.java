package com.university.retail.IT;

import com.university.retail.config.TestDataInitializer;
import com.university.retail.domain.UserDTO;
import com.university.retail.domain.enums.Role;
import com.university.retail.entity.UserEntity;
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
public class UserControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID adminId;


    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        adminId = testData.adminId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldUpdateUserProfile() {
        UserEntity updatedUser = UserEntity.builder()
                .email("updated@example.com")
                .password("password")
                .username("updatedUser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                    assertEquals("updated@example.com", user.getEmail());
                    assertEquals("updatedUser", user.getUsername());
                });
    }

    @Test
    void shouldFailToUpdateNonexistentUser() {
        UUID nonexistentUserId = UUID.randomUUID();

        UserEntity updatedUser = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .username("testuser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", nonexistentUserId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found with id: " + nonexistentUserId);
    }

    @Test
    void shouldFailToUpdateUserWithInvalidEmail() {
        UserEntity invalidUser = UserEntity.builder()
                .email("invalid email")
                .password("password")
                .username("testuser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid email format");
    }

    @Test
    void shouldRetrieveUserByEmail() {
        webTestClient.get()
                .uri("/api/v1/users/email/{email}", "admin@retail.com")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                    assertEquals("admin@retail.com", user.getEmail());
                    assertEquals("RetailAdmin", user.getUsername());
                });
    }

    @Test
    void shouldFailRetrieveNonexistentUserByEmail() {
        webTestClient.get()
                .uri("/api/v1/users/email/{email}", "nonexistent@retail.com")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found with email: nonexistent@retail.com");
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        webTestClient.delete()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteNonexistentUser() {
        UUID nonexistentUserId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/users/{id}", nonexistentUserId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found with id: " + nonexistentUserId);
    }

}
