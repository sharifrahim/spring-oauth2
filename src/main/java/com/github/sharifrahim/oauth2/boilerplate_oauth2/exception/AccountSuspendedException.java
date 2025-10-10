package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountSuspendedException extends RuntimeException {

    public AccountSuspendedException(String message) {
        super(message);
    }
}
