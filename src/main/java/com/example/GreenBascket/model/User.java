package com.example.GreenBascket.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="Users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {

    @Id
    @Column(length = 8)
    private String userId;

    @Column(nullable = false,length=100)
    private String name;

    @Column(nullable = false,unique = true,length = 100)
    private String email;

    @Column (nullable = false)
    private  String password;


    @Transient
    private String confirmpassword;

    @Column(nullable = false)
    private String role;

    @Column(length = 255)
    private String location;

    @Column(nullable = false,unique = true,length=15)
    private String phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }
}