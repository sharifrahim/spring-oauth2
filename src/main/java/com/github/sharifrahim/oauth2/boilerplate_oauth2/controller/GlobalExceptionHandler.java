package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.RateLimitExceededException;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.exception.RegistrationSessionExpiredException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@RequestMapping("/error")
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ModelAndView modelAndView = new ModelAndView();

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                modelAndView.setViewName("error/404");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                modelAndView.setViewName("error/500");
            }
            else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                modelAndView.setViewName("error/403");
            }
             else if(statusCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
                modelAndView.setViewName("error/rate-limited");
            }
            else {
                 modelAndView.setViewName("error/default");
            }
        } else {
             modelAndView.setViewName("error/default");
        }
        modelAndView.addObject("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        return modelAndView;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public String handleRateLimitExceeded() {
        return "error/rate-limited";
    }

    @ExceptionHandler(RegistrationSessionExpiredException.class)
    public String handleRegistrationSessionExpired() {
        return "error/registration-expired";
    }
}
