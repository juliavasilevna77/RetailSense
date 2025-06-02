package com.university.retail.controller;

import com.university.retail.domain.ProductDTO;
import com.university.retail.exception.ProductNotFoundException;
import com.university.retail.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products.")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new product", description = "Adds a new product to the system.")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves product details by ID.")
    public ResponseEntity<ProductDTO> getProductById(@Parameter(description = "Product ID") @PathVariable UUID id) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates product details.")
    public ResponseEntity<ProductDTO> updateProduct(@Parameter(description = "Product ID") @PathVariable UUID id, @Valid @RequestBody ProductDTO productDTO) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product.")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "Product ID") @PathVariable UUID id) throws ProductNotFoundException {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
