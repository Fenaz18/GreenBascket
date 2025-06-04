package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    // farmerId is now Long
    ProductResponseDTO addProduct(Long farmerId, ProductRequestDTO productRequest);

    // farmerId is now Long
    List<ProductResponseDTO> getProductsByFarmer(Long farmerId);

    // farmerId is now Long
    void deleteProduct(Long farmerId, Long productId);

    // farmerId is now Long
    ProductResponseDTO updateProduct(Long farmerId, Long productId, ProductRequestDTO productRequest);

    List<ProductResponseDTO> getAllProducts();
}