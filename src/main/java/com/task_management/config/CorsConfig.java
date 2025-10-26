package com.task_management.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private static final String DEFAULT_ALLOWED_ORIGIN = "https://task-management.exeltan.com";

    @Value("${app.cors.allowed-origins:" + DEFAULT_ALLOWED_ORIGIN + "}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/api/**")
                        .allowedOrigins(resolveAllowedOrigins())
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    private String[] resolveAllowedOrigins() {
        if (!StringUtils.hasText(allowedOrigins)) {
            return new String[] {DEFAULT_ALLOWED_ORIGIN};
        }
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
        return origins.length == 0 ? new String[] {DEFAULT_ALLOWED_ORIGIN} : origins;
    }
}
