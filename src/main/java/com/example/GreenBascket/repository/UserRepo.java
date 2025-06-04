package com.example.GreenBascket.repository;

import com.example.GreenBascket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> { // FIX: Changed String to Long
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUserId(Long userId); // This method is redundant if userId is the primary key and named 'Id'
    // You can use findById(Long userId) directly from JpaRepository
}