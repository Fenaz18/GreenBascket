package com.example.GreenBascket.dto;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal; // <-- Import BigDecimal

public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal quantity; // <-- Change to BigDecimal
    private MultipartFile imageFile; // For file upload

    // Constructor (if manually created)
    public ProductRequestDTO(String name, String description, BigDecimal price, BigDecimal quantity, MultipartFile imageFile) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageFile = imageFile;
    }

    public ProductRequestDTO() {
    }

    // Getters and Setters (ensure quantity is BigDecimal)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQuantity() { return quantity; } // <-- Updated
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; } // <-- Updated
    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
}