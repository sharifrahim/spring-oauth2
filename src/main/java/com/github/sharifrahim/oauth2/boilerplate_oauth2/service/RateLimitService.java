package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.LoginAttempt;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.LoginAttemptRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RateLimitService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Value("${security.rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${security.rate-limiting.ip-max-attempts:10}")
    private int ipMaxAttempts;

    @Value("${security.rate-limiting.email-max-attempts:5}")
    private int emailMaxAttempts;

    @Value("${security.rate-limiting.time-window-in-minutes:15}")
    private int timeWindowInMinutes;

    @Value("${security.rate-limiting.block-duration-in-minutes:60}")
    private int blockDurationInMinutes;


    public RateLimitService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    public boolean isBlocked(String ipAddress) {
        if (!enabled) {
            return false;
        }
        return loginAttemptRepository.countOfRecentBlocksByIp(ipAddress, Instant.now()) > 0;
    }

    public void recordAttempt(HttpServletRequest request, String email, AttemptType type, AttemptStatus status) {
        if (!enabled) {
            return;
        }

        String ipAddress = getClientIP(request);

        // Check if IP is already blocked
        if (isBlocked(ipAddress)) {
            // Optionally log that a blocked IP is still trying to attempt actions
            return;
        }

        LoginAttempt attempt = new LoginAttempt();
        attempt.setIpAddress(ipAddress);
        attempt.setEmail(email);
        attempt.setAttemptType(type);
        attempt.setStatus(status);
        attempt.setUserAgent(request.getHeader("User-Agent"));

        Instant now = Instant.now();

        if (status == AttemptStatus.FAILED) {
            Instant since = now.minus(timeWindowInMinutes, ChronoUnit.MINUTES);
            long ipFailures = loginAttemptRepository.countByIpAddressAndStatusFailedSince(ipAddress, since);

            if (ipFailures + 1 >= ipMaxAttempts) {
                blockIp(request, type);
                return;
            }

            if (email != null) {
                long emailFailures = loginAttemptRepository.countByEmailAndStatusFailedSince(email, since);
                if (emailFailures + 1 >= emailMaxAttempts) {
                    blockIp(request, type); // Block the IP associated with the email
                    return;
                }
            }
        }

        // For SUCCESS or FAILED attempts that don't trigger a block
        attempt.setExpiresAt(now.plus(1, ChronoUnit.DAYS)); // Record expires after 1 day
        loginAttemptRepository.save(attempt);
    }

    private void blockIp(HttpServletRequest request, AttemptType type) {
        LoginAttempt blockAttempt = new LoginAttempt();
        blockAttempt.setIpAddress(getClientIP(request));
        blockAttempt.setAttemptType(type);
        blockAttempt.setStatus(AttemptStatus.BLOCKED);
        blockAttempt.setUserAgent(request.getHeader("User-Agent"));
        blockAttempt.setExpiresAt(Instant.now().plus(blockDurationInMinutes, ChronoUnit.MINUTES));
        loginAttemptRepository.save(blockAttempt);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
