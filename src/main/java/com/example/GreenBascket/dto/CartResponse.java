package com.example.GreenBascket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private String userId; // Based on the 'found' types, this is String. Confirm with your User model.
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Manual All-ArgsConstructor matching the 'found' types in your error
    public CartResponse(Long cartId, String userId, List<CartItemResponse> items, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Manual No-ArgsConstructor (often needed for JSON deserialization)
    public CartResponse() {
    }

    // Manual Getters and Setters
    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}