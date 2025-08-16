package com.github.sharifrahim.oauth2.boilerplate_oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRegistrationDataException extends RuntimeException {
    public InvalidRegistrationDataException(String message) {
        super(message);
    }
}
