package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.AccountProfile;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountProfileService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import org.springframework.util.StringUtils;

@Controller
public class HomeController {

	private final AccountService accountService;
    private final AccountProfileService accountProfileService;

	public HomeController(AccountService accountService, AccountProfileService accountProfileService) {
		this.accountService = accountService;
        this.accountProfileService = accountProfileService;
	}

	@GetMapping
	public ModelAndView main() {
		return new ModelAndView("index");
	}

	@GetMapping("/login")
	public ModelAndView login(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			return new ModelAndView("redirect:/dashboard");
		}
		return new ModelAndView("login");
	}

	@GetMapping("/dashboard")
	public ModelAndView dashboard(Authentication authentication, HttpServletRequest request) {
		
		
		  ModelAndView modelAndView = new ModelAndView("dashboard");

	        // Initialize variables
	        String displayName = null;
	        String email = null;
	        AccountStatus accountStatus = null;
	        AccountProfile profile = null;

	        // Check if authentication is not null
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            // Extract display hints and email from DefaultOAuth2User
            if (principal instanceof DefaultOAuth2User) {
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;

                String oauthName = (String) oauth2User.getAttributes().get("name");
                email = (String) oauth2User.getAttributes().get("email");

                if (email != null) {
                    Optional<Account> accountOpt = accountService.getAccountByEmail(email);
                    if (accountOpt.isPresent()) {
                        Account account = accountOpt.get();
                        accountStatus = account.getStatus();
                        profile = accountProfileService.findByAccountId(account.getId()).orElse(null);
                    }
                }

                if (profile != null && StringUtils.hasText(profile.getDisplayName())) {
                    displayName = profile.getDisplayName();
                } else {
                    displayName = oauthName;
                }
            }
        }

	        // Add all data to the model
	        modelAndView.addObject("displayName", displayName);
	        modelAndView.addObject("userEmail", email);
	        modelAndView.addObject("accountStatus", accountStatus);
            modelAndView.addObject("profile", profile);
	        
	        // Clear OAuth account linking messages after displaying them
	        request.getSession().removeAttribute("accountLinkSuccess");
	        request.getSession().removeAttribute("accountLinkError");
	        
	        return modelAndView;
	}

}
