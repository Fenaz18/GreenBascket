package com.example.GreenBascket.repository;

import com.example.GreenBascket.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Finds all products associated with a specific farmer's userId
    // Assumes your User entity has a 'userId' field of type String
    List<Product> findByFarmerUserId(String farmerId);
}