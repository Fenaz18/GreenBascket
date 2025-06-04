package com.example.GreenBascket.model;

import jakarta.persistence.*;
// Removed Lombok annotations (if they were present)
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
// Removed Lombok annotations
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    // @ToString.Exclude // Remove if you removed Lombok completely
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // @ToString.Exclude // Remove if you removed Lombok completely
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_image_url")
    private String productImageUrl;

    @Column(precision = 10, scale = 3) // Changed to BigDecimal for decimal quantities
    private BigDecimal quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    // Manual No-ArgsConstructor
    public OrderItem() {
    }

    // Manual Constructor (update parameter type)
    public OrderItem(Order order, Product product, String productName, String productImageUrl, BigDecimal quantity, BigDecimal pricePerUnit) {
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Manual Getters and Setters (ensure all are present and updated for BigDecimal quantity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public BigDecimal getQuantity() { return quantity; } // Updated return type
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; } // Updated parameter type

    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}