package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.ProfileProperties;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.OAuthProviderType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountProfileService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.OAuthProviderService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RateLimitService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RateLimitService rateLimitService;
    private final AccountService accountService;
    private final OAuthProviderService oauthProviderService;
    private final AccountProfileService accountProfileService;
    private final ProfileProperties profileProperties;

    public OAuth2AuthenticationSuccessHandler(RateLimitService rateLimitService,
                                              AccountService accountService,
                                              OAuthProviderService oauthProviderService,
                                              AccountProfileService accountProfileService,
                                              ProfileProperties profileProperties) {
        this.rateLimitService = rateLimitService;
        this.accountService = accountService;
        this.oauthProviderService = oauthProviderService;
        this.accountProfileService = accountProfileService;
        this.profileProperties = profileProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = token.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String registrationId = token.getAuthorizedClientRegistrationId();

        rateLimitService.recordAttempt(request, email, AttemptType.OAUTH_START, com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AttemptStatus.SUCCESS);

        Optional<Account> existingAccountOpt = accountService.getAccountByEmail(email);

        OAuthProviderType providerType = OAuthProviderType.valueOf(registrationId.toUpperCase());

        Account account;

        if (existingAccountOpt.isEmpty()) {
            account = new Account();
            account.setEmail(email);
            account.setStatus(AccountStatus.ACTIVE);
            account = accountService.saveAccount(account);

            OAuthProvider provider = new OAuthProvider();
            provider.setAccount(account);
            provider.setProvider(providerType);
            provider.setProviderUserId(oauthUser.getName());
            oauthProviderService.save(provider);
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
        }

        AccountProfile profile = accountProfileService.ensureProfile(
            account,
            oauthUser.getAttribute("name"),
            oauthUser.getAttribute("given_name")
        );

        String targetUrl = "/dashboard";
        if (profileProperties.isRequireCompletion() && !accountProfileService.isProfileComplete(profile)) {
            targetUrl = "/profile";
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
