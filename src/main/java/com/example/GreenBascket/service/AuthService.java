package com.example.GreenBascket.service;

import com.example.GreenBascket.model.User;
import com.example.GreenBascket.repository.UserRepo;
import com.example.GreenBascket.repository.UserRepo; // Assuming you have a UserRepository
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepo userRepository;

    public AuthService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Assuming your UserDetailsService returns a UserDetails implementation that is your User entity
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        // If your UserDetails is not directly your User entity (e.g., custom UserDetailsImpl),
        // you'd typically get the username/email and then load the user from the repository.
        // Example:
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByEmail(username) // Assuming findByEmail in UserRepository
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB"));
        }
        throw new RuntimeException("User not authenticated");
    }
}