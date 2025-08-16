package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;

@Repository
public interface RegistrationSessionRepository extends JpaRepository<RegistrationSession, Long> {
    Optional<RegistrationSession> findBySessionToken(String sessionToken);
    Optional<RegistrationSession> findByAccountId(Long accountId);
}
