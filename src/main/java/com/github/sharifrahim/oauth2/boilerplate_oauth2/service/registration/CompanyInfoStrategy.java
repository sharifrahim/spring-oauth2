package com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.service.RegistrationSessionService;

@Component("companyInfoStrategy")
public class CompanyInfoStrategy implements RegistrationStrategy {

    private final ObjectMapper objectMapper;
    private final RegistrationSessionService sessionService;

    @Autowired
    public CompanyInfoStrategy(ObjectMapper objectMapper, RegistrationSessionService sessionService) {
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    @Override
    public void validate(RegistrationSession session, Object data) throws Exception {
        // Validation logic will be implemented in Phase 6 with DTOs
    }

    @Override
    public void process(RegistrationSession session, Object data) {
        System.out.println("=== COMPANY INFO STRATEGY PROCESS CALLED ===");
        System.out.println("Session: " + session);
        System.out.println("Data: " + data);
        try {
            String companyDataJson = objectMapper.writeValueAsString(data);
            System.out.println("Company data JSON: " + companyDataJson);
            session.setCompanyData(companyDataJson);
            // Save the session to persist the company data
            sessionService.save(session);
            System.out.println("✅ Company data saved successfully");
        } catch (Exception e) {
            System.err.println("❌ Error processing company info: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error processing company info", e);
        }
    }
}
