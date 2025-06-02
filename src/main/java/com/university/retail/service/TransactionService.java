package com.university.retail.service;

import com.university.retail.domain.TransactionDTO;
import com.university.retail.exception.TransactionNotFoundException;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    List<TransactionDTO> findAllTransactions();
    TransactionDTO createTransaction(TransactionDTO transactionDTO);
    TransactionDTO findTransactionById(UUID id) throws TransactionNotFoundException;
    void deleteTransaction(UUID id) throws TransactionNotFoundException;
}
