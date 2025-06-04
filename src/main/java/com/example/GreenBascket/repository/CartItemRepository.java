package com.example.GreenBascket.repository;

import com.example.GreenBascket.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // CHANGE THIS METHOD NAME:
    // From: findByCartIdAndProductId
    // To:   findByCartIdAndProduct_productId
    Optional<CartItem> findByCartIdAndProduct_productId(Long cartId, Long productId);
    // OR:   findByCartAndProduct_productId(Cart cart, Long productId); (if you want to pass the Cart object directly)
}