package com.github.sharifrahim.oauth2.boilerplate_oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApiRateLimitInterceptor apiRateLimitInterceptor;

    public WebMvcConfig(ApiRateLimitInterceptor apiRateLimitInterceptor) {
        this.apiRateLimitInterceptor = apiRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiRateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login/**", "/actuator/**");
    }
}
