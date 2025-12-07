package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto;

public record ProfileUpdateRequest(
        String displayName,
        String phoneNumber,
        String company
) {
}
