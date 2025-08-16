package com.github.sharifrahim.oauth2.boilerplate_oauth2.controller;

import java.time.Instant;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.CompanyDataDto;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.dto.PersonalDataDto;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.CompanyInfoStrategy;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.PersonalInfoStrategy;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationSessionService sessionService;
    private final StateMachineFactory<RegistrationState, RegistrationEvent> stateMachineFactory;
    private final PersonalInfoStrategy personalInfoStrategy;
    private final CompanyInfoStrategy companyInfoStrategy;

    public RegistrationController(RegistrationSessionService sessionService, StateMachineFactory<RegistrationState, RegistrationEvent> stateMachineFactory, PersonalInfoStrategy personalInfoStrategy, CompanyInfoStrategy companyInfoStrategy) {
        this.sessionService = sessionService;
        this.stateMachineFactory = stateMachineFactory;
        this.personalInfoStrategy = personalInfoStrategy;
        this.companyInfoStrategy = companyInfoStrategy;
    }

    @GetMapping
    public String showRegistrationPage(@RequestParam("token") String token, Model model) {
        RegistrationSession session = sessionService.findBySessionToken(token)
            .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
            .orElseThrow(() -> new RuntimeException("Invalid or expired registration token"));

        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));

        // This is the initial entry point, let's send an event to move to the first step
        if (session.getCurrentState() == RegistrationState.PENDING_OAUTH) {
            sm.sendEvent(RegistrationEvent.START_PERSONAL);
            session.setCurrentState(sm.getState().getId());
            sessionService.save(session);
        }

        return "redirect:/register/" + sm.getState().getId().toString().toLowerCase() + "?token=" + token;
    }

    @GetMapping("/personal")
    public ModelAndView showPersonalForm(@RequestParam("token") String token) {
        sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        ModelAndView mav = new ModelAndView("register-personal");
        mav.addObject("personalDataDto", new PersonalDataDto());
        mav.addObject("token", token);
        return mav;
    }

    @PostMapping("/personal")
    public String submitPersonalForm(@RequestParam("token") String token, @Valid @ModelAttribute("personalDataDto") PersonalDataDto personalDataDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            return "register-personal";
        }
        RegistrationSession session = sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        personalInfoStrategy.process(session, personalDataDto);

        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        sm.sendEvent(RegistrationEvent.SUBMIT_PERSONAL);
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

        return "redirect:/register/" + sm.getState().getId().toString().toLowerCase() + "?token=" + token;
    }

    @GetMapping("/company")
    public ModelAndView showCompanyForm(@RequestParam("token") String token) {
        sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        ModelAndView mav = new ModelAndView("register-company");
        mav.addObject("companyDataDto", new CompanyDataDto());
        mav.addObject("token", token);
        return mav;
    }

    @PostMapping("/company")
    public String submitCompanyForm(@RequestParam("token") String token, @Valid @ModelAttribute("companyDataDto") CompanyDataDto companyDataDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            return "register-company";
        }
        RegistrationSession session = sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        companyInfoStrategy.process(session, companyDataDto);

        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        sm.sendEvent(RegistrationEvent.SUBMIT_COMPANY);
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

        return "redirect:/register/complete?token=" + token;
    }

    @PostMapping("/skip-company")
    public String skipCompanyStep(@RequestParam("token") String token) {
        RegistrationSession session = sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineFactory.getStateMachine();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> sma.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(session.getCurrentState(), null, null, null)));
        sm.sendEvent(RegistrationEvent.SKIP_COMPANY);
        session.setCurrentState(sm.getState().getId());
        sessionService.save(session);

        return "redirect:/register/complete?token=" + token;
    }

    @GetMapping("/complete")
    public ModelAndView showCompletionPage(@RequestParam("token") String token) {
        sessionService.findBySessionToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        return new ModelAndView("register-complete");
    }
}
