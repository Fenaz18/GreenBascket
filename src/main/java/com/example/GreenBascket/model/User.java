package com.example.GreenBascket.model;

import jakarta.persistence.*;

@Entity
@Table(name="Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length=100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column (nullable = false)
    private String password;

    @Transient
    private String confirmpassword;

    @Column(nullable = false)
    private String role;

    @Column(length = 255)
    private String location;

    @Column(nullable = false, unique = true, length=15)
    private String phone;

    // --- Constructors ---
    public User() {
        // No-argument constructor required by JPA
    }

    public User(Long userId, String name, String email, String password, String confirmpassword, String role, String location, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.role = role;
        this.location = location;
        this.phone = phone;
    }

    // --- Getters ---
    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public String getRole() {
        return role;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    // --- Setters ---
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}