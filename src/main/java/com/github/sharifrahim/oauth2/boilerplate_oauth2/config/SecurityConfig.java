package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationSuccessHandler;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final SecurityProperties securityProperties;

    public SecurityConfig(
            RateLimitFilter rateLimitFilter,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
            SecurityProperties securityProperties) {
        this.rateLimitFilter = rateLimitFilter;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.securityProperties = securityProperties;
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        boolean allowH2Console = securityProperties.isAllowH2Console();

        String[] publicEndpoints = allowH2Console
                ? new String[]{"/", "/error", "/login", "/logout", "/oauth-error", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/h2-console/**"}
                : new String[]{"/", "/error", "/login", "/logout", "/oauth-error", "/css/**", "/js/**", "/images/**", "/favicon.ico"};

        http
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(publicEndpoints).permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint((request, response, authException) ->
                            response.sendRedirect("/login"))
            )
            .csrf(csrf -> {
                if (allowH2Console) {
                    csrf.ignoringRequestMatchers("/h2-console/**");
                }
            })
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            )
            .headers(headers -> {
                if (allowH2Console) {
                    headers.frameOptions(frameOptions -> frameOptions.sameOrigin());
                }
            });

        return http.build();
    }
}
