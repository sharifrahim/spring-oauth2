package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
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
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationSessionService sessionService;
    private final StateMachineFactory<RegistrationState, RegistrationEvent> stateMachineFactory;

    public RegistrationController(RegistrationSessionService sessionService, StateMachineFactory<RegistrationState, RegistrationEvent> stateMachineFactory) {
        this.sessionService = sessionService;
        this.stateMachineFactory = stateMachineFactory;
    }

    @GetMapping
    public String showRegistrationPage(Authentication auth) {
        // User is authenticated via OAuth, start with personal info
        return "redirect:/register/personal";
    }

    @GetMapping("/personal")
    public ModelAndView showPersonalForm(Authentication auth) {
        // Ensure the session is in PERSONAL_INFO state
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        
        if (session.getCurrentState() == RegistrationState.PENDING_OAUTH) {
            // Transition from PENDING_OAUTH to PERSONAL_INFO
            StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
            sm.start(); // Start the state machine
            sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
            
            System.out.println("State machine current state before START_PERSONAL: " + sm.getState().getId());
            boolean success = sm.sendEvent(RegistrationEvent.START_PERSONAL);
            System.out.println("START_PERSONAL event success: " + success);
            System.out.println("State machine current state after START_PERSONAL: " + sm.getState().getId());
            
            session.setCurrentState(sm.getState().getId());
            sessionService.save(session);
            System.out.println("Transitioned to state: " + session.getCurrentState());
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

        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.start(); // Start the state machine
        
        // Pass data through state machine extended state BEFORE resetting context
        sm.getExtendedState().getVariables().put("personalData", personalDataDto);
        sm.getExtendedState().getVariables().put("session", session);
        
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        
        System.out.println("Current state before event: " + sm.getState().getId());
        System.out.println("Session current state: " + session.getCurrentState());
        boolean success = sm.sendEvent(RegistrationEvent.SUBMIT_PERSONAL);
        System.out.println("Event success: " + success);
        System.out.println("State after event: " + sm.getState().getId());
        if (!success) {
            return "register-personal"; // Validation failed
        }
        
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

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
        if (bindingResult.hasErrors()) {
            return "register-company";
        }
        
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);

        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        
        // Pass data through state machine extended state BEFORE resetting context
        sm.getExtendedState().getVariables().put("companyData", companyDataDto);
        sm.getExtendedState().getVariables().put("session", session);
        
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        
        boolean success = sm.sendEvent(RegistrationEvent.SUBMIT_COMPANY);
        if (!success) {
            return "register-company"; // Validation failed
        }
        
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

        return "redirect:/register/complete";
    }

    @PostMapping("/skip-company")
    public String skipCompanyStep(Authentication auth) {
        String email = extractEmailFromAuth(auth);
        RegistrationSession session = sessionService.getOrCreateForUser(email);
        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        sm.sendEvent(RegistrationEvent.SKIP_COMPANY);
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

        return "redirect:/register/complete";
    }

    @GetMapping("/pending_oauth")
    public String handlePendingOAuth() {
        // For pending OAuth state, redirect to the main registration handler
        return "redirect:/register";
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
