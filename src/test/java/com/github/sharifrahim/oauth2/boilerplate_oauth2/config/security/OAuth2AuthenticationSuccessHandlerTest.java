package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.OAuthProviderService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RateLimitService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

@ExtendWith(MockitoExtension.class)
public class OAuth2AuthenticationSuccessHandlerTest {

    @Mock private RateLimitService rateLimitService;
    @Mock private AccountService accountService;
    @Mock private OAuthProviderService oauthProviderService;
    @Mock private RegistrationSessionService registrationSessionService;
    @Mock private OAuth2AuthenticationToken authenticationToken;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void whenNewUser_shouldCreateAccountAndRedirectToRegister() throws Exception {
        // Arrange
        String email = "new.user@example.com";
        OAuth2User oauthUser = new DefaultOAuth2User(Collections.emptyList(), Map.of("email", email, "name", "New User"), "email");
        when(authenticationToken.getPrincipal()).thenReturn(oauthUser);
        when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.empty());

        Account newAccount = new Account();
        newAccount.setEmail(email);
        when(accountService.saveAccount(any(Account.class))).thenReturn(newAccount);

        RegistrationSession session = new RegistrationSession();
        session.setSessionToken("test-token");
        when(registrationSessionService.createSession(any(Account.class))).thenReturn(session);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authenticationToken);

        // Assert
        verify(accountService, times(1)).saveAccount(any(Account.class));
        verify(oauthProviderService, times(1)).save(any());
        verify(registrationSessionService, times(1)).createSession(any(Account.class));
        assertEquals("/register?token=test-token", response.getRedirectedUrl());
    }

    @Test
    void whenExistingUser_shouldRedirectToDashboard() throws Exception {
        // Arrange
        String email = "existing.user@example.com";
        OAuth2User oauthUser = new DefaultOAuth2User(Collections.emptyList(), Map.of("email", email, "name", "Existing User"), "email");
        Account existingAccount = new Account();
        existingAccount.setEmail(email);

        when(authenticationToken.getPrincipal()).thenReturn(oauthUser);
        when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn("github");
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.of(existingAccount));

        // Act
        successHandler.onAuthenticationSuccess(request, response, authenticationToken);

        // Assert
        verify(accountService, never()).saveAccount(any(Account.class));
        verify(oauthProviderService, times(1)).save(any());
        verify(registrationSessionService, never()).createSession(any(Account.class));
        assertEquals("/dashboard", response.getRedirectedUrl());
    }
}
