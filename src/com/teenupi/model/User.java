package com.teenupi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String name;
    private String upiId;
    private Integer avatarHue;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private String bio;

    private String profilePictureUrl;

    public User(Long id, String name, String upiId, Integer avatarHue, BigDecimal balance, String bio) {
        this.id = id;
        this.name = name;
        this.upiId = upiId;
        this.avatarHue = avatarHue;
        this.balance = balance;
        this.bio = bio;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUpiId() {
        return upiId;
    }

    public Integer getAvatarHue() {
        return avatarHue;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
