package com.university.retail.config;

import com.university.retail.domain.enums.Gender;
import com.university.retail.domain.enums.Role;
import com.university.retail.entity.*;
import com.university.retail.repository.*;
import com.university.retail.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public TestData initTestData() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        UserEntity admin = userRepository.save(UserEntity.builder()
                .email("admin@retail.com")
                .password("securepassword")
                .username("RetailAdmin")
                .role(Role.ADMIN)
                .build());

        String adminToken = jwtProvider.createToken(admin.getEmail(), admin.getRole());

        UserEntity manager = userRepository.save(UserEntity.builder()
                .email("manager@retail.com")
                .password("securepassword")
                .username("RetailManager")
                .role(Role.MANAGER)
                .build());

        String managerToken = jwtProvider.createToken(manager.getEmail(), manager.getRole());

        CustomerEntity customer1 = customerRepository.save(CustomerEntity.builder()
                .username("customer1")
                .email("customer1@example.com")
                .age(25)
                .gender(Gender.MALE)
                .createdAt(LocalDateTime.now())
                .build());

        CustomerEntity customer2 = customerRepository.save(CustomerEntity.builder()
                .username("customer2")
                .email("customer2@example.com")
                .age(30)
                .gender(Gender.FEMALE)
                .createdAt(LocalDateTime.now())
                .build());

        ProductEntity product1 = productRepository.save(ProductEntity.builder()
                .name("Laptop")
                .category("Electronics")
                .price(1200.00)
                .stockStatus(true)
                .build());

        ProductEntity product2 = productRepository.save(ProductEntity.builder()
                .name("Smartphone")
                .category("Electronics")
                .price(800.00)
                .stockStatus(true)
                .build());

        TransactionEntity transaction1 = transactionRepository.save(TransactionEntity.builder()
                .customer(customer1)
                .product(product1)
                .date(LocalDateTime.now())
                .paymentMethod("CARD")
                .totalAmount(1200.00)
                .build());

        TransactionEntity transaction2 = transactionRepository.save(TransactionEntity.builder()
                .customer(customer2)
                .product(product2)
                .date(LocalDateTime.now())
                .paymentMethod("CASH")
                .totalAmount(800.00)
                .build());

        return new TestData(admin.getId(), manager.getId(), customer1.getId(), customer2.getId(),
                product1.getId(), product2.getId(), transaction1.getId(), transaction2.getId(),
                adminToken, managerToken);
    }

    public record TestData(UUID adminId, UUID managerId, UUID customer1Id, UUID customer2Id,
                           UUID product1Id, UUID product2Id, UUID transaction1Id, UUID transaction2Id,
                            String adminToken, String managerToken) {}
}