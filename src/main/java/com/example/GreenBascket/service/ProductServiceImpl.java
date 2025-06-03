package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;
import com.example.GreenBascket.exception.ResourceNotFoundException;
import com.example.GreenBascket.model.Product;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.ProductRepository;
import com.example.GreenBascket.repository.UserRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal; // <-- IMPORTANT: Make sure this import is present
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService { // <-- Ensure this line is correct

    private final ProductRepository productRepository;
    private final UserRepo userRepository;
    private final ImageStorageService imageStorageService;

    public ProductServiceImpl(ProductRepository productRepository, UserRepo userRepository, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override // <-- This @Override should now be valid after interface update
    public ProductResponseDTO addProduct(String farmerId, ProductRequestDTO request) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + farmerId));

        Product product = new Product();
        product.setFarmer(farmer);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailableQuantity(request.getQuantity()); // <-- FIX: Use setAvailableQuantity()

        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = imageStorageService.storeFile(imageFile);
                product.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image for product: " + e.getMessage(), e);
            }
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override // <-- This @Override should now be valid
    public List<ProductResponseDTO> getProductsByFarmer(String farmerId) {
        userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + farmerId));

        List<Product> products = productRepository.findByFarmerUserId(farmerId);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override // <-- This @Override should now be valid
    public void deleteProduct(String farmerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!product.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to delete this product.");
        }

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                imageStorageService.deleteFile(product.getImageUrl());
            } catch (IOException e) {
                // Log and handle the error, but don't prevent product deletion if file delete fails
            }
        }
        productRepository.delete(product);
    }

    @Override // <-- This @Override should now be valid
    public ProductResponseDTO updateProduct(String farmerId, Long productId, ProductRequestDTO request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existingProduct.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to update this product.");
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setAvailableQuantity(request.getQuantity()); // <-- FIX: Use setAvailableQuantity()

        MultipartFile newImageFile = request.getImageFile();
        if (newImageFile != null && !newImageFile.isEmpty()) {
            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                try {
                    imageStorageService.deleteFile(existingProduct.getImageUrl());
                } catch (IOException e) {
                    // Log but continue
                }
            }
            try {
                String newImageUrl = imageStorageService.storeFile(newImageFile);
                existingProduct.setImageUrl(newImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store new image for product: " + e.getMessage(), e);
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    @Override // <-- This @Override should now be valid
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getAvailableQuantity(), // <-- FIX: Use getAvailableQuantity()
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}