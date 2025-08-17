package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

public class RegistrationIncompleteException extends RuntimeException {
    
    public RegistrationIncompleteException(String message) {
        super(message);
    }
    
    public RegistrationIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}