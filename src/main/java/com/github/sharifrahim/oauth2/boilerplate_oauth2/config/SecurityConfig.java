package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.GoogleLogoutHandler;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationFailureHandler;
import com.github.sharifrahim.oauth2.boilerplate_oauth2.config.security.OAuth2AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final GoogleLogoutHandler googleLogoutHandler;

    public SecurityConfig(
            RateLimitFilter rateLimitFilter,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
            GoogleLogoutHandler googleLogoutHandler) {
        this.rateLimitFilter = rateLimitFilter;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.googleLogoutHandler = googleLogoutHandler;
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        String[] publicEndpoints = {
                "/",
                "/login",
                "/index.html",
                "/assets/**",
                "/favicon.ico",
                "/manifest.json",
                "/robots.txt",
                "/api/login/**",
                "/actuator/health",
                "/actuator/health/**"
        };

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(publicEndpoints).permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/**").permitAll()
                    .anyRequest().denyAll()
            )
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling
                            .authenticationEntryPoint((request, response, authException) -> {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.getWriter().write("""
                                        {"code":"unauthorized","message":"Authentication required"}
                                        """);
                            })
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.getWriter().write("""
                                        {"code":"forbidden","message":"Access denied"}
                                        """);
                            })
            )
            .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .addLogoutHandler(googleLogoutHandler)
                    .addLogoutHandler(new SecurityContextLogoutHandler())
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                    })
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .headers(headers -> headers
                    .cacheControl(cache -> cache.disable())
            )
            .oauth2Login(oauth2 -> oauth2
                    .authorizationEndpoint(authorization -> authorization.baseUri("/api/login"))
                    .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://127.0.0.1:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Location");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
