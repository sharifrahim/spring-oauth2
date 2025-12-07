package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginWithGoogle() {
        return "redirect:/api/login/google";
    }
}
