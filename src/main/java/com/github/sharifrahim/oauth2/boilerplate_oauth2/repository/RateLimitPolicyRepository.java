package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RateLimitPolicy;

@Repository
public interface RateLimitPolicyRepository extends JpaRepository<RateLimitPolicy, Long> {

    @Query("SELECT p FROM RateLimitPolicy p WHERE p.email = :email AND p.enabled = true AND (p.expiresAt IS NULL OR p.expiresAt > :now)")
    Optional<RateLimitPolicy> findActiveByEmail(@Param("email") String email, @Param("now") Instant now);
}
