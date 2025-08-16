package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setStatus(AccountStatus.PENDING);
        account.setPendingExpiresAt(Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS));
    }

    @Test
    void testGetAccountByEmail() {
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        Optional<Account> found = accountService.getAccountByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testSaveAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        Account saved = accountService.saveAccount(new Account());
        assertNotNull(saved);
    }

    @Test
    void testDeleteExpiredPendingAccounts() {
        when(accountRepository.findByStatusAndPendingExpiresAtBefore(eq(AccountStatus.PENDING), any(Instant.class)))
            .thenReturn(List.of(account));

        accountService.deleteExpiredPendingAccounts();

        verify(accountRepository, times(1)).findByStatusAndPendingExpiresAtBefore(eq(AccountStatus.PENDING), any(Instant.class));
        verify(accountRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testDeleteExpiredPendingAccounts_NoExpiredAccounts() {
        when(accountRepository.findByStatusAndPendingExpiresAtBefore(eq(AccountStatus.PENDING), any(Instant.class)))
            .thenReturn(Collections.emptyList());

        accountService.deleteExpiredPendingAccounts();

        verify(accountRepository, times(1)).findByStatusAndPendingExpiresAtBefore(eq(AccountStatus.PENDING), any(Instant.class));
        verify(accountRepository, never()).deleteAll(anyList());
    }
}
