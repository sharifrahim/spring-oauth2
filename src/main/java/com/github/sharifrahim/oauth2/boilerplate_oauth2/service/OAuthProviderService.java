package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.OAuthProviderType;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.OAuthProvider;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.OAuthProviderRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class OAuthProviderService {

    private final OAuthProviderRepository oauthProviderRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public OAuthProviderService(OAuthProviderRepository oauthProviderRepository) {
        this.oauthProviderRepository = oauthProviderRepository;
    }

    public OAuthProvider save(OAuthProvider oauthProvider) {
        // Ensure the account is managed in the current persistence context
        if (oauthProvider.getAccount() != null) {
            oauthProvider.setAccount(entityManager.merge(oauthProvider.getAccount()));
        }
        return oauthProviderRepository.save(oauthProvider);
    }
    
    public Optional<OAuthProvider> findByAccountIdAndProvider(Long accountId, OAuthProviderType provider) {
        return oauthProviderRepository.findByAccountIdAndProvider(accountId, provider);
    }
}
