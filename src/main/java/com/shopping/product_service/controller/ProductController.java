package com.shopping.product_service.controller;

import com.shopping.product_service.dto.ProductRequest;
import com.shopping.product_service.entity.Product;
import com.shopping.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products
    // GET /api/products?name=phone
    // GET /api/products?category=electronics
    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {

        if (name != null) {
            return ResponseEntity.ok(productService.searchByName(name));
        }
        if (category != null) {
            return ResponseEntity.ok(productService.getByCategory(category));
        }
        return ResponseEntity.ok(productService.getAll());
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // POST /api/products  — multipart/form-data to support image upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> create(
            @Valid @ModelAttribute ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, image));
    }

    // PUT /api/products/{id}
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(productService.update(id, request, image));
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}