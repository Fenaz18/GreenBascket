package com.example.GreenBascket.repository;

import com.example.GreenBascket.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Correct way to query by nested property (productId within the Product entity)
    Optional<CartItem> findByCartIdAndProduct_productId(Long cartId, Long productId);

    // Alternatively, if you want to pass the Cart object directly and query by product ID
    // Optional<CartItem> findByCartAndProduct_productId(Cart cart, Long productId);
}