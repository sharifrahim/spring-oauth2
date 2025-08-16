package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity;

import java.time.Instant;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String email;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "attempt_type", nullable = false)
    private AttemptType attemptType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public AttemptType getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(AttemptType attemptType) {
        this.attemptType = attemptType;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
