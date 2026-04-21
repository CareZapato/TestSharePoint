package com.example.sharepointlab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web de la aplicación
 * Habilita CORS para desarrollo local
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:" + serverPort,
                    "http://127.0.0.1:" + serverPort
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
