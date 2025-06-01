package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;
import com.example.GreenBascket.exception.ResourceNotFoundException;
import com.example.GreenBascket.model.Product;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.ProductRepository;
import com.example.GreenBascket.repository.UserRepo; // Assuming your User repository is named UserRepo
import org.springframework.security.access.AccessDeniedException; // Good for security
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepo userRepository;
    private final ImageStorageService imageStorageService; // Inject ImageStorageService

    // Constructor Injection
    public ProductServiceImpl(ProductRepository productRepository, UserRepo userRepository, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public ProductResponseDTO addProduct(String farmerId, ProductRequestDTO request) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + farmerId));

        Product product = new Product();
        product.setFarmer(farmer);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        // Handle image upload
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = imageStorageService.storeFile(imageFile);
                product.setImageUrl(imageUrl); // Save the unique filename to the product
            } catch (IOException e) {
                // Log the exception (e.g., using SLF4J and Logback)
                // logger.error("Failed to store image for product " + request.getName(), e);
                throw new RuntimeException("Failed to store image for product: " + e.getMessage(), e);
            }
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getProductsByFarmer(String farmerId) {
        // You might want to ensure the farmer exists first, or handle an empty list if not.
        userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + farmerId));

        List<Product> products = productRepository.findByFarmerUserId(farmerId);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(String farmerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Security check: Ensure the product belongs to the farmer attempting to delete it
        if (!product.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to delete this product.");
        }

        // Before deleting the product record, delete the associated image file
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                imageStorageService.deleteFile(product.getImageUrl());
            } catch (IOException e) {
                // Log and handle the error, but don't prevent product deletion if file delete fails
                // logger.warn("Failed to delete image file " + product.getImageUrl() + " for product " + productId, e);
            }
        }
        productRepository.delete(product);
    }

    @Override
    public ProductResponseDTO updateProduct(String farmerId, Long productId, ProductRequestDTO request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Security check
        if (!existingProduct.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to update this product.");
        }

        // Update fields from DTO
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());

        // Handle image update
        MultipartFile newImageFile = request.getImageFile();
        if (newImageFile != null && !newImageFile.isEmpty()) {
            // Delete old image if it exists
            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                try {
                    imageStorageService.deleteFile(existingProduct.getImageUrl());
                } catch (IOException e) {
                    // Log but continue, don't block update for failed old image deletion
                    // logger.warn("Failed to delete old image file " + existingProduct.getImageUrl() + " for product " + productId, e);
                }
            }
            // Store new image
            try {
                String newImageUrl = imageStorageService.storeFile(newImageFile);
                existingProduct.setImageUrl(newImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store new image for product: " + e.getMessage(), e);
            }
        } else if (request.getImageFile() == null && existingProduct.getImageUrl() != null) {
            // If newImageFile is null (no new file provided) AND old image exists
            // This case handles explicit removal of an image if the frontend sends null/empty file
            // You might need a flag in your DTO or a specific API endpoint for image removal
            // For now, if no file is provided, it leaves the current image as is.
            // If you want to remove the image if 'imageFile' is explicitly sent as null, you'd add:
            // imageStorageService.deleteFile(existingProduct.getImageUrl());
            // existingProduct.setImageUrl(null);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }


    // Helper method to map Product entity to ProductResponseDTO
    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(), // Map the stored image URL
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}