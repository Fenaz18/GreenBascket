// com.example.GreenBascket.controller.CartController
package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.CartResponse;
import com.example.GreenBascket.dto.CartItemRequest; // Import the new request DTO
import com.example.GreenBascket.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // --- OPTION 1: Keep using @RequestParam (requires only small change to CartItemRequest DTO) ---
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(@RequestParam Long productId, @RequestParam BigDecimal quantity) {
        // Your service method already expects productId and BigDecimal quantity, so this is fine.
        CartResponse cartResponse = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    // --- OPTION 2: Use @RequestBody for a more structured request (requires changes here and in service) ---
    /*
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(@RequestBody CartItemRequest request) {
        // You would need to update your CartService.addProductToCart method signature
        // to accept a CartItemRequest object, or extract productId and quantity from it.
        CartResponse cartResponse = cartService.addProductToCart(request.getProductId(), request.getQuantity());
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }
    */

    @GetMapping
    public ResponseEntity<CartResponse> getUserCart() {
        CartResponse cartResponse = cartService.getUserCart();
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam BigDecimal newQuantity) {
        CartResponse cartResponse = cartService.updateCartItemQuantity(cartItemId, newQuantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}