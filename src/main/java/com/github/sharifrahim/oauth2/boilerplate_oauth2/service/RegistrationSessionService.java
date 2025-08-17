package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.RegistrationSessionRepository;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistrationSessionService {

    private final RegistrationSessionRepository sessionRepository;
    private final AccountService accountService;
    
    @PersistenceContext
    private EntityManager entityManager;

    public RegistrationSessionService(RegistrationSessionRepository sessionRepository, AccountService accountService) {
        this.sessionRepository = sessionRepository;
        this.accountService = accountService;
    }

    public RegistrationSession createSession(Account account) {
        // Ensure the account is managed in the current persistence context
        Account managedAccount = entityManager.merge(account);
        
        RegistrationSession session = new RegistrationSession();
        session.setAccount(managedAccount);
        session.setCurrentState(RegistrationState.PERSONAL_INFO);
        session.setSessionToken(UUID.randomUUID().toString());
        session.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS)); // Session expires in 1 hour
        return sessionRepository.save(session);
    }

    public Optional<RegistrationSession> findBySessionToken(String token) {
        return sessionRepository.findBySessionToken(token);
    }

    public Optional<RegistrationSession> findByAccountId(Long accountId) {
        return sessionRepository.findByAccountId(accountId);
    }

    public RegistrationSession save(RegistrationSession session) {
        return sessionRepository.save(session);
    }

    public RegistrationSession getOrCreateForUser(String email) {
        Account account = accountService.getAccountByEmail(email)
            .orElseThrow(() -> new RuntimeException("Account not found for email: " + email));
        
        // Check if registration session already exists
        Optional<RegistrationSession> existingSession = findByAccountId(account.getId());
        if (existingSession.isPresent()) {
            return existingSession.get();
        }
        
        // Create new session
        return createSession(account);
    }
}
