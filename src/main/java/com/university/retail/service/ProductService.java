package com.university.retail.service;

import com.university.retail.domain.ProductDTO;
import com.university.retail.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO findProductById(UUID id) throws ProductNotFoundException;
    ProductDTO updateProduct(UUID id, ProductDTO productDTO) throws ProductNotFoundException;
    void deleteProduct(UUID id) throws ProductNotFoundException;
}
