package com.shopping.product_service.repository;

import com.shopping.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Paginated full listing — @SQLRestriction filters deleted rows automatically
    Page<Product> findAll(Pageable pageable);

    // Search by name (case-insensitive) — deleted rows excluded automatically
    List<Product> findByNameContainingIgnoreCase(String name);

    // Filter by category — deleted rows excluded automatically
    List<Product> findByCategory(String category);

    // Distinct categories of active products — the deletedAt IS NULL predicate is written
    // explicitly even though @SQLRestriction would add it, so the guarantee is visible here
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.deletedAt IS NULL AND p.category IS NOT NULL AND p.category <> '' ORDER BY p.category ASC")
    List<String> findDistinctCategories();

    // Find including soft-deleted — used by admin to restore a product
    @Query(value = "SELECT * FROM products WHERE product_id = :id", nativeQuery = true)
    Optional<Product> findByIdIncludingDeleted(@Param("id") Long id);
}
