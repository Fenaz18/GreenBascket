// CartResponse.java
package com.example.GreenBascket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Assume Lombok is working and providing boilerplate, or ensure manual constructors/getters/setters are correct
// @Data // If using Lombok
// @NoArgsConstructor // If using Lombok
// @AllArgsConstructor // If using Lombok
public class CartResponse {
    private Long cartId;
    private Long userId; // Changed from String to Long
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Manual All-ArgsConstructor (ensure it's updated to Long userId)
    public CartResponse(Long cartId, Long userId, List<CartItemResponse> items, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Manual No-ArgsConstructor
    public CartResponse() {
    }

    // Manual Getters and Setters (ensure userId is Long)
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; } // Updated return type
    public void setUserId(Long userId) { this.userId = userId; } // Updated parameter type
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}