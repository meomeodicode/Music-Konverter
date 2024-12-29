package com.minh.konverter.Model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "spotifyid", nullable = false, unique = true)  
    private String spotifyId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url") 
    private String avatarUrl;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "access_token")  
    private String accessToken;

    @Column(name = "refresh_token")  
    private String refreshToken; 

    @Column(name = "created_at")  
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getId() {
        return uid;
    }

    public void setId(Long id) {
        this.uid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatarUrl;
    }

    public void setAvatar(String avatar) {
        this.avatarUrl = avatar;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
