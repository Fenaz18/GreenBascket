package com.example.GreenBascket.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products") // Changed table name for consistency, previously "Product"
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false) // Consistent naming
    private User farmer;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2) // Price per unit (e.g., per KG)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 3) // Changed to BigDecimal for decimal quantities (e.g., 1.5 kg, 0.250 kg)
    private BigDecimal availableQuantity; // Renamed for clarity: stock quantity

    @Column(name = "image_url") // Consistent naming
    private String imageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Manual No-ArgsConstructor
    public Product() {
    }

    // Manual All-ArgsConstructor (adjust if you need a different one)
    public Product(User farmer, String name, String description, BigDecimal price, BigDecimal availableQuantity, String imageUrl) {
        this.farmer = farmer;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.imageUrl = imageUrl;
    }

    // Manual Getters and Setters (ensure all are present and updated for BigDecimal quantities)
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getAvailableQuantity() { return availableQuantity; } // Updated return type
    public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; } // Updated parameter type

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


}