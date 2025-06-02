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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepo userRepository;
    private final ImageStorageService imageStorageService;

    public ProductServiceImpl(ProductRepository productRepository, UserRepo userRepository, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    // ... (Your existing addProduct, getProductsByFarmer, deleteProduct, updateProduct methods) ...

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

    @Override
    public List<ProductResponseDTO> getProductsByFarmer(String farmerId) {
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

    @Override
    public ProductResponseDTO updateProduct(String farmerId, Long productId, ProductRequestDTO request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existingProduct.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to update this product.");
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());

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

    // --- NEW METHOD IMPLEMENTATION ---
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll(); // Fetch all products
        return products.stream()
                .map(this::mapToResponse) // Map each Product entity to ProductResponseDTO
                .collect(Collectors.toList());
    }
    // ---------------------------------

    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}