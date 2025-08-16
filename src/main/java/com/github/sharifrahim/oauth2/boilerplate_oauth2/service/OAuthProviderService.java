package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import org.springframework.stereotype.Service;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.OAuthProviderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OAuthProviderService {

    private final OAuthProviderRepository oauthProviderRepository;

    public OAuthProviderService(OAuthProviderRepository oauthProviderRepository) {
        this.oauthProviderRepository = oauthProviderRepository;
    }

    public OAuthProvider save(OAuthProvider oauthProvider) {
        return oauthProviderRepository.save(oauthProvider);
    }
}
