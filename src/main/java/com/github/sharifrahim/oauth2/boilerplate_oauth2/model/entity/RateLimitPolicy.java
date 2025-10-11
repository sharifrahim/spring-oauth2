package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "rate_limit_policies", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitPolicy extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "max_requests_per_minute", nullable = false)
    private Integer maxRequestsPerMinute;

    @Column(name = "max_requests_per_hour", nullable = false)
    private Integer maxRequestsPerHour;

    @Column(name = "max_requests_per_day", nullable = false)
    private Integer maxRequestsPerDay;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
