package com.example.ex4springgaldrimer1.entity;

import com.example.ex4springgaldrimer1.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER; // Default role

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    @Column(name = "customer_points", nullable = false)
    private Integer customerPoints = 0;

    @Column(name = "highest_game_score", nullable = false)
    private Integer highestGameScore = 0;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    // Constructors
    public User() {
        this.registrationDate = LocalDateTime.now();
        this.customerPoints = 0;
        this.highestGameScore = 0;
        this.enabled = true;
    }

    public User(String username, String password, String email, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public Integer getCustomerPoints() {
        return customerPoints;
    }

    public void setCustomerPoints(Integer customerPoints) {
        this.customerPoints = customerPoints;
    }

    public Integer getHighestGameScore() {
        return highestGameScore;
    }

    public void setHighestGameScore(Integer highestGameScore) {
        this.highestGameScore = highestGameScore;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    // Helper methods
    public void addCustomerPoints(int points) {
        this.customerPoints += points;
    }

    public void updateHighestScore(int newScore) {
        if (newScore > this.highestGameScore) {
            this.highestGameScore = newScore;
        }
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", customerPoints=" + customerPoints +
                ", highestGameScore=" + highestGameScore +
                '}';
    }
}