package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RateLimitPolicy;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.RateLimitPolicyRepository;

@Service
public class RateLimitPolicyService {

    private final RateLimitPolicyRepository rateLimitPolicyRepository;

    public RateLimitPolicyService(RateLimitPolicyRepository rateLimitPolicyRepository) {
        this.rateLimitPolicyRepository = rateLimitPolicyRepository;
    }

    @Transactional
    public RateLimitPolicy createOrUpdatePolicy(String email, Integer perMinute, Integer perHour, Integer perDay) {
        Optional<RateLimitPolicy> existing = rateLimitPolicyRepository.findActiveByEmail(email, Instant.now());
        
        RateLimitPolicy policy = existing.orElse(new RateLimitPolicy());
        policy.setEmail(email);
        policy.setMaxRequestsPerMinute(perMinute);
        policy.setMaxRequestsPerHour(perHour);
        policy.setMaxRequestsPerDay(perDay);
        policy.setEnabled(true);
        
        return rateLimitPolicyRepository.save(policy);
    }

    @Transactional
    public void disablePolicy(String email) {
        rateLimitPolicyRepository.findActiveByEmail(email, Instant.now())
            .ifPresent(policy -> {
                policy.setEnabled(false);
                rateLimitPolicyRepository.save(policy);
            });
    }

    public Optional<RateLimitPolicy> getActivePolicy(String email) {
        return rateLimitPolicyRepository.findActiveByEmail(email, Instant.now());
    }
}
