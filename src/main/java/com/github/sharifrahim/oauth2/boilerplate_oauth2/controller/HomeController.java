package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.CompanyDataDto;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.PersonalDataDto;

@Controller
public class HomeController {

	private final RegistrationSessionService registrationSessionService;
	private final ObjectMapper objectMapper;

	public HomeController(RegistrationSessionService registrationSessionService, ObjectMapper objectMapper) {
		this.registrationSessionService = registrationSessionService;
		this.objectMapper = objectMapper;
	}

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

	        // Initialize variables
	        String username = null;
	        String email = null;
	        CompanyDataDto companyData = null;
	        PersonalDataDto personalData = null;

	        // Check if authentication is not null
	        if (authentication != null) {
	            Object principal = authentication.getPrincipal();

	            // Extract username and email from DefaultOAuth2User
	            if (principal instanceof DefaultOAuth2User) {
	                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;

	                // Extract the "name" attribute
	                username = (String) oauth2User.getAttributes().get("name");
	                
	                // Extract email
	                email = (String) oauth2User.getAttributes().get("email");
	                
	                // Try to get registration session data
	                try {
	                    RegistrationSession session = registrationSessionService.getOrCreateForUser(email);
	                    
	                    // Parse company data if available
	                    if (session.getCompanyData() != null && !session.getCompanyData().isEmpty()) {
	                        companyData = objectMapper.readValue(session.getCompanyData(), CompanyDataDto.class);
	                    }
	                    
	                    // Parse personal data if available
	                    if (session.getPersonalData() != null && !session.getPersonalData().isEmpty()) {
	                        personalData = objectMapper.readValue(session.getPersonalData(), PersonalDataDto.class);
	                    }
	                } catch (Exception e) {
	                    // Log error but don't break the dashboard
	                    System.err.println("Error retrieving registration data: " + e.getMessage());
	                }
	            }
	        }

	        // Add all data to the model
	        modelAndView.addObject("username", username);
	        modelAndView.addObject("userEmail", email);
	        modelAndView.addObject("companyData", companyData);
	        modelAndView.addObject("personalData", personalData);
	        return modelAndView;
	}

}
