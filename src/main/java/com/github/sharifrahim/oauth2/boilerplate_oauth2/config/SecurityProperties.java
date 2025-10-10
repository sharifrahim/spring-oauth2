package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Application-level security toggles.
 */
@Component
@ConfigurationProperties(prefix = "security.web")
@Getter
@Setter
public class SecurityProperties {

    /**
     * When true, the embedded H2 console is exposed (intended for local development only).
     */
    private boolean allowH2Console = false;
}
