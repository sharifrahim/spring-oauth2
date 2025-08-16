package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity;

import java.time.Instant;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class Account extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "pending_expires_at")
    private Instant pendingExpiresAt;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Instant getPendingExpiresAt() {
        return pendingExpiresAt;
    }

    public void setPendingExpiresAt(Instant pendingExpiresAt) {
        this.pendingExpiresAt = pendingExpiresAt;
    }
}
