package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.ApiRateLimitService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiRateLimitInterceptor implements HandlerInterceptor {

    private final ApiRateLimitService apiRateLimitService;

    public ApiRateLimitInterceptor(ApiRateLimitService apiRateLimitService) {
        this.apiRateLimitService = apiRateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User user = (OAuth2User) auth.getPrincipal();
            String email = user.getAttribute("email");
            
            if (email != null) {
                if (!apiRateLimitService.isAllowed(email)) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("Rate limit exceeded");
                    return false;
                }
                apiRateLimitService.recordUsage(email, request);
            }
        }
        
        return true;
    }
}
