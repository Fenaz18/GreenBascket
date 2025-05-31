package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.LoginRequestDTO;
import com.example.GreenBascket.dto.LoginResponseDTO;
import com.example.GreenBascket.dto.RegisterRequestDTO;
import com.example.GreenBascket.model.User;
import com.example.GreenBascket.security.JwtUtil;
import com.example.GreenBascket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired // This annotation tells Spring to inject an instance of JwtUtil
    private JwtUtil jwtUtil; // This declares the variable named 'jwtUtil'

    @CrossOrigin(origins = "http://localhost:3001")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());         // ✅ set phone
        user.setLocation(dto.getLocation());   // ✅ set location

        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // You can also apply it to individual methods if needed
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(
                user.getUserId(), user.getName(), user.getEmail(),
                user.getRole(), user.getLocation(), user.getPhone(), token // ⬅️ include token
        ));
    }


}
