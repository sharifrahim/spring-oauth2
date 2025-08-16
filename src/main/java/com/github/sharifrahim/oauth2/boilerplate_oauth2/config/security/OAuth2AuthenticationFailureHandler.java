package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RateLimitService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final RateLimitService rateLimitService;

    public OAuth2AuthenticationFailureHandler(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        setDefaultFailureUrl("/oauth-error");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // The email is not available in the exception for OAuth2 failures typically, so we pass null.
        // The rate limiting will be based on IP address in this case.
        rateLimitService.recordAttempt(request, null, AttemptType.OAUTH_START, com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptStatus.FAILED);

        // You can log the exception here for debugging purposes
        // logger.error("OAuth2 Authentication failed: {}", exception.getMessage());

        super.onAuthenticationFailure(request, response, exception);
    }
}
