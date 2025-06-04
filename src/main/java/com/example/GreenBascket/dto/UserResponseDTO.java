package com.example.GreenBascket.dto;

// Remove Lombok annotations if you're manually defining everything
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

public class UserResponseDTO {
    private String userId; // FIX: Changed to String
    private String name;
    private String email;
    private String role;
    private String phone;
    private String location;
    private String token;

    // --- Constructor ---
    // FIX: Constructor argument for userId is String
    public UserResponseDTO(String userId, String name, String email, String role, String phone, String location, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.location = location;
        this.token = token;
    }

    // FIX: Add a no-argument constructor (essential for JSON deserialization by frameworks)
    public UserResponseDTO() {
    }

    // --- Getters ---
    public String getUserId() { // FIX: Return type is String
        return userId;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getToken() { return token; }

    // --- Setters ---
    public void setUserId(String userId) { // FIX: Parameter type is String
        this.userId = userId;
    }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setToken(String token) { this.token = token; }
}