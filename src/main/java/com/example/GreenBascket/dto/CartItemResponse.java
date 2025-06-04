package com.example.GreenBascket.dto;

import java.math.BigDecimal;

// No Lombok annotations here, already manually implemented
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;

    public CartItemResponse(Long cartItemId, Long productId, String productName, String productImageUrl, BigDecimal quantity, BigDecimal pricePerUnit, BigDecimal subtotal) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = subtotal;
    }

    public CartItemResponse() {
    }

    // Getters and Setters
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}