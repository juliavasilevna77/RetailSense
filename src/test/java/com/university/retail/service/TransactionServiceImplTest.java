package com.university.retail.service;

import com.university.retail.domain.TransactionDTO;
import com.university.retail.entity.CustomerEntity;
import com.university.retail.entity.ProductEntity;
import com.university.retail.entity.TransactionEntity;
import com.university.retail.repository.CustomerRepository;
import com.university.retail.repository.ProductRepository;
import com.university.retail.repository.TransactionRepository;
import com.university.retail.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    private TransactionServiceImpl transactionService;
    private TransactionEntity transactionEntity;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepository, customerRepository, productRepository);

        transactionEntity = TransactionEntity.builder()
                .customer(CustomerEntity.builder().build())
                .product(ProductEntity.builder().build())
                .date(LocalDateTime.now())
                .paymentMethod("CARD")
                .totalAmount(120.0)
                .build();

        transactionDTO = TransactionDTO.builder()
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .date(LocalDateTime.now())
                .paymentMethod("CARD")
                .totalAmount(120.0)
                .build();
    }

    @Test
    void testFindAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(transactionEntity));

        List<TransactionDTO> result = transactionService.findAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    void testCreateTransaction() {
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
        when(customerRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(transactionEntity.getCustomer()));
        when(productRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(transactionEntity.getProduct()));

        TransactionDTO result = transactionService.createTransaction(transactionDTO);

        assertNotNull(result);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }
}
