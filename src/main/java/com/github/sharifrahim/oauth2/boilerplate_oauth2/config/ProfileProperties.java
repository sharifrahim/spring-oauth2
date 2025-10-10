package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "profile")
@Getter
@Setter
public class ProfileProperties {
    /**
     * When true, users are redirected to complete their profile after OAuth sign-in.
     */
    private boolean requireCompletion = false;
}
