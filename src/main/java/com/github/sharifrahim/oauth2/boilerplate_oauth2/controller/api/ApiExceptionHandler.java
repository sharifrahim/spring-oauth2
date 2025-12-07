package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.AccountSuspendedException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.OAuthAccountLinkException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.RateLimitExceededException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.ApiErrorResponse;

@RestControllerAdvice(basePackages = "com.github.sharifrahim.oauth2.boilerplate_oauth2.controller")
public class ApiExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiErrorResponse("rate_limited", messageOrDefault(ex, "Too many attempts")));
    }

    @ExceptionHandler(OAuthAccountLinkException.class)
    public ResponseEntity<ApiErrorResponse> handleOAuthAccountLinkException(OAuthAccountLinkException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("oauth_account_link_error", messageOrDefault(ex, "OAuth account linking failed")));
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountSuspended(AccountSuspendedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse("suspended", messageOrDefault(ex, "Account is suspended")));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiErrorResponse(codeFromStatus(ex.getStatusCode().value()), ex.getReason()));
    }

    private String messageOrDefault(Exception ex, String fallback) {
        return ex.getMessage() != null ? ex.getMessage() : fallback;
    }

    private String codeFromStatus(int status) {
        return switch (status) {
            case 400 -> "bad_request";
            case 401 -> "unauthorized";
            case 403 -> "forbidden";
            case 404 -> "not_found";
            case 429 -> "rate_limited";
            default -> "error";
        };
    }
}
