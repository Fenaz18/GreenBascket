package com.example.GreenBascket.repository;// In com.example.GreenBascket.repository.CartItemRepository.java

import com.example.GreenBascket.model.Cart; // Import Cart entity
import com.example.GreenBascket.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Changed from Optional, as you expect a list
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProduct_productId(Long cartId, Long productId);

    // Add this method to fetch all items for a given cart
    List<CartItem> findByCart(Cart cart); // This is very common and effective
    // Or, if you prefer by cartId directly:
    // List<CartItem> findByCart_cartId(Long cartId); // Assumes CartItem has a 'cart' field with 'cartId' property
}