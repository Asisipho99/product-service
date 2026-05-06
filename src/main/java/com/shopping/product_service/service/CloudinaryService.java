package com.shopping.product_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            // Upload to a folder called "shopping-products" in your Cloudinary account
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "shopping-products",
                            "resource_type", "image"
                    )
            );
            // Return the secure URL of the uploaded image
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract the public ID from the URL to delete it
            // URL format: https://res.cloudinary.com/cloudname/image/upload/v123/shopping-products/filename.jpg
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Image deletion failed: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // Get everything after /upload/ and remove the file extension
        String[] parts = imageUrl.split("/upload/");
        String withVersion = parts[1]; // e.g. v1234567/shopping-products/filename.jpg
        // Remove version prefix if present
        String withoutVersion = withVersion.replaceFirst("v\\d+/", "");
        // Remove file extension
        return withoutVersion.substring(0, withoutVersion.lastIndexOf("."));
    }
}
