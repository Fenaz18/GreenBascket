package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.OrderItemResponse;
import com.example.GreenBascket.dto.OrderResponse;
import com.example.GreenBascket.model.Cart;
import com.example.GreenBascket.model.CartItem;
import com.example.GreenBascket.model.Order;
import com.example.GreenBascket.model.OrderItem;
import com.example.GreenBascket.model.OrderStatus;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.CartRepository;
import com.example.GreenBascket.repository.OrderItemRepository;
import com.example.GreenBascket.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AuthService authService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        CartRepository cartRepository, CartService cartService, AuthService authService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.authService = authService;
    }

    @Transactional
    public OrderResponse placeOrder() {
        User currentUser = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart is empty or not found for user.")); // ResourceNotFoundException

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place order, cart is empty."); // IllegalArgumentException or custom InvalidOrderException
        }

        // Validate stock and decrement product quantities via CartService
        cartService.validateCartAndDecrementStock(cart);

        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        // Assuming user's location is shipping address. Adjust if you have a separate address entity.
        order.setShippingAddress(currentUser.getLocation() != null ? currentUser.getLocation() : "Default Address");

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getProduct().getName(), // Pass product name
                    cartItem.getProduct().getImageUrl(), // Pass product image URL
                    cartItem.getQuantity(),
                    cartItem.getPricePerUnit()
            );
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(cartItem.getPricePerUnit().multiply(cartItem.getQuantity()));
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(order.getOrderItems()); // Save order items after saving the order

        cart.getCartItems().clear(); // Clear the user's cart after order is placed
        cart.recalculateTotalAmount(); // Update cart total to zero
        cartRepository.save(cart); // Save the now empty cart

        return mapOrderToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {
        User currentUser = authService.getAuthenticatedUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(currentUser);
        return orders.stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetails(Long orderId) {
        User currentUser = authService.getAuthenticatedUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId)); // ResourceNotFoundException

        // Ensure the order belongs to the authenticated user
        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Order does not belong to current user."); // AccessDeniedException
        }
        return mapOrderToOrderResponse(order);
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct() != null ? item.getProduct().getProductId() : null, // Handle null product if detached/deleted
                        item.getProductName(),
                        item.getProductImageUrl(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getPricePerUnit().multiply(item.getQuantity())
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getUserId(), // Using getUserId()
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getShippingAddress(),
                itemResponses
        );
    }
}