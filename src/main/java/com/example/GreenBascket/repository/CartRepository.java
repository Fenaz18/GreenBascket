package com.example.GreenBascket.repository;


import com.example.GreenBascket.model.Cart;
import com.example.GreenBascket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
