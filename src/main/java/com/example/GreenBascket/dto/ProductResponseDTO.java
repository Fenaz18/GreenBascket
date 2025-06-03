package com.example.GreenBascket.dto;

import java.math.BigDecimal; // <-- Import BigDecimal
import java.time.LocalDateTime;

public class ProductResponseDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal quantity; // <-- Change to BigDecimal
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor (if manually created)
    public ProductResponseDTO(Long productId, String name, String description, BigDecimal price, BigDecimal quantity, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductResponseDTO() {
    }

    // Getters and Setters (ensure quantity is BigDecimal)
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQuantity() { return quantity; } // <-- Updated
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; } // <-- Updated
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}