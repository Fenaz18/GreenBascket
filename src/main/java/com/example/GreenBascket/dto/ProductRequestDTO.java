package com.example.GreenBascket.dto;


import java.math.BigDecimal;

public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;


    // 1. ADD THIS NO-ARGUMENT CONSTRUCTOR (THIS IS THE PRIMARY FIX)
    public ProductRequestDTO() {
        // Default constructor needed by Jackson for deserialization
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public ProductRequestDTO(String description, String imageUrl, String name, Integer quantity, BigDecimal price) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

