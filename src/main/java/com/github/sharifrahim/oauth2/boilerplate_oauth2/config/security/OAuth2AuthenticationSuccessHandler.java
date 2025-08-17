package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.OAuthProviderType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.OAuthProviderService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RateLimitService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RateLimitService rateLimitService;
    private final AccountService accountService;
    private final OAuthProviderService oauthProviderService;
    private final RegistrationSessionService registrationSessionService;

    public OAuth2AuthenticationSuccessHandler(RateLimitService rateLimitService, AccountService accountService, OAuthProviderService oauthProviderService, RegistrationSessionService registrationSessionService) {
        this.rateLimitService = rateLimitService;
        this.accountService = accountService;
        this.oauthProviderService = oauthProviderService;
        this.registrationSessionService = registrationSessionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = token.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String registrationId = token.getAuthorizedClientRegistrationId();

        rateLimitService.recordAttempt(request, email, AttemptType.OAUTH_START, com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptStatus.SUCCESS);

        Optional<Account> existingAccountOpt = accountService.getAccountByEmail(email);

        Account account;
        String targetUrl;

        OAuthProviderType providerType = OAuthProviderType.valueOf(registrationId.toUpperCase());
        
        if (existingAccountOpt.isEmpty()) {
            // Create new account
            account = new Account();
            account.setEmail(email);
            account.setUsername(oauthUser.getAttribute("name"));
            account.setStatus(AccountStatus.PENDING);
            account.setPendingExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
            account = accountService.saveAccount(account);

            // Link the OAuth provider
            OAuthProvider provider = new OAuthProvider();
            provider.setAccount(account);
            provider.setProvider(providerType);
            provider.setProviderUserId(oauthUser.getName());
            oauthProviderService.save(provider);

            targetUrl = "/register";
        } else {
            account = existingAccountOpt.get();
            
            // Check if this OAuth provider is already linked
            Optional<OAuthProvider> existingProvider = oauthProviderService.findByAccountIdAndProvider(account.getId(), providerType);
            
            if (existingProvider.isEmpty()) {
                // Link the new OAuth provider to existing account
                try {
                    OAuthProvider provider = new OAuthProvider();
                    provider.setAccount(account);
                    provider.setProvider(providerType);
                    provider.setProviderUserId(oauthUser.getName());
                    oauthProviderService.save(provider);
                    
                    // Add success message to session for user feedback
                    request.getSession().setAttribute("accountLinkSuccess", 
                        "Your " + providerType.name().toLowerCase() + " account has been successfully linked!");
                } catch (Exception e) {
                    // Handle any database constraint violations gracefully
                    request.getSession().setAttribute("accountLinkError", 
                        "Unable to link " + providerType.name().toLowerCase() + " account. Please try again.");
                }
            }
            
            targetUrl = "/dashboard";
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
