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
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "registration_sessions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "account_id"),
    @UniqueConstraint(columnNames = "session_token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
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
}
