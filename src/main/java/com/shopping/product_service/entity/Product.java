package com.shopping.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
// Override DELETE to set deleted_at instead of removing the row
@SQLDelete(sql = "UPDATE products SET deleted_at = NOW() WHERE product_id = ?")
// Automatically filter out soft-deleted rows on every query
@SQLRestriction("deleted_at IS NULL")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String category;

    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Soft delete timestamp — null means active, non-null means deleted
    private LocalDateTime deletedAt;
}
