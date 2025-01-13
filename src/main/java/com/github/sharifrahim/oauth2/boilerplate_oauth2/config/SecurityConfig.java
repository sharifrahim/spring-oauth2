package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        
    	http
        .authorizeHttpRequests(a -> 
            a.requestMatchers("/", "/error","/login","/logout").permitAll()
             .anyRequest().authenticated()
        )
        .exceptionHandling(e -> 
        	e.authenticationEntryPoint((request, response, authException) -> 
        	response.sendRedirect("/login"))
        )
        .csrf(csrf -> csrf.disable())
        //.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .logout(l -> l
                .logoutUrl("/logout") // URL for logging out
                .logoutSuccessUrl("/") // Redirect to home page after logout
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID") // Delete cookies
                .permitAll() // Allow everyone to access /logout
            )
        .oauth2Login(o -> o
        		.loginPage("/login")
                .defaultSuccessUrl("/dashboard", true) // Redirect to /dashboard upon successful login
            );

    return http.build();
    
    }
}


