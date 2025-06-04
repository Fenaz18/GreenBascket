package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.CartItemResponse;
import com.example.GreenBascket.dto.CartResponse;
import com.example.GreenBascket.model.Cart;
import com.example.GreenBascket.model.CartItem;
import com.example.GreenBascket.model.Product;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.CartItemRepository;
import com.example.GreenBascket.repository.CartRepository;
import com.example.GreenBascket.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, AuthService authService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @Transactional
    public CartResponse addProductToCart(Long productId, BigDecimal quantity) { // Quantity is now BigDecimal
        User currentUser = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> cartRepository.save(new Cart(currentUser)));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Use BigDecimal.compareTo for quantity comparison
        if (product.getAvailableQuantity().compareTo(quantity) < 0) { // Changed to getAvailableQuantity()
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getAvailableQuantity());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProduct_productId(cart.getId(), productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            // Add new quantity to existing quantity
            cartItem.setQuantity(cartItem.getQuantity().add(quantity));
        } else {
            cartItem = new CartItem(cart, product, quantity, product.getPrice());
            cart.getCartItems().add(cartItem);
        }
        cartItemRepository.save(cartItem);

        return mapCartToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItemQuantity(Long cartItemId, BigDecimal newQuantity) { // New method for update
        User currentUser = authService.getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));

        if (!cartItem.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to current user.");
        }

        if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be positive. Use remove option to delete.");
        }

        Product product = cartItem.getProduct();
        BigDecimal oldQuantity = cartItem.getQuantity();
        BigDecimal quantityDifference = newQuantity.subtract(oldQuantity);

        // Check if there's enough stock for the *additional* quantity needed
        if (quantityDifference.compareTo(BigDecimal.ZERO) > 0) { // If increasing quantity
            if (product.getAvailableQuantity().compareTo(quantityDifference) < 0) {
                throw new RuntimeException("Insufficient stock to increase quantity. Available: " + product.getAvailableQuantity());
            }
        }
        // No need to revert stock here. The overall stock check for the *final* quantity will happen on order placement.
        // Or, you can choose to reserve stock here if you want real-time stock reduction on cart add/update.
        // For simplicity, we'll reserve/reduce stock only on order placement.

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return mapCartToCartResponse(cartItem.getCart());
    }

    @Transactional
    public void removeCartItem(Long cartItemId) { // New method for removal
        User currentUser = authService.getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));

        if (!cartItem.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to current user.");
        }

        // Remove from the cart's list of items
        cartItem.getCart().getCartItems().remove(cartItem);
        // Delete the item from the repository
        cartItemRepository.delete(cartItem);

        // No need to put stock back here. Stock reduction only happens on order placement.
    }


    public CartResponse getUserCart() {
        User currentUser = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found for user."));
        return mapCartToCartResponse(cart);
    }

    @Transactional // Helper method for OrderService
    public void validateCartAndDecrementStock(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            // Use BigDecimal.compareTo for quantity comparison
            if (product.getAvailableQuantity().compareTo(item.getQuantity()) < 0) { // Changed to getAvailableQuantity()
                throw new RuntimeException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getAvailableQuantity() + ", Requested: " + item.getQuantity());
            }
            // Use BigDecimal.subtract for quantity reduction
            product.setAvailableQuantity(product.getAvailableQuantity().subtract(item.getQuantity())); // Changed to setAvailableQuantity()
            productRepository.save(product);
        }
    }

    private CartResponse mapCartToCartResponse(Cart cart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getPricePerUnit().multiply(item.getQuantity()); // BigDecimal multiplication
                    return new CartItemResponse(
                            item.getId(),
                            item.getProduct().getProductId(),
                            item.getProduct().getName(),
                            item.getProduct().getImageUrl(),
                            item.getQuantity(), // Now BigDecimal
                            item.getPricePerUnit(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());

        totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getUser().getUserId(),
                itemResponses,
                totalAmount,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}