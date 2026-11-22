package com.shopping.product_service.controller;

import com.shopping.product_service.config.JwtService;
import com.shopping.product_service.config.SecurityConfig;
import com.shopping.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=test-secret-value-long-enough-for-hs256-signing-0123456789")
class ProductCategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    // config.JwtService is a @Component and is not scanned by the web slice
    @MockitoBean
    private JwtService jwtService;

    @Test
    void returnsBareJsonArrayOfCategoriesWithoutAuthentication() throws Exception {
        when(productService.getCategories()).thenReturn(List.of("Books", "Electronics"));

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Books\",\"Electronics\"]", true));

        // Regression guard on the /{id} route collision: "categories" must not be an id
        verify(productService).getCategories();
        verify(productService, never()).getById(anyLong());
    }
}
