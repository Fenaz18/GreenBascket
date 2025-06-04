package com.example.GreenBascket.dto;

import com.example.GreenBascket.model.OrderStatus; // Import OrderStatus Enum
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private String userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus; // Changed to OrderStatus Enum
    private String shippingAddress;
    private List<OrderItemResponse> orderItems;

    public OrderResponse(Long orderId, String userId, LocalDateTime orderDate, BigDecimal totalAmount, OrderStatus orderStatus, String shippingAddress, List<OrderItemResponse> orderItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.orderItems = orderItems;
    }

    public OrderResponse() {
    }

    // Getters and Setters (ensure orderStatus is OrderStatus Enum)
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getOrderStatus() { return orderStatus; } // Updated
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; } // Updated
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
}