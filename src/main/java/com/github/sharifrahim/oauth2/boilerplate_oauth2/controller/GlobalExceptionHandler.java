package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.RateLimitExceededException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.AccountSuspendedException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.OAuthAccountLinkException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public String handleRateLimitExceeded() {
        return "error/rate-limited";
    }

    @ExceptionHandler(OAuthAccountLinkException.class)
    public String handleOAuthAccountLinkException() {
        return "error/oauth-error";
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public String handleAccountSuspended() {
        return "error/suspended";
    }
}
