package com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleLogoutHandler implements LogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(GoogleLogoutHandler.class);
    private static final String REVOKE_ENDPOINT = "https://oauth2.googleapis.com/revoke";

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleLogoutHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Auth)) {
            return;
        }

        String registrationId = oauth2Auth.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId, oauth2Auth.getName());
        if (client == null || client.getAccessToken() == null) {
            return;
        }

        String accessToken = client.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", accessToken);

        try {
            ResponseEntity<String> revokeResponse = restTemplate.postForEntity(REVOKE_ENDPOINT, new HttpEntity<>(body, headers), String.class);
            log.info("Revoked Google access token, status={}", revokeResponse.getStatusCode());
        } catch (Exception ex) {
            log.warn("Failed to revoke Google token during logout: {}", ex.getMessage());
        } finally {
            authorizedClientService.removeAuthorizedClient(registrationId, oauth2Auth.getName());
        }
    }
}
