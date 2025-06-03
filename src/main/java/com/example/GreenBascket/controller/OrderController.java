package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.OrderResponse;
import com.example.GreenBascket.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<OrderResponse> placeOrder() {
        // Order is placed based on the current user's cart
        OrderResponse orderResponse = orderService.placeOrder();
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<OrderResponse> orders = orderService.getMyOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderDetails(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}