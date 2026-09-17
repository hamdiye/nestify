package com.nestify.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Global CORS configuration.
 * Configures allowed origins for cross-origin requests to the backend API.
 */
// @Configuration — SecurityConfig içindeki CORS konfigürasyonu kullanıldığından bu sınıf devre dışı bırakıldı.
// WebMvcConfigurer CORS'u Spring Security CORS filtresinden sonra devreye girdiği için çakışmaya neden oluyordu.
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:5173,https://nestify.hamdiyecicek.tech}")
    private String allowedOrigins;

    /**
     * Configures CORS mappings for the application.
     *
     * @return WebMvcConfigurer instance with CORS rules applied
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Adds CORS mapping for all API endpoints.
             *
             * @param registry the CORS registry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);

                registry.addMapping("/api/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
