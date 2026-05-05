package com.shopping.product_service.repository;

import com.shopping.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Filter by category
    List<Product> findByCategory(String category);
}