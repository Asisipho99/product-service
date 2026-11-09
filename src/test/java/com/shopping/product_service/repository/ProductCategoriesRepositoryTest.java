package com.shopping.product_service.repository;

import com.shopping.product_service.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2test")
class ProductCategoriesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void returnsDistinctCategoriesAndExcludesSoftDeletedProducts() {
        persist("Laptop", "Electronics", null);
        persist("Phone", "Electronics", null);
        persist("Novel", "Books", null);
        persist("Smart Watch", "Gadgets", LocalDateTime.now());

        entityManager.flush();
        entityManager.clear();

        List<String> categories = productRepository.findDistinctCategories();

        // Electronics appears once (distinct) and Gadgets is absent (soft-deleted)
        assertThat(categories).containsExactly("Books", "Electronics");
    }

    @Test
    void excludesBlankCategories() {
        persist("Novel", "Books", null);
        Product blank = persist("Mystery Box", "Other", null);

        entityManager.flush();

        // Bean validation (@NotBlank on category) blocks persisting "" through the entity,
        // so the blank value is written straight to the row the query will read.
        entityManager.getEntityManager()
                .createNativeQuery("UPDATE products SET category = '' WHERE product_id = ?1")
                .setParameter(1, blank.getProductId())
                .executeUpdate();
        entityManager.clear();

        List<String> categories = productRepository.findDistinctCategories();

        assertThat(categories).containsExactly("Books");
    }

    private Product persist(String name, String category, LocalDateTime deletedAt) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal("9.99"));
        product.setDeletedAt(deletedAt);
        return entityManager.persist(product);
    }
}
