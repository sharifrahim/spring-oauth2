package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RegistrationStatusInterceptor registrationStatusInterceptor;

    public WebMvcConfig(RegistrationStatusInterceptor registrationStatusInterceptor) {
        this.registrationStatusInterceptor = registrationStatusInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(registrationStatusInterceptor)
                .addPathPatterns("/**") // Apply to all paths
                .excludePathPatterns(
                    "/", 
                    "/login", 
                    "/logout", 
                    "/register/**", 
                    "/error/**", 
                    "/oauth/**", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/favicon.ico", 
                    "/h2-console/**"
                );
    }
}