package com.example.GreenBascket.repository;

import com.example.GreenBascket.model.Order;
import com.example.GreenBascket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
}