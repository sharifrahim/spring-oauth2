package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<RegistrationState, RegistrationEvent> {

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
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.PERSONAL_INFO)
                .event(RegistrationEvent.START_PERSONAL) // Go back
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SUBMIT_COMPANY)
                .and()
            .withExternal()
                .source(RegistrationState.COMPANY_INFO).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.SKIP_COMPANY)
                .and()
            .withExternal()
                .source(RegistrationState.COMPLETED).target(RegistrationState.COMPLETED)
                .event(RegistrationEvent.COMPLETE);
    }
}
