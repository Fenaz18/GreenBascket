package com.example.GreenBascket.model;

import jakarta.persistence.*;
// Removed Lombok annotations (if they were present)
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
// Removed Lombok annotations
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    // @ToString.Exclude // Remove if you removed Lombok completely
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // @ToString.Exclude // Remove if you removed Lombok completely
    private Product product;

    @Column(precision = 10, scale = 3) // Changed to BigDecimal for decimal quantities
    private BigDecimal quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    // Manual No-ArgsConstructor
    public CartItem() {
    }

    // Manual Constructor (update parameter type)
    public CartItem(Cart cart, Product product, BigDecimal quantity, BigDecimal pricePerUnit) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Manual Getters and Setters (ensure all are present and updated for BigDecimal quantity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getQuantity() { return quantity; } // Updated return type
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; } // Updated parameter type

    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}