package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE)
public class RegistrationSessionExpiredException extends RuntimeException {
    public RegistrationSessionExpiredException(String message) {
        super(message);
    }
}
