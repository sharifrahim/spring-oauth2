package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.PersonalInfoStrategy;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration.CompanyInfoStrategy;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<RegistrationState, RegistrationEvent> {

    @Autowired
    private PersonalInfoStrategy personalInfoStrategy;
    
    @Autowired
    private CompanyInfoStrategy companyInfoStrategy;

    @Override
    public void configure(StateMachineStateConfigurer<RegistrationState, RegistrationEvent> states) throws Exception {
        states
            .withStates()
            .initial(RegistrationState.PENDING_OAUTH)
            .states(EnumSet.allOf(RegistrationState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<RegistrationState, RegistrationEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(RegistrationState.PENDING_OAUTH).target(RegistrationState.PERSONAL_INFO)
                .event(RegistrationEvent.START_PERSONAL)
                .and()
            .withExternal()
                .source(RegistrationState.PERSONAL_INFO).target(RegistrationState.COMPANY_INFO)
                .event(RegistrationEvent.SUBMIT_PERSONAL)
                .guard(this::validatePersonalInfo)
                .action(this::processPersonalInfo)
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.PERSONAL_INFO)
                .event(RegistrationEvent.START_PERSONAL) // Go back
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SUBMIT_COMPANY)
                .guard(this::validateCompanyInfo)
                .action(this::processCompanyInfo)
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SKIP_COMPANY)
                .and()
            .withExternal()
                .source(RegistrationState.COMPLETED).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.COMPLETE);
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
        try {
            Object companyData = context.getExtendedState().getVariables().get("companyData");
            Object session = context.getExtendedState().getVariables().get("session");
            if (companyData != null && session != null) {
                companyInfoStrategy.validate((com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession) session, companyData);
                return true;
            }
            return false;
        } catch (Exception e) {
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
}
