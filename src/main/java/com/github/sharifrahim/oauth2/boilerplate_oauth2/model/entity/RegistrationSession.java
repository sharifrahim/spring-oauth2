package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity;

import java.time.Instant;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "registration_sessions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "account_id"),
    @UniqueConstraint(columnNames = "session_token")
})
public class RegistrationSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private RegistrationState currentState;

    @Column(name = "personal_data", columnDefinition = "TEXT")
    private String personalData;

    @Column(name = "company_data", columnDefinition = "TEXT")
    private String companyData;

    @Column(name = "session_token", nullable = false)
    private String sessionToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public RegistrationState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RegistrationState currentState) {
        this.currentState = currentState;
    }

    public String getPersonalData() {
        return personalData;
    }

    public void setPersonalData(String personalData) {
        this.personalData = personalData;
    }

    public String getCompanyData() {
        return companyData;
    }

    public void setCompanyData(String companyData) {
        this.companyData = companyData;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
