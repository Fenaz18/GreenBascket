package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO addProduct(String farmerId, ProductRequestDTO productRequest);
    List<ProductResponseDTO> getProductsByFarmer(String farmerId);
    void deleteProduct(String farmerId, Long productId);
    // You might want to add an update method too
    ProductResponseDTO updateProduct(String farmerId, Long productId, ProductRequestDTO productRequest);
    List<ProductResponseDTO> getAllProducts(); // <--- Add this line

}