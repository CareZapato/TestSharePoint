package com.example.sharepointlab.service;

import com.example.sharepointlab.model.Payment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de pagos
 * Simula una base de datos usando archivos JSON
 */
@Service
@Slf4j
public class PaymentService {
    
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private List<Payment> paymentsDatabase;
    
    @Value("${app.mock-data-path}")
    private String mockDataPath;
    
    public PaymentService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.paymentsDatabase = new ArrayList<>();
    }
    
    /**
     * Carga los datos mock desde el archivo JSON
     */
    public List<Payment> loadMockData() {
        try {
            Resource resource = resourceLoader.getResource(mockDataPath);
            paymentsDatabase = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<Payment>>() {}
            );
            log.info("Cargados {} pagos desde archivo mock", paymentsDatabase.size());
            return new ArrayList<>(paymentsDatabase);
        } catch (IOException e) {
            log.error("Error al cargar datos mock: {}", e.getMessage());
            paymentsDatabase = createDefaultMockData();
            return new ArrayList<>(paymentsDatabase);
        }
    }
    
    /**
     * Obtiene todos los pagos
     */
    public List<Payment> getAllPayments() {
        if (paymentsDatabase.isEmpty()) {
            loadMockData();
        }
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Busca pagos por RUT
     */
    public List<Payment> getPaymentsByRut(String rut) {
        return getAllPayments().stream()
            .filter(p -> p.getRut().equals(rut))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene cuotas pendientes por RUT
     */
    public List<Payment> getPendingQuotasByRut(String rut) {
        return getAllPayments().stream()
            .filter(p -> p.getRut().equals(rut))
            .filter(p -> p.getEstadoCobro() == Payment.EstadoCobro.PENDIENTE)
            .collect(Collectors.toList());
    }
    
    /**
     * Aplica un escenario de simulación predefinido
     */
    public List<Payment> applyScenario(String scenarioName) {
        log.info("Aplicando escenario: {}", scenarioName);
        
        switch (scenarioName.toLowerCase()) {
            case "pago-online-confirmado":
                return createOnlinePaymentScenario();
            case "pago-automatico-confirmado":
                return createAutomaticPaymentScenario();
            case "pago-rechazado":
                return createRejectedPaymentScenario();
            case "lote-confirmados":
                return createConfirmedBatchScenario();
            case "cuotas-pendientes":
                return createPendingQuotasScenario();
            default:
                return loadMockData();
        }
    }
    
    /**
     * Escenario: Pago en línea confirmado e imputado
     */
    private List<Payment> createOnlinePaymentScenario() {
        paymentsDatabase = new ArrayList<>();
        paymentsDatabase.add(Payment.builder()
            .rut("12345678-9")
            .email("cliente1@example.com")
            .poliza("POL-2024-001")
            .prima(new BigDecimal("50000"))
            .cuotaNumero(1)
            .monto(new BigDecimal("50000"))
            .canal(Payment.CanalPago.PAGO_EN_LINEA)
            .pasarela("FLOW")
            .estadoCobro(Payment.EstadoCobro.CONFIRMADO)
            .imputado(true)
            .fechaPago(LocalDateTime.now().minusHours(2))
            .fechaImputacion(LocalDateTime.now().minusHours(1))
            .descripcion("Pago en línea confirmado e imputado exitosamente")
            .build());
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Escenario: Pago automático confirmado e imputado
     */
    private List<Payment> createAutomaticPaymentScenario() {
        paymentsDatabase = new ArrayList<>();
        paymentsDatabase.add(Payment.builder()
            .rut("98765432-1")
            .email("cliente2@example.com")
            .poliza("POL-2024-002")
            .prima(new BigDecimal("75000"))
            .cuotaNumero(3)
            .monto(new BigDecimal("75000"))
            .canal(Payment.CanalPago.PAGO_AUTOMATICO)
            .pasarela("FLOW")
            .estadoCobro(Payment.EstadoCobro.CONFIRMADO)
            .imputado(true)
            .fechaPago(LocalDateTime.now().minusDays(1))
            .fechaImputacion(LocalDateTime.now().minusHours(12))
            .descripcion("Pago automático procesado correctamente")
            .build());
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Escenario: Pago rechazado
     */
    private List<Payment> createRejectedPaymentScenario() {
        paymentsDatabase = new ArrayList<>();
        paymentsDatabase.add(Payment.builder()
            .rut("11223344-5")
            .email("cliente3@example.com")
            .poliza("POL-2024-003")
            .prima(new BigDecimal("100000"))
            .cuotaNumero(2)
            .monto(new BigDecimal("100000"))
            .canal(Payment.CanalPago.PAGO_EN_LINEA)
            .pasarela("FLOW")
            .estadoCobro(Payment.EstadoCobro.RECHAZADO)
            .imputado(false)
            .fechaPago(LocalDateTime.now().minusHours(1))
            .fechaImputacion(null)
            .descripcion("Pago rechazado por fondos insuficientes")
            .build());
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Escenario: Lote de pagos confirmados para reporte
     */
    private List<Payment> createConfirmedBatchScenario() {
        paymentsDatabase = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.now().minusDays(1);
        
        for (int i = 1; i <= 5; i++) {
            paymentsDatabase.add(Payment.builder()
                .rut(String.format("%08d-%d", 10000000 + i, i % 10))
                .email(String.format("cliente%d@example.com", i))
                .poliza(String.format("POL-2024-%03d", i))
                .prima(new BigDecimal(50000 + (i * 10000)))
                .cuotaNumero(i)
                .monto(new BigDecimal(50000 + (i * 10000)))
                .canal(i % 2 == 0 ? Payment.CanalPago.PAGO_AUTOMATICO : Payment.CanalPago.PAGO_EN_LINEA)
                .pasarela("FLOW")
                .estadoCobro(Payment.EstadoCobro.CONFIRMADO)
                .imputado(true)
                .fechaPago(baseDate.plusHours(i))
                .fechaImputacion(baseDate.plusHours(i + 1))
                .descripcion(String.format("Pago confirmado - lote %d", i))
                .build());
        }
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Escenario: Cuotas pendientes por RUT
     */
    private List<Payment> createPendingQuotasScenario() {
        paymentsDatabase = new ArrayList<>();
        String rut = "15555666-7";
        
        for (int i = 1; i <= 3; i++) {
            paymentsDatabase.add(Payment.builder()
                .rut(rut)
                .email("cliente.pendiente@example.com")
                .poliza("POL-2024-999")
                .prima(new BigDecimal("60000"))
                .cuotaNumero(i)
                .monto(new BigDecimal("60000"))
                .canal(Payment.CanalPago.PAGO_EN_LINEA)
                .pasarela("FLOW")
                .estadoCobro(Payment.EstadoCobro.PENDIENTE)
                .imputado(false)
                .fechaPago(null)
                .fechaImputacion(null)
                .descripcion(String.format("Cuota %d pendiente de pago", i))
                .build());
        }
        return new ArrayList<>(paymentsDatabase);
    }
    
    /**
     * Crea datos mock por defecto si no se puede cargar el archivo
     */
    private List<Payment> createDefaultMockData() {
        log.info("Creando datos mock por defecto");
        List<Payment> defaultData = new ArrayList<>();
        
        defaultData.add(Payment.builder()
            .rut("12345678-9")
            .email("cliente1@example.com")
            .poliza("POL-2024-001")
            .prima(new BigDecimal("50000"))
            .cuotaNumero(1)
            .monto(new BigDecimal("50000"))
            .canal(Payment.CanalPago.PAGO_EN_LINEA)
            .pasarela("FLOW")
            .estadoCobro(Payment.EstadoCobro.CONFIRMADO)
            .imputado(true)
            .fechaPago(LocalDateTime.now().minusDays(1))
            .fechaImputacion(LocalDateTime.now().minusHours(12))
            .descripcion("Pago confirmado y procesado")
            .build());
        
        defaultData.add(Payment.builder()
            .rut("98765432-1")
            .email("cliente2@example.com")
            .poliza("POL-2024-002")
            .prima(new BigDecimal("75000"))
            .cuotaNumero(1)
            .monto(new BigDecimal("75000"))
            .canal(Payment.CanalPago.PAGO_AUTOMATICO)
            .pasarela("FLOW")
            .estadoCobro(Payment.EstadoCobro.PENDIENTE)
            .imputado(false)
            .fechaPago(null)
            .fechaImputacion(null)
            .descripcion("Pago pendiente")
            .build());
        
        return defaultData;
    }
}
