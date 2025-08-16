package com.github.sharifrahim.oauth2.boilerplate_oauth2.service.registration;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.model.entity.RegistrationSession;

public interface RegistrationStrategy {
    void validate(RegistrationSession session, Object data) throws Exception;
    void process(RegistrationSession session, Object data);
}
