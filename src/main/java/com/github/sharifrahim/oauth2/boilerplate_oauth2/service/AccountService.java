package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;
import java.time.Instant;


@Service
@Transactional
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public void deleteExpiredPendingAccounts() {
        log.info("Running scheduled cleanup of expired pending accounts.");
        List<Account> expiredAccounts = accountRepository.findByStatusAndPendingExpiresAtBefore(AccountStatus.PENDING, Instant.now());
        if (expiredAccounts.isEmpty()) {
            log.info("No expired pending accounts found to clean up.");
            return;
        }

        log.info("Found {} expired pending accounts to delete.", expiredAccounts.size());
        accountRepository.deleteAll(expiredAccounts);
        log.info("Successfully deleted {} expired pending accounts.", expiredAccounts.size());
    }
}
