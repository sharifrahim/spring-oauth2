package com.github.sharifrahim.oauth2.boilerplate_oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationEvent;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.domain.RegistrationState;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;

@Service
public class StateMachineManager {

    @Autowired
    private StateMachineService<RegistrationState, RegistrationEvent> stateMachineService;

    @Autowired
    private RegistrationSessionService sessionService;

    /**
     * Acquires a StateMachine instance for the given session token and synchronizes
     * its state with the RegistrationSession's current state.
     */
    public StateMachine<RegistrationState, RegistrationEvent> acquireStateMachine(RegistrationSession session) {
        String machineId = session.getSessionToken();
        StateMachine<RegistrationState, RegistrationEvent> sm = stateMachineService.acquireStateMachine(machineId);
        
        // Synchronize StateMachine state with RegistrationSession state
        if (session.getCurrentState() != null && sm.getState().getId() != session.getCurrentState()) {
            System.out.println("Synchronizing StateMachine state to: " + session.getCurrentState());
            sm.getStateMachineAccessor().doWithAllRegions(sma -> 
                sma.resetStateMachine(new DefaultStateMachineContext<>(session.getCurrentState(), null, null, null))
            );
        }
        
        // Always put session in extended state for access in guards/actions
        sm.getExtendedState().getVariables().put("session", session);
        
        return sm;
    }

    /**
     * Releases the StateMachine back to the service and updates the RegistrationSession
     * with the current StateMachine state.
     */
    public void releaseStateMachine(StateMachine<RegistrationState, RegistrationEvent> sm, RegistrationSession session) {
        // Update session state from StateMachine state
        RegistrationState currentState = sm.getState().getId();
        if (session.getCurrentState() != currentState) {
            System.out.println("Updating session state from " + session.getCurrentState() + " to " + currentState);
            session.setCurrentState(currentState);
            sessionService.save(session);
        }
        
        // Release the StateMachine
        stateMachineService.releaseStateMachine(session.getSessionToken());
    }

    /**
     * Sends an event to the StateMachine and handles state synchronization.
     */
    public boolean sendEvent(RegistrationSession session, RegistrationEvent event, Object eventData) {
        StateMachine<RegistrationState, RegistrationEvent> sm = acquireStateMachine(session);
        
        try {
            // Put event data in extended state if provided
            if (eventData != null) {
                String dataKey = getDataKeyForEvent(event);
                sm.getExtendedState().getVariables().put(dataKey, eventData);
            }
            
            System.out.println("Sending event " + event + " to StateMachine in state " + sm.getState().getId());
            boolean success = sm.sendEvent(event);
            System.out.println("Event result: " + success + ", new state: " + sm.getState().getId());
            
            return success;
        } finally {
            releaseStateMachine(sm, session);
        }
    }

    private String getDataKeyForEvent(RegistrationEvent event) {
        switch (event) {
            case SUBMIT_PERSONAL:
                return "personalData";
            case SUBMIT_COMPANY:
                return "companyData";
            default:
                return "eventData";
        }
    }
}