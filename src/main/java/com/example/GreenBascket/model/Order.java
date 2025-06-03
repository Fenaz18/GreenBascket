package com.example.GreenBascket.model;

import jakarta.persistence.*;
// Removed Lombok annotations (if they were present)
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
// Removed Lombok annotations
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // @ToString.Exclude // Remove if you removed Lombok completely
    private User user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING) // Map Enum to String in DB
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // Changed to OrderStatus Enum

    @Column(name = "shipping_address")
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Manual No-ArgsConstructor
    public Order() {
    }

    // Manual Constructor (add if needed, e.g., for initial creation)
    public Order(User user, LocalDateTime orderDate, BigDecimal totalAmount, OrderStatus orderStatus, String shippingAddress) {
        this.user = user;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
    }


    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        if (this.orderStatus == null) {
            this.orderStatus = OrderStatus.PENDING; // Default status using Enum
        }
    }

    // Manual Getters and Setters (ensure all are present and updated for OrderStatus)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getOrderStatus() { return orderStatus; } // Updated return type
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; } // Updated parameter type

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}