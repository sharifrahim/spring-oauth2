package com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileForm {
    private String displayName;
    private String phoneNumber;
    private String company;

    public ProfileForm(String displayName, String phoneNumber, String company) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.company = company;
    }
}
