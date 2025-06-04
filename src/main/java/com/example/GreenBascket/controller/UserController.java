package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.*;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.security.JwtUtil;
import com.example.GreenBascket.security.UserPrincipal;
import com.example.GreenBascket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // Apply CORS globally to the controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- CRITICAL CHANGE START ---
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        // Your frontend already checks password match, so this can remain commented or removed.
        // if (!dto.getPassword().equals(dto.getConfirmPassword())) {
        //     return ResponseEntity.badRequest().body(new MessageResponseDTO("Passwords do not match"));
        // }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole()); // Assuming dto.getRole() returns the Role enum
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());

        User registeredUser = userService.registerUser(user); // Get the saved user object

        // Generate JWT token for the newly registered user
        // You might generate it based on email (as in login) or user ID
        // Ensure userService.loadUserByUsername can load the user *after* registration
        // or directly use the user's email/ID to generate token.
        UserDetails userDetails = userService.loadUserByUsername(registeredUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        // Construct and return the UserResponseDTO
        UserResponseDTO responseDto = new UserResponseDTO(
                registeredUser.getUserId(),
                registeredUser.getName(),
                registeredUser.getEmail(),
                registeredUser.getRole(), // Ensure this is the correct Role enum
                registeredUser.getPhone(),
                registeredUser.getLocation(),
                token // Include the generated token
        );

        return ResponseEntity.ok(responseDto);
    }
    // --- CRITICAL CHANGE END ---


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        LoginResponseDTO response = new LoginResponseDTO(
                user.getUserId(), user.getName(), user.getEmail(),
                user.getRole(), user.getLocation(), user.getPhone(), token
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to get the profile of the currently authenticated user.
     * Maps to GET /api/users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // Ensure user is authenticated
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponseDTO profile = userService.getUserProfile(userPrincipal.getUserId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Endpoint to update the profile of the currently authenticated user.
     * Maps to PUT /api/users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()") // Ensure user is authenticated
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserProfileRequestDTO requestDTO) {
        UserProfileResponseDTO updatedProfile = userService.updateUserProfile(userPrincipal.getUserId(), requestDTO);
        return ResponseEntity.ok(updatedProfile);
    }
}