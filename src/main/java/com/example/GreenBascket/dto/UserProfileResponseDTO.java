package com.example.GreenBascket.dto;

// Remove Lombok annotations if you're defining constructors manually
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

public class UserProfileResponseDTO {
    private String userId; // FIX: Make the field String
    private String name;
    private String email;
    private String phone;
    private String location;
    private String role;

    // FIX: Add a proper no-argument constructor (essential for JSON deserialization)
    public UserProfileResponseDTO() {
    }

    // FIX: All-argument constructor now expects String userId
    public UserProfileResponseDTO(String userId, String name, String email, String phone, String location, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.role = role;
    }

    // --- Getters ---
    public String getUserId() { // FIX: Getter returns String
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

    // Optional Setters (if needed for deserialization or other purposes)
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setRole(String role) { this.role = role; }
}