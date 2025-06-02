package com.university.retail.controller;

import com.university.retail.domain.CustomerDTO;
import com.university.retail.exception.CustomerNotFoundException;
import com.university.retail.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all registered customers.")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Registers a new customer.")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.createCustomer(customerDTO));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves customer details by ID.")
    public ResponseEntity<CustomerDTO> getCustomerById(@Parameter(description = "Customer ID") @PathVariable UUID id) throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates customer details.")
    public ResponseEntity<CustomerDTO> updateCustomer(@Parameter(description = "Customer ID") @PathVariable UUID id, @Valid @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer.")
    public ResponseEntity<Void> deleteCustomer(@Parameter(description = "Customer ID") @PathVariable UUID id) throws CustomerNotFoundException {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
