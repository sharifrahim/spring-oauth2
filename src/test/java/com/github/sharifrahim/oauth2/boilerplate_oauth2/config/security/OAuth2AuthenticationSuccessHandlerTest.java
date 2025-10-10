package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.ProfileProperties;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountProfileService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.OAuthProviderService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RateLimitService;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock private RateLimitService rateLimitService;
    @Mock private AccountService accountService;
    @Mock private OAuthProviderService oauthProviderService;
    @Mock private AccountProfileService accountProfileService;
    @Mock private ProfileProperties profileProperties;
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
    void whenNewUserAndProfileNotRequired_redirectsToDashboard() throws Exception {
        String email = "new.user@example.com";
        OAuth2User oauthUser = new DefaultOAuth2User(Collections.emptyList(), Map.of("email", email, "name", "New User"), "email");

        when(authenticationToken.getPrincipal()).thenReturn(oauthUser);
        when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.empty());
        when(profileProperties.isRequireCompletion()).thenReturn(false);

        Account persistedAccount = new Account();
        persistedAccount.setId(42L);
        persistedAccount.setEmail(email);
        persistedAccount.setStatus(AccountStatus.ACTIVE);
        when(accountService.saveAccount(any(Account.class))).thenReturn(persistedAccount);

        AccountProfile profile = new AccountProfile();
        profile.setAccount(persistedAccount);
        profile.setDisplayName("New User");
        when(accountProfileService.ensureProfile(eq(persistedAccount), any(), any())).thenReturn(profile);

        successHandler.onAuthenticationSuccess(request, response, authenticationToken);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountService).saveAccount(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertEquals(email, savedAccount.getEmail());
        assertEquals(AccountStatus.ACTIVE, savedAccount.getStatus());
        verify(oauthProviderService).save(any(OAuthProvider.class));
        verify(accountProfileService).ensureProfile(eq(persistedAccount), any(), any());
        assertEquals("/dashboard", response.getRedirectedUrl());
    }

    @Test
    void whenProfileRequiredAndIncomplete_redirectsToProfile() throws Exception {
        String email = "existing.user@example.com";
        OAuth2User oauthUser = new DefaultOAuth2User(Collections.emptyList(), Map.of("email", email, "name", "Existing User"), "email");

        Account existingAccount = new Account();
        existingAccount.setId(7L);
        existingAccount.setEmail(email);
        existingAccount.setStatus(AccountStatus.ACTIVE);

        when(authenticationToken.getPrincipal()).thenReturn(oauthUser);
        when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.of(existingAccount));
        when(oauthProviderService.findByAccountIdAndProvider(eq(7L), any())).thenReturn(Optional.of(new OAuthProvider()));
        when(profileProperties.isRequireCompletion()).thenReturn(true);

        AccountProfile incompleteProfile = new AccountProfile();
        incompleteProfile.setAccount(existingAccount);
        incompleteProfile.setDisplayName(null);
        when(accountProfileService.ensureProfile(eq(existingAccount), any(), any())).thenReturn(incompleteProfile);
        when(accountProfileService.isProfileComplete(incompleteProfile)).thenReturn(false);

        successHandler.onAuthenticationSuccess(request, response, authenticationToken);

        verify(oauthProviderService, never()).save(any(OAuthProvider.class));
        assertEquals("/profile", response.getRedirectedUrl());
    }

    @Test
    void whenProfileRequiredAndComplete_redirectsToDashboard() throws Exception {
        String email = "existing.user@example.com";
        OAuth2User oauthUser = new DefaultOAuth2User(Collections.emptyList(), Map.of("email", email, "name", "Existing User"), "email");

        Account existingAccount = new Account();
        existingAccount.setId(8L);
        existingAccount.setEmail(email);
        existingAccount.setStatus(AccountStatus.ACTIVE);

        when(authenticationToken.getPrincipal()).thenReturn(oauthUser);
        when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.of(existingAccount));
        when(oauthProviderService.findByAccountIdAndProvider(eq(8L), any())).thenReturn(Optional.empty());
        when(profileProperties.isRequireCompletion()).thenReturn(true);

        AccountProfile completeProfile = new AccountProfile();
        completeProfile.setAccount(existingAccount);
        completeProfile.setDisplayName("Existing User");
        when(accountProfileService.ensureProfile(eq(existingAccount), any(), any())).thenReturn(completeProfile);
        when(accountProfileService.isProfileComplete(completeProfile)).thenReturn(true);

        successHandler.onAuthenticationSuccess(request, response, authenticationToken);

        verify(oauthProviderService).save(any(OAuthProvider.class));
        assertEquals("/dashboard", response.getRedirectedUrl());
    }
}
