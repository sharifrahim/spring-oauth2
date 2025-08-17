package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.AccountStatus;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.Account;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.AccountService;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.PersonalInfoStrategy;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.CompanyInfoStrategy;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<RegistrationState, RegistrationEvent> {

    @Autowired
    private PersonalInfoStrategy personalInfoStrategy;
    
    @Autowired
    private CompanyInfoStrategy companyInfoStrategy;
    
    @Autowired
    private AccountService accountService;

    @Override
    public void configure(StateMachineConfigurationConfigurer<RegistrationState, RegistrationEvent> config) throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(new org.springframework.statemachine.listener.StateMachineListenerAdapter<RegistrationState, RegistrationEvent>() {
                @Override
                public void stateMachineStarted(org.springframework.statemachine.StateMachine<RegistrationState, RegistrationEvent> stateMachine) {
                    System.out.println("StateMachine started with ID: " + stateMachine.getId());
                }
            });
    }

    @Override
    public void configure(StateMachineStateConfigurer<RegistrationState, RegistrationEvent> states) throws Exception {
        states
            .withStates()
            .initial(RegistrationState.PERSONAL_INFO)
            .states(EnumSet.of(RegistrationState.PERSONAL_INFO, RegistrationState.COMPANY_INFO, RegistrationState.COMPLETED));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<RegistrationState, RegistrationEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(RegistrationState.PERSONAL_INFO).target(RegistrationState.COMPANY_INFO)
                .event(RegistrationEvent.SUBMIT_PERSONAL)
                .guard(this::validatePersonalInfo)
                .action(this::processPersonalInfo)
                .action((context) -> System.out.println("=== TRANSITION: PERSONAL_INFO -> COMPANY_INFO ==="))
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.PERSONAL_INFO)
                .event(RegistrationEvent.START_PERSONAL) // Go back
                .action((context) -> System.out.println("=== TRANSITION: COMPANY_INFO -> PERSONAL_INFO (back) ==="))
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SUBMIT_COMPANY)
                .guard(this::validateCompanyInfo)
                .action(this::processCompanyInfo)
                .action(this::activateAccount)
                .action((context) -> System.out.println("=== TRANSITION: COMPANY_INFO -> COMPLETED ==="))
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SKIP_COMPANY)
                .action(this::activateAccount)
                .action((context) -> System.out.println("=== TRANSITION: COMPANY_INFO -> COMPLETED (skip) ==="));
    }

    private boolean validatePersonalInfo(StateContext<RegistrationState, RegistrationEvent> context) {
        System.out.println("validatePersonalInfo guard called!");
        try {
            Object personalData = context.getExtendedState().getVariables().get("personalData");
            Object session = context.getExtendedState().getVariables().get("session");
            System.out.println("personalData: " + personalData);
            System.out.println("session: " + session);
            if (personalData != null && session != null) {
                personalInfoStrategy.validate((com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession) session, personalData);
                System.out.println("Validation passed!");
                return true;
            }
            System.out.println("Data is null, validation failed");
            return false;
        } catch (Exception e) {
            System.out.println("Validation exception: " + e.getMessage());
            return false;
        }
    }

    private void processPersonalInfo(StateContext<RegistrationState, RegistrationEvent> context) {
        Object personalData = context.getExtendedState().getVariables().get("personalData");
        Object session = context.getExtendedState().getVariables().get("session");
        if (personalData != null && session != null) {
            personalInfoStrategy.process((com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession) session, personalData);
        }
    }

    private boolean validateCompanyInfo(StateContext<RegistrationState, RegistrationEvent> context) {
        System.out.println("=== VALIDATE COMPANY INFO GUARD CALLED ===");
        try {
            Object companyData = context.getExtendedState().getVariables().get("companyData");
            Object session = context.getExtendedState().getVariables().get("session");
            System.out.println("Company data: " + companyData);
            System.out.println("Session: " + session);
            
            if (companyData != null && session != null) {
                companyInfoStrategy.validate((com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession) session, companyData);
                System.out.println("✅ Company validation passed");
                return true;
            }
            System.err.println("❌ Company validation failed - data or session is null");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Company validation failed with exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void processCompanyInfo(StateContext<RegistrationState, RegistrationEvent> context) {
        Object companyData = context.getExtendedState().getVariables().get("companyData");
        Object session = context.getExtendedState().getVariables().get("session");
        if (companyData != null && session != null) {
            companyInfoStrategy.process((com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession) session, companyData);
        }
    }

    private void activateAccount(StateContext<RegistrationState, RegistrationEvent> context) {
        System.out.println("=== ACTIVATE ACCOUNT METHOD CALLED ===");
        Object sessionObj = context.getExtendedState().getVariables().get("session");
        System.out.println("Session object: " + sessionObj);
        
        if (sessionObj != null) {
            RegistrationSession session = (RegistrationSession) sessionObj;
            String email = session.getAccount().getEmail();
            System.out.println("Session email: " + email);
            try {
                // Find the account by email and activate it
                Account account = accountService.getAccountByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found for email: " + email));
                
                System.out.println("Found account with current status: " + account.getStatus());
                
                // Update account status to ACTIVE
                account.setStatus(AccountStatus.ACTIVE);
                account.setPendingExpiresAt(null); // Clear pending expiration
                accountService.saveAccount(account);
                
                System.out.println("✅ Account activated for email: " + email);
            } catch (Exception e) {
                System.err.println("❌ Failed to activate account: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to activate account", e);
            }
        } else {
            System.err.println("❌ Session object is null - cannot activate account");
        }
    }

    @Bean
    public StateMachineService<RegistrationState, RegistrationEvent> stateMachineService(
            org.springframework.statemachine.config.StateMachineFactory<RegistrationState, RegistrationEvent> stateMachineFactory) {
        return new DefaultStateMachineService<>(stateMachineFactory);
    }
}
