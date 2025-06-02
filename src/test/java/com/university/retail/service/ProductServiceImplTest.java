package com.university.retail.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.retail.domain.ProductDTO;
import com.university.retail.entity.ProductEntity;
import com.university.retail.exception.ProductNotFoundException;
import com.university.retail.repository.ProductRepository;
import com.university.retail.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;
    private UUID productId;
    private ProductEntity productEntity;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
        productId = UUID.randomUUID();

        productEntity = ProductEntity.builder()
                .id(productId)
                .name("Product A")
                .category("Electronics")
                .price(100.0)
                .stockStatus(true)
                .build();

        productDTO = ProductDTO.builder()
                .name("Product A")
                .category("Electronics")
                .price(100.0)
                .stockStatus(true)
                .build();
    }

    @Test
    void testFindAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(productEntity));

        List<ProductDTO> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product A", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("Product A", result.getName());
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void testFindProductById() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        ProductDTO result = productService.findProductById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).findById(productId);
    }

    @Test
    void testFindProductByIdNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(productId));
    }
}
