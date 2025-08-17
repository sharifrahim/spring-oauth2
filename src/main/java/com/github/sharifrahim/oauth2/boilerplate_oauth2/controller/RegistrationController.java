package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.StateMachineManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.CompanyDataDto;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.PersonalDataDto;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationSessionService sessionService;
    private final AccountService accountService;
    private final StateMachineManager stateMachineManager;

    public RegistrationController(RegistrationSessionService sessionService, AccountService accountService, StateMachineManager stateMachineManager) {
        this.sessionService = sessionService;
        this.accountService = accountService;
        this.stateMachineManager = stateMachineManager;
    }

    @GetMapping
    public String showRegistrationPage(Authentication auth) {
        // Create or get registration session and link with account
        String email = extractEmailFromAuth(auth);
        
        // Get the account
        Account account = accountService.getAccountByEmail(email)
            .orElseThrow(() -> new RuntimeException("Account not found for email: " + email));
        
        // Create registration session if it doesn't exist
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        
        // Link session with account if not already linked
        if (session.getAccount() == null) {
            session.setAccount(account);
            session.setCurrentState(RegistrationState.PERSONAL_INFO); // Start with PERSONAL_INFO
            sessionService.save(session);
            System.out.println("✅ Created RegistrationSession linked to account for: " + email);
        }
        
        // User is authenticated via OAuth, start with personal info
        return "redirect:/register/personal";
    }

    @GetMapping("/personal")
    public ModelAndView showPersonalForm(Authentication auth) {
        // Ensure the session is in PERSONAL_INFO state
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        
        // Ensure session is in PERSONAL_INFO state (should already be set from showRegistrationPage)
        if (session.getCurrentState() != RegistrationState.PERSONAL_INFO) {
            session.setCurrentState(RegistrationState.PERSONAL_INFO);
            sessionService.save(session);
            System.out.println("Set session state to PERSONAL_INFO for: " + email);
        }
        
        ModelAndView mav = new ModelAndView("register-personal");
        mav.addObject("personalDataDto", new PersonalDataDto());
        mav.addObject("userEmail", email);
        return mav;
    }

    @PostMapping("/personal")
    public String submitPersonalForm(Authentication auth, @Valid @ModelAttribute("personalDataDto") PersonalDataDto personalDataDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "register-personal";
        }
        
        // Get or create registration session for this authenticated user
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);

        // Use StateMachineManager to handle state machine operations
        boolean success = stateMachineManager.sendEvent(session, RegistrationEvent.SUBMIT_PERSONAL, personalDataDto);
        
        if (!success) {
            return "register-personal"; // Validation failed
        }

        return "redirect:/register/company";
    }

    @GetMapping("/company")
    public ModelAndView showCompanyForm(Authentication auth) {
        ModelAndView mav = new ModelAndView("register-company");
        mav.addObject("companyDataDto", new CompanyDataDto());
        return mav;
    }

    @PostMapping("/company")
    public String submitCompanyForm(Authentication auth, @Valid @ModelAttribute("companyDataDto") CompanyDataDto companyDataDto, BindingResult bindingResult) throws Exception {
        System.out.println("=== COMPANY FORM SUBMITTED ===");
        System.out.println("Company data DTO: " + companyDataDto);
        
        if (bindingResult.hasErrors()) {
            System.err.println("❌ Binding result has errors");
            return "register-company";
        }
        
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        System.out.println("Email: " + email);
        System.out.println("Session current state: " + session.getCurrentState());

        // Use StateMachineManager to handle state machine operations
        boolean success = stateMachineManager.sendEvent(session, RegistrationEvent.SUBMIT_COMPANY, companyDataDto);
        
        if (!success) {
            System.err.println("❌ State machine event failed");
            return "register-company"; // Validation failed
        }

        return "redirect:/register/complete";
    }

    @PostMapping("/skip-company")
    public String skipCompanyStep(Authentication auth) {
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        // Use StateMachineManager to handle state machine operations
        stateMachineManager.sendEvent(session, RegistrationEvent.SKIP_COMPANY, null);

        return "redirect:/register/complete";
    }


    @GetMapping("/complete")
    public ModelAndView showCompletionPage(Authentication auth) {
        return new ModelAndView("register-complete");
    }

    private String extractEmailFromAuth(Authentication auth) {
        if (auth.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) auth.getPrincipal();
            return (String) oauth2User.getAttributes().get("email");
        }
        throw new RuntimeException("Unable to extract email from authentication");
    }
}
