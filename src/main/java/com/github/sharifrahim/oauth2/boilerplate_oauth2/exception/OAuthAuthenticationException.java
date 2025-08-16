package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuthAuthenticationException extends AuthenticationException {
    public OAuthAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OAuthAuthenticationException(String msg) {
        super(msg);
    }
}
