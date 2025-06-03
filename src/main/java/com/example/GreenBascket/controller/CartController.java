package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.CartResponse;
import com.example.GreenBascket.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Import BigDecimal

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(@RequestParam Long productId, @RequestParam BigDecimal quantity) { // Quantity is BigDecimal
        CartResponse cartResponse = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getUserCart() {
        CartResponse cartResponse = cartService.getUserCart();
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PutMapping("/items/{cartItemId}") // New endpoint for updating quantity
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam BigDecimal newQuantity) { // New quantity is BigDecimal
        CartResponse cartResponse = cartService.updateCartItemQuantity(cartItemId, newQuantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/items/{cartItemId}") // New endpoint for removing item
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}