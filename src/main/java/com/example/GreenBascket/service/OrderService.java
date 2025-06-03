package com.example.GreenBascket.service;

import com.example.GreenBascket.dto.OrderItemResponse;
import com.example.GreenBascket.dto.OrderResponse;
import com.example.GreenBascket.model.Cart;
import com.example.GreenBascket.model.CartItem;
import com.example.GreenBascket.model.Order;
import com.example.GreenBascket.model.OrderItem;
import com.example.GreenBascket.model.OrderStatus; // Import OrderStatus Enum
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
                .orElseThrow(() -> new RuntimeException("Cart is empty or not found for user."));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place order, cart is empty.");
        }

        // Validate stock and decrement product quantities via CartService
        cartService.validateCartAndDecrementStock(cart);

        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CONFIRMED); // Use Enum
        order.setShippingAddress(currentUser.getLocation() != null ? currentUser.getLocation() : "Default Address");

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getProduct().getName(),
                    cartItem.getProduct().getImageUrl(),
                    cartItem.getQuantity(), // Quantity is now BigDecimal
                    cartItem.getPricePerUnit()
            );
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(cartItem.getPricePerUnit().multiply(cartItem.getQuantity())); // BigDecimal multiplication
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(order.getOrderItems());

        cart.getCartItems().clear(); // Clear the user's cart
        cartRepository.save(cart);

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
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: Order does not belong to current user.");
        }
        return mapOrderToOrderResponse(order);
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getProductId(),
                        item.getProductName(),
                        item.getProductImageUrl(),
                        item.getQuantity(), // Quantity is now BigDecimal
                        item.getPricePerUnit(),
                        item.getPricePerUnit().multiply(item.getQuantity()) // BigDecimal multiplication
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getUserId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getOrderStatus(), // Using OrderStatus Enum
                order.getShippingAddress(),
                itemResponses
        );
    }
}