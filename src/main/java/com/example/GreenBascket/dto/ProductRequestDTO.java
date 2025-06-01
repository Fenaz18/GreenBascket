package com.example.GreenBascket.dto;

import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile; // Important import

public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private MultipartFile imageFile; // Field to receive the uploaded image

    // Constructors (optional, but good practice)
    public ProductRequestDTO() {
    }

    public ProductRequestDTO(String name, String description, BigDecimal price, Integer quantity, MultipartFile imageFile) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageFile = imageFile;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}