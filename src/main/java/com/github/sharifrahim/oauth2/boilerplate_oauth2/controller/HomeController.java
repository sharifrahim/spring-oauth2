package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@GetMapping
	public ModelAndView main() {
		return new ModelAndView("index");
	}

	@GetMapping("/login")
	public ModelAndView login(Authentication authentication) {

		// Check if the user is already authenticated
		if (authentication != null && authentication.isAuthenticated()) {
			// Redirect to the home page if already logged in
			return new ModelAndView("redirect:/");
		}
		// Otherwise, show the login page
		return new ModelAndView("login"); // Render the login.html template
	}

	@GetMapping("/dashboard")
	public ModelAndView dashboard(Authentication authentication) {
		
		
		  ModelAndView modelAndView = new ModelAndView("dashboard");

	        // Initialize username variable
	        String username = null;

	        // Check if authentication is not null
	        if (authentication != null) {
	            Object principal = authentication.getPrincipal();

	            // Extract username from DefaultOAuth2User
	            if (principal instanceof DefaultOAuth2User) {
	                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;

	                // Example: Extract the "name" attribute
	                username = (String) oauth2User.getAttributes().get("name");
	                
	                // Or extract another attribute like "email"
	                // username = (String) oauth2User.getAttributes().get("email");
	            }
	        }

	        // Add the username to the model
	        modelAndView.addObject("username", username);
	        return modelAndView;
	}

}
