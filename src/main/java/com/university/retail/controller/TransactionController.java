package com.university.retail.controller;

import com.university.retail.domain.TransactionDTO;
import com.university.retail.exception.TransactionNotFoundException;
import com.university.retail.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "APIs for managing transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves a list of all recorded transactions.")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAllTransactions());
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Registers a new transaction.")
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves transaction details by ID.")
    public ResponseEntity<TransactionDTO> getTransactionById(@Parameter(description = "Transaction ID") @PathVariable UUID id) throws TransactionNotFoundException {
        return ResponseEntity.ok(transactionService.findTransactionById(id));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction.")
    public ResponseEntity<Void> deleteTransaction(@Parameter(description = "Transaction ID") @PathVariable UUID id) throws TransactionNotFoundException {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
