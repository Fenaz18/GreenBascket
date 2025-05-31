package com.example.GreenBascket.dto;

public class UserProfileRequestDTO {
    private String name;
    private String phone;
    private String location;

    // Getters and Setters for the fields
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // You can also add a no-argument constructor if needed by some frameworks
    public UserProfileRequestDTO() {
    }

    // And a constructor with all fields for convenience (optional)
    public UserProfileRequestDTO(String name, String phone, String location) {
        this.name = name;
        this.phone = phone;
        this.location = location;
    }
}