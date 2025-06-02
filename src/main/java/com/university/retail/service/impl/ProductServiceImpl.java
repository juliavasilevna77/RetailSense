package com.university.retail.service.impl;

import com.university.retail.domain.ProductDTO;
import com.university.retail.entity.ProductEntity;
import com.university.retail.exception.ProductNotFoundException;
import com.university.retail.repository.ProductRepository;
import com.university.retail.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductEntity product = ProductEntity.builder()
                .name(productDTO.getName())
                .category(productDTO.getCategory())
                .price(productDTO.getPrice())
                .stockStatus(productDTO.getStockStatus())
                .build();

        ProductEntity savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }

    @Override
    public ProductDTO findProductById(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    @Override
    public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product = product.toBuilder()
                .name(productDTO.getName())
                .category(productDTO.getCategory())
                .price(productDTO.getPrice())
                .stockStatus(productDTO.getStockStatus())
                .build();

        ProductEntity updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO mapToDTO(ProductEntity product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockStatus(product.getStockStatus())
                .build();
    }
}
