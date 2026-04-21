package com.example.sharepointlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para laboratorio de integración con SharePoint
 * Simula un sistema de pagos de seguros con generación de reportes CSV
 */
@SpringBootApplication
public class SharepointLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharepointLabApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("SharePoint Payment Lab iniciado!");
        System.out.println("Accede a: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
