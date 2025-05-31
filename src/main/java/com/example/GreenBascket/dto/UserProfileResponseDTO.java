package com.example.GreenBascket.dto;


public class UserProfileResponseDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String role; // Use your UserRole enum or String, depending on your User model

    // Constructor to easily create instances with all fields
    public UserProfileResponseDTO(String userId, String name, String email, String phone, String location, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.role = role;
    }

    // You should also include a no-argument constructor if your framework or libraries require it (e.g., for JSON deserialization)
    public UserProfileResponseDTO() {
    }

    // Getters for all fields (essential for Spring to serialize this to JSON)
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getRole() {
        return role;
    }

    // Setters are generally not needed for a Response DTO, as it's typically immutable after creation.
    // However, if your JSON deserializer expects setters, you might add them:
    // public void setUserId(String userId) { this.userId = userId; }
    // public void setName(String name) { this.name = name; }
    // public void setEmail(String email) { this.email = email; }
    // public void setPhone(String phone) { this.phone = phone; }
    // public void setLocation(String location) { this.location = location; }
    // public void setRole(UserRole role) { this.role = role; }
}