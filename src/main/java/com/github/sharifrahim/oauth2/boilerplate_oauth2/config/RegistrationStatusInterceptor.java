package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.RegistrationIncompleteException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RegistrationStatusInterceptor implements HandlerInterceptor {

    private final AccountService accountService;
    private final RegistrationSessionService registrationSessionService;

    public RegistrationStatusInterceptor(AccountService accountService, RegistrationSessionService registrationSessionService) {
        this.accountService = accountService;
        this.registrationSessionService = registrationSessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String requestUri = request.getRequestURI();
        
        // Skip interceptor for public endpoints and registration-related endpoints
        if (shouldSkipInterceptor(requestUri)) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Only apply interceptor to authenticated users
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof DefaultOAuth2User) {
            
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = (String) oauth2User.getAttributes().get("email");
            
            if (email != null && !isRegistrationComplete(email)) {
                // Throw exception to be handled by @ControllerAdvice
                throw new RegistrationIncompleteException("User registration is not complete for email: " + email);
            }
        }

        return true; // Continue to controller
    }

    private boolean shouldSkipInterceptor(String requestUri) {
        // Skip interceptor for these paths
        return requestUri.equals("/") ||
               requestUri.startsWith("/login") ||
               requestUri.startsWith("/logout") ||
               requestUri.startsWith("/register") ||
               requestUri.startsWith("/error") ||
               requestUri.startsWith("/oauth") ||
               requestUri.startsWith("/css/") ||
               requestUri.startsWith("/js/") ||
               requestUri.startsWith("/images/") ||
               requestUri.equals("/favicon.ico") ||
               requestUri.startsWith("/h2-console/");
    }

    private boolean isRegistrationComplete(String email) {
        try {
            // Check account status
            Optional<Account> accountOpt = accountService.getAccountByEmail(email);
            if (accountOpt.isEmpty()) {
                return false; // Account doesn't exist, needs registration
            }
            
            Account account = accountOpt.get();
            
            // If account is still PENDING, registration is not complete
            if (account.getStatus() == AccountStatus.PENDING) {
                return false;
            }
            
            // Check registration session state
            RegistrationSession session = registrationSessionService.getOrCreateForUser(email);
            return session.getCurrentState() == RegistrationState.COMPLETED;
            
        } catch (Exception e) {
            // If we can't determine status, assume registration is incomplete for safety
            return false;
        }
    }
}