package com.example.sharepointlab.controller;

import com.example.sharepointlab.model.Payment;
import com.example.sharepointlab.service.CsvGeneratorService;
import com.example.sharepointlab.service.PaymentService;
import com.example.sharepointlab.service.SharePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para generación de reportes y envío a SharePoint
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final PaymentService paymentService;
    private final CsvGeneratorService csvGeneratorService;
    private final SharePointService sharePointService;
    
    @Value("${app.local-reports-path}")
    private String localReportsPath;
    
    /**
     * POST /api/reports/csv
     * Genera un archivo CSV con los pagos actuales
     */
    @PostMapping("/csv")
    public ResponseEntity<Map<String, Object>> generateCsvReport() {
        log.info("Generando reporte CSV...");
        
        try {
            // Obtener pagos actuales
            List<Payment> payments = paymentService.getAllPayments();
            
            if (payments.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No hay datos para generar el reporte");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generar CSV
            Path csvFile = csvGeneratorService.generateCsvReport(payments, localReportsPath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reporte CSV generado exitosamente");
            response.put("fileName", csvFile.getFileName().toString());
            response.put("filePath", csvFile.toString());
            response.put("recordCount", payments.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al generar reporte CSV: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al generar CSV: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * POST /api/reports/upload-sharepoint
     * Genera CSV y lo sube a SharePoint (o guarda localmente si no está configurado)
     */
    @PostMapping("/upload-sharepoint")
    public ResponseEntity<Map<String, Object>> uploadToSharePoint() {
        log.info("Generando y subiendo reporte a SharePoint...");
        
        try {
            // Obtener pagos actuales
            List<Payment> payments = paymentService.getAllPayments();
            
            if (payments.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No hay datos para generar el reporte");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generar CSV localmente primero
            Path csvFile = csvGeneratorService.generateCsvReport(payments, localReportsPath);
            
            // Intentar subir a SharePoint
            Map<String, String> uploadResult = sharePointService.uploadFile(csvFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileName", csvFile.getFileName().toString());
            response.put("recordCount", payments.size());
            response.put("sharepoint", uploadResult);
            
            if ("success".equals(uploadResult.get("status"))) {
                response.put("message", "Reporte generado y subido a SharePoint exitosamente");
            } else if ("local".equals(uploadResult.get("status"))) {
                response.put("message", "SharePoint no configurado. Reporte guardado localmente.");
            } else {
                response.put("message", "Reporte generado localmente, pero falló la subida a SharePoint");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al generar/subir reporte: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * GET /api/reports/sharepoint-status
     * Verifica el estado de la configuración de SharePoint
     */
    @GetMapping("/sharepoint-status")
    public ResponseEntity<Map<String, Object>> getSharePointStatus() {
        log.info("Verificando estado de SharePoint...");
        
        Map<String, Object> status = sharePointService.getConfigStatus();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }
}
