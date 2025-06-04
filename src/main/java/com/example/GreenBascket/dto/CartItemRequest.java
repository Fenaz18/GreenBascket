package com.example.GreenBascket.dto;

// REMOVE ALL LOMBOK IMPORTS AND ANNOTATIONS
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// REMOVE LOMBOK ANNOTATIONS: @Data, @NoArgsConstructor, @AllArgsConstructor
public class CartItemRequest {
    private Long productId;
    private BigDecimal quantity;

    // --- Manual No-Argument Constructor (Essential for JSON deserialization) ---
    public CartItemRequest() {
    }

    // --- Manual All-Argument Constructor ---
    public CartItemRequest(Long productId, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // --- Manual Getters ---
    public Long getProductId() {
        return productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    // --- Manual Setters ---
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}