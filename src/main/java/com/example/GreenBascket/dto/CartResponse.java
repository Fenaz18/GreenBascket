// src/main/java/com/example/GreenBascket/dto/CartResponse.java
package com.example.GreenBascket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> cartItems; // <--- RENAMED THIS FIELD!
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Manual All-ArgsConstructor (update 'items' to 'cartItems')
    public CartResponse(Long cartId, Long userId, List<CartItemResponse> cartItems, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.cartItems = cartItems; // <--- Use the new field name
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Manual No-ArgsConstructor
    public CartResponse() {
    }

    // Manual Getters and Setters (update 'Items' to 'CartItems')
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemResponse> getCartItems() { return cartItems; } // <--- Updated getter
    public void setCartItems(List<CartItemResponse> cartItems) { this.cartItems = cartItems; } // <--- Updated setter
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}