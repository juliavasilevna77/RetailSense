package com.university.retail.service.impl;

import com.university.retail.domain.TransactionDTO;
import com.university.retail.entity.CustomerEntity;
import com.university.retail.entity.ProductEntity;
import com.university.retail.entity.TransactionEntity;
import com.university.retail.exception.CustomerNotFoundException;
import com.university.retail.exception.ProductNotFoundException;
import com.university.retail.exception.TransactionNotFoundException;
import com.university.retail.repository.CustomerRepository;
import com.university.retail.repository.ProductRepository;
import com.university.retail.repository.TransactionRepository;
import com.university.retail.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public List<TransactionDTO> findAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        CustomerEntity customer = customerRepository.findById(transactionDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + transactionDTO.getCustomerId()));
        ProductEntity product = productRepository.findById(transactionDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + transactionDTO.getProductId()));

        TransactionEntity transaction = TransactionEntity.builder()
                .customer(customer)
                .product(product)
                .date(transactionDTO.getDate())
                .paymentMethod(transactionDTO.getPaymentMethod())
                .totalAmount(transactionDTO.getTotalAmount())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    @Override
    public TransactionDTO findTransactionById(UUID id) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
        return mapToDTO(transaction);
    }

    @Override
    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private TransactionDTO mapToDTO(TransactionEntity transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .productId(transaction.getProduct().getId())
                .date(transaction.getDate())
                .paymentMethod(transaction.getPaymentMethod())
                .totalAmount(transaction.getTotalAmount())
                .build();
    }
}
