package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.ApiUsage;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RateLimitPolicy;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.ApiUsageRepository;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.RateLimitPolicyRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ApiRateLimitService {

    private final ApiUsageRepository apiUsageRepository;
    private final RateLimitPolicyRepository rateLimitPolicyRepository;

    public ApiRateLimitService(ApiUsageRepository apiUsageRepository,
                                RateLimitPolicyRepository rateLimitPolicyRepository) {
        this.apiUsageRepository = apiUsageRepository;
        this.rateLimitPolicyRepository = rateLimitPolicyRepository;
    }

    @Transactional
    public void recordUsage(String email, HttpServletRequest request) {
        ApiUsage usage = new ApiUsage();
        usage.setEmail(email);
        usage.setEndpoint(request.getRequestURI());
        usage.setHttpMethod(request.getMethod());
        usage.setIpAddress(getClientIP(request));
        usage.setRecordedAt(Instant.now());
        apiUsageRepository.save(usage);
    }

    public boolean isAllowed(String email) {
        RateLimitPolicy policy = rateLimitPolicyRepository
            .findActiveByEmail(email, Instant.now())
            .orElse(getMaxPlan());

        Instant now = Instant.now();
        
        long perMinute = apiUsageRepository.countByEmailSince(email, now.minus(1, ChronoUnit.MINUTES));
        if (perMinute >= policy.getMaxRequestsPerMinute()) {
            return false;
        }

        long perHour = apiUsageRepository.countByEmailSince(email, now.minus(1, ChronoUnit.HOURS));
        if (perHour >= policy.getMaxRequestsPerHour()) {
            return false;
        }

        long perDay = apiUsageRepository.countByEmailSince(email, now.minus(1, ChronoUnit.DAYS));
        if (perDay >= policy.getMaxRequestsPerDay()) {
            return false;
        }

        return true;
    }

    private RateLimitPolicy getMaxPlan() {
        RateLimitPolicy maxPlan = new RateLimitPolicy();
        maxPlan.setMaxRequestsPerMinute(Integer.MAX_VALUE);
        maxPlan.setMaxRequestsPerHour(Integer.MAX_VALUE);
        maxPlan.setMaxRequestsPerDay(Integer.MAX_VALUE);
        maxPlan.setEnabled(true);
        return maxPlan;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
