package com.example.GreenBascket.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal quantity; // Changed to BigDecimal
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;

    public OrderItemResponse(Long orderItemId, Long productId, String productName, String productImageUrl, BigDecimal quantity, BigDecimal pricePerUnit, BigDecimal subtotal) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = subtotal;
    }

    public OrderItemResponse() {
    }

    // Getters and Setters (ensure quantity is BigDecimal)
    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public BigDecimal getQuantity() { return quantity; } // Updated
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; } // Updated
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}