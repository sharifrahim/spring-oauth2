package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.OAuthProviderType;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "oauth_providers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"account_id", "provider"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthProvider extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProviderType provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;
}
