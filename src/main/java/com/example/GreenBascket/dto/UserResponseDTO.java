package com.example.GreenBascket.dto;


// import lombok.AllArgsConstructor; // <<< REMOVE THIS if you're defining manually to avoid confusion


// If you define the constructor manually, you typically don't need @AllArgsConstructor
// as it can sometimes conflict or cause confusion.
// Remove @AllArgsConstructor if you want to explicitly control the constructor.
public class UserResponseDTO {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String location;
    private String token;

    // --- CRITICAL CHANGE: Constructor arguments now match the field order ---
    public UserResponseDTO(String userId, String name, String email, String role, String phone, String location, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.location = location;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}