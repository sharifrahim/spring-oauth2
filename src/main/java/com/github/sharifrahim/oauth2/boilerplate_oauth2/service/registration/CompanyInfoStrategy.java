package com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;

@Component("companyInfoStrategy")
public class CompanyInfoStrategy implements RegistrationStrategy {

    private final ObjectMapper objectMapper;

    @Autowired
    public CompanyInfoStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void validate(RegistrationSession session, Object data) throws Exception {
        // Validation logic will be implemented in Phase 6 with DTOs
    }

    @Override
    public void process(RegistrationSession session, Object data) {
        try {
            String companyDataJson = objectMapper.writeValueAsString(data);
            session.setCompanyData(companyDataJson);
        } catch (Exception e) {
            throw new RuntimeException("Error processing company info", e);
        }
    }
}
