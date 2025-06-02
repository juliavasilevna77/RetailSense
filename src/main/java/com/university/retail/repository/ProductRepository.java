package com.university.retail.repository;

import com.university.retail.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByCategory(String category);
    List<ProductEntity> findByStockStatusTrue();
}
