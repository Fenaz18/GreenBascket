package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    // Add product to the system, linked to a specific farmer.
    // productRequest contains details including quantity as BigDecimal.
    ProductResponseDTO addProduct(String farmerId, ProductRequestDTO productRequest);

    // Retrieve all products listed by a specific farmer.
    List<ProductResponseDTO> getProductsByFarmer(String farmerId);

    // Delete a product, ensuring it belongs to the specified farmer.
    void deleteProduct(String farmerId, Long productId);

    // Update an existing product, ensuring it belongs to the specified farmer.
    // productRequest contains updated details including quantity as BigDecimal.
    ProductResponseDTO updateProduct(String farmerId, Long productId, ProductRequestDTO productRequest);

    // Retrieve all products available in the system.
    List<ProductResponseDTO> getAllProducts();
}