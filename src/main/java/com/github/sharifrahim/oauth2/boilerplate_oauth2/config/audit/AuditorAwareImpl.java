package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(principal -> {
                    if (principal instanceof OAuth2User) {
                        return ((OAuth2User) principal).getAttribute("email");
                    }
                    return principal.toString();
                })
                .or(() -> Optional.of("system")); // Fallback for system processes
    }
}
