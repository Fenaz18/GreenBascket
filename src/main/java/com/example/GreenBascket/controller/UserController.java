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
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());

        User registeredUser = userService.registerUser(user);

        UserDetails userDetails = userService.loadUserByUsername(registeredUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        UserResponseDTO responseDto = new UserResponseDTO(
                String.valueOf(registeredUser.getUserId()), // Convert Long to String for DTO
                registeredUser.getName(),
                registeredUser.getEmail(),
                registeredUser.getRole(),
                registeredUser.getPhone(),
                registeredUser.getLocation(),
                token
        );

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        LoginResponseDTO response = new LoginResponseDTO(
                String.valueOf(user.getUserId()), // Convert Long to String for DTO
                user.getName(), user.getEmail(),
                user.getRole(), user.getLocation(), user.getPhone(), token
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to get the profile of the currently authenticated user.
     * Maps to GET /api/users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // userPrincipal.getUserId() returns Long (from your UserPrincipal class).
        // UserService.getUserProfile now expects a Long, so pass it directly.
        UserProfileResponseDTO profile = userService.getUserProfile(userPrincipal.getUserId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Endpoint to update the profile of the currently authenticated user.
     * Maps to PUT /api/users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserProfileRequestDTO requestDTO) {
        // userPrincipal.getUserId() returns Long (from your UserPrincipal class).
        // UserService.updateUserProfile now expects a Long, so pass it directly.
        UserProfileResponseDTO updatedProfile = userService.updateUserProfile(userPrincipal.getUserId(), requestDTO);
        return ResponseEntity.ok(updatedProfile);
    }
}