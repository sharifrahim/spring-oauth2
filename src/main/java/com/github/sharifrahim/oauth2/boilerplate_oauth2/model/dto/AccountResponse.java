package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;

public record AccountResponse(
        Long id,
        String email,
        AccountStatus status,
        ProfileResponse profile,
        boolean profileComplete
) {
    public static AccountResponse from(Account account, AccountProfile profile) {
        ProfileResponse profileResponse = profile != null ? ProfileResponse.from(profile) : null;
        boolean complete = profile != null && profile.isComplete();
        return new AccountResponse(account.getId(), account.getEmail(), account.getStatus(), profileResponse, complete);
    }
}
