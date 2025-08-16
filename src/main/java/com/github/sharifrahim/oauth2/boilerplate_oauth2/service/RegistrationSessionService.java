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

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistrationSessionService {

    private final RegistrationSessionRepository sessionRepository;

    public RegistrationSessionService(RegistrationSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public RegistrationSession createSession(Account account) {
        RegistrationSession session = new RegistrationSession();
        session.setAccount(account);
        session.setCurrentState(RegistrationState.PENDING_OAUTH);
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
}
