package com.example.GreenBascket.dto;

public class LoginResponseDTO {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String location;
    private String phone;
    private String token;

    public LoginResponseDTO(String userId, String name, String email,
                            String role, String location, String phone, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.location = location;
        this.phone = phone;
        this.token = token;

    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}