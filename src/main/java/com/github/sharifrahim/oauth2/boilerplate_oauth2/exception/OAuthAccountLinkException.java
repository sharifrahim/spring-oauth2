package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

public class OAuthAccountLinkException extends RuntimeException {
    
    public OAuthAccountLinkException(String message) {
        super(message);
    }
    
    public OAuthAccountLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}