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


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepo userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepo userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductResponseDTO addProduct(String farmerId, ProductRequestDTO request) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        Product product = new Product();
        product.setFarmer(farmer);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getProductsByFarmer(String farmerId) {
        List<Product> products = productRepository.findByFarmerUserId(farmerId);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(String farmerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getFarmer().getUserId().equals(farmerId)) {
            throw new AccessDeniedException("Not authorized to delete this product");
        }

        productRepository.delete(product);
    }

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


