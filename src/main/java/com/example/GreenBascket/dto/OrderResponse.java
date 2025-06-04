// OrderResponse.java
package com.example.GreenBascket.dto;

import com.example.GreenBascket.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Assume Lombok is working and providing boilerplate, or ensure manual constructors/getters/setters are correct
// @Data // If using Lombok
// @NoArgsConstructor // If using Lombok
// @AllArgsConstructor // If using Lombok
public class OrderResponse {
    private Long orderId;
    private Long userId; // Changed from String to Long
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private String shippingAddress;
    private List<OrderItemResponse> orderItems;

    // Manual All-ArgsConstructor (ensure it's updated to Long userId)
    public OrderResponse(Long orderId, Long userId, LocalDateTime orderDate, BigDecimal totalAmount, OrderStatus orderStatus, String shippingAddress, List<OrderItemResponse> orderItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.orderItems = orderItems;
    }

    // Manual No-ArgsConstructor
    public OrderResponse() {
    }

    // Manual Getters and Setters (ensure userId is Long)
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; } // Updated return type
    public void setUserId(Long userId) { this.userId = userId; } // Updated parameter type
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
}