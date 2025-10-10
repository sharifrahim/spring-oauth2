package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
class AccountServiceTest {

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
        account.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getAccountByEmail_shouldReturnAccountWhenPresent() {
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));

        Optional<Account> found = accountService.getAccountByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void saveAccount_shouldDelegateToRepository() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account saved = accountService.saveAccount(new Account());

        assertNotNull(saved);
        verify(accountRepository).save(any(Account.class));
    }
}
