package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;

public record ProfileResponse(
        String displayName,
        String phoneNumber,
        String company
) {
    public static ProfileResponse from(AccountProfile profile) {
        return new ProfileResponse(profile.getDisplayName(), profile.getPhoneNumber(), profile.getCompany());
    }
}
