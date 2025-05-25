package com.example.GreenBascket.repository;


import com.example.GreenBascket.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find all products by farmer's userId
    List<Product> findByFarmerUserId(String farmerId);
}


