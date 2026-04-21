package com.example.sharepointlab.controller;

import com.example.sharepointlab.model.Payment;
import com.example.sharepointlab.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operaciones relacionadas con pagos
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * GET /api/payments/mock
     * Carga y retorna todos los datos mock
     */
    @GetMapping("/mock")
    public ResponseEntity<Map<String, Object>> loadMockData() {
        log.info("Cargando datos mock...");
        
        List<Payment> payments = paymentService.loadMockData();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Datos mock cargados exitosamente");
        response.put("count", payments.size());
        response.put("data", payments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/payments
     * Obtiene todos los pagos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayments() {
        log.info("Obteniendo todos los pagos...");
        
        List<Payment> payments = paymentService.getAllPayments();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/payments/by-rut/{rut}
     * Busca pagos por RUT
     */
    @GetMapping("/by-rut/{rut}")
    public ResponseEntity<Map<String, Object>> getPaymentsByRut(@PathVariable String rut) {
        log.info("Buscando pagos para RUT: {}", rut);
        
        List<Payment> payments = paymentService.getPaymentsByRut(rut);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("rut", rut);
        response.put("count", payments.size());
        response.put("data", payments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/payments/pending/{rut}
     * Obtiene cuotas pendientes por RUT
     */
    @GetMapping("/pending/{rut}")
    public ResponseEntity<Map<String, Object>> getPendingQuotas(@PathVariable String rut) {
        log.info("Obteniendo cuotas pendientes para RUT: {}", rut);
        
        List<Payment> pendingPayments = paymentService.getPendingQuotasByRut(rut);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("rut", rut);
        response.put("count", pendingPayments.size());
        response.put("data", pendingPayments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/payments/scenario/{scenarioName}
     * Aplica un escenario de simulación predefinido
     */
    @PostMapping("/scenario/{scenarioName}")
    public ResponseEntity<Map<String, Object>> applyScenario(@PathVariable String scenarioName) {
        log.info("Aplicando escenario: {}", scenarioName);
        
        try {
            List<Payment> payments = paymentService.applyScenario(scenarioName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Escenario aplicado: " + scenarioName);
            response.put("scenario", scenarioName);
            response.put("count", payments.size());
            response.put("data", payments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al aplicar escenario: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al aplicar escenario: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
