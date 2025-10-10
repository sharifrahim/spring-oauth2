package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.repository.AccountProfileRepository;

@Service
@Transactional
public class AccountProfileService {

    private final AccountProfileRepository accountProfileRepository;

    public AccountProfileService(AccountProfileRepository accountProfileRepository) {
        this.accountProfileRepository = accountProfileRepository;
    }

    public Optional<AccountProfile> findByAccountId(Long accountId) {
        return accountProfileRepository.findByAccountId(accountId);
    }

    public AccountProfile ensureProfile(Account account, String primaryDisplayName, String fallbackDisplayName) {
        return findByAccountId(account.getId())
            .orElseGet(() -> {
                AccountProfile profile = new AccountProfile();
                profile.setAccount(account);
                String displayName = resolveDisplayName(primaryDisplayName, fallbackDisplayName);
                if (StringUtils.hasText(displayName)) {
                    profile.setDisplayName(displayName);
                }
                return accountProfileRepository.save(profile);
            });
    }

    public AccountProfile save(AccountProfile profile) {
        return accountProfileRepository.save(profile);
    }

    public boolean isProfileComplete(AccountProfile profile) {
        return profile != null && profile.isComplete();
    }

    private String resolveDisplayName(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary;
        }
        if (StringUtils.hasText(fallback)) {
            return fallback;
        }
        return null;
    }
}
