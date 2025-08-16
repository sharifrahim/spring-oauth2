package com.github.sharifrahim.oauth2.boilerplate_oauth2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;

import java.time.Instant;
import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    List<Account> findByStatusAndPendingExpiresAtBefore(AccountStatus status, Instant now);
}
