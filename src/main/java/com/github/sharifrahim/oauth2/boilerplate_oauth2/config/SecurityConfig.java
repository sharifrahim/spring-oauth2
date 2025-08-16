package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationFailureHandler;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SecurityConfig(RateLimitFilter rateLimitFilter, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.rateLimitFilter = rateLimitFilter;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        
    	http
        .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(a -> 
            a.requestMatchers("/", "/error","/login","/logout", "/oauth-error", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/h2-console/**").permitAll()
             .anyRequest().authenticated()
        )
        .exceptionHandling(e -> 
        	e.authenticationEntryPoint((request, response, authException) -> 
        	response.sendRedirect("/login"))
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
            .disable())
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
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
            )
        .headers(headers -> headers.frameOptions().sameOrigin());

    return http.build();
    
    }
}


