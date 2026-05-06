package com.shopping.product_service.service;

import com.shopping.product_service.dto.ProductRequest;
import com.shopping.product_service.entity.Product;
import com.shopping.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product create(ProductRequest request, MultipartFile image) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());

        // Upload image to Cloudinary if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image);
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request, MultipartFile image) {
        Product product = getById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());

        // If a new image is uploaded, delete old one and upload new one
        if (image != null && !image.isEmpty()) {
            if (product.getImageUrl() != null) {
                cloudinaryService.deleteImage(product.getImageUrl());
            }
            String imageUrl = cloudinaryService.uploadImage(image);
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = getById(id);

        // Delete image from Cloudinary when product is deleted
        if (product.getImageUrl() != null) {
            cloudinaryService.deleteImage(product.getImageUrl());
        }

        productRepository.deleteById(id);
    }
}