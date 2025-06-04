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
import jakarta.transaction.Transactional; // Using jakarta for Spring Boot 3+
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
    public CartResponse addProductToCart(Long productId, BigDecimal quantity) {
        User currentUser = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> cartRepository.save(new Cart(currentUser)));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId)); // Consider custom exception

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        if (product.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getAvailableQuantity()); // Consider custom exception
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProduct_productId(cart.getId(), productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            BigDecimal newTotalQuantity = cartItem.getQuantity().add(quantity);
            // Re-check stock with the new total quantity if increasing existing item
            if (product.getAvailableQuantity().compareTo(newTotalQuantity) < 0) {
                throw new RuntimeException("Not enough stock to add more for product: " + product.getName() + ". Available: " + product.getAvailableQuantity() + ", Requested total: " + newTotalQuantity);
            }
            cartItem.setQuantity(newTotalQuantity);
        } else {
            cartItem = new CartItem(cart, product, quantity, product.getPrice());
            cart.getCartItems().add(cartItem); // Add to the collection in the parent Cart entity
        }
        cartItemRepository.save(cartItem); // Save or update the cart item

        cart.recalculateTotalAmount(); // Recalculate and update cart's total amount
        cartRepository.save(cart); // Save the updated cart

        return mapCartToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItemQuantity(Long cartItemId, BigDecimal newQuantity) {
        User currentUser = authService.getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId)); // ResourceNotFoundException

        // Ensure the cart item belongs to the authenticated user's cart
        if (!cartItem.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to current user."); // AccessDeniedException
        }

        if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            // If new quantity is 0 or less, recommend removing the item
            throw new IllegalArgumentException("Quantity must be positive. To remove, use the delete endpoint.");
        }

        Product product = cartItem.getProduct();

        // Check if there's enough stock for the new total quantity
        if (product.getAvailableQuantity().compareTo(newQuantity) < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName() + ". Available: " + product.getAvailableQuantity() + ", Requested: " + newQuantity); // Custom exception
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        cart.recalculateTotalAmount(); // Recalculate and update cart's total amount
        cartRepository.save(cart); // Save the updated cart

        return mapCartToCartResponse(cart);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        User currentUser = authService.getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId)); // ResourceNotFoundException

        // Ensure the cart item belongs to the authenticated user's cart
        if (!cartItem.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to current user."); // AccessDeniedException
        }

        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem); // Remove from the collection in the parent Cart entity
        cartItemRepository.delete(cartItem); // Delete the item from the repository

        cart.recalculateTotalAmount(); // Recalculate and update cart's total amount
        cartRepository.save(cart); // Save the updated cart
    }

    public CartResponse getUserCart() {
        User currentUser = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found for user.")); // ResourceNotFoundException
        return mapCartToCartResponse(cart);
    }

    @Transactional // Helper method for OrderService
    public void validateCartAndDecrementStock(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot process empty cart."); // Consider InvalidOrderException
        }

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getAvailableQuantity().compareTo(item.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getAvailableQuantity() + ", Requested: " + item.getQuantity()); // InvalidOrderException
            }
            product.setAvailableQuantity(product.getAvailableQuantity().subtract(item.getQuantity()));
            productRepository.save(product);
        }
    }

    private CartResponse mapCartToCartResponse(Cart cart) {
        cart.recalculateTotalAmount(); // Ensure total is up-to-date before mapping

        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getPricePerUnit().multiply(item.getQuantity())
                ))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                cart.getUser().getUserId(), // Using getUserId()
                itemResponses,
                cart.getTotalAmount(), // Use the calculated total from the Cart entity
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}