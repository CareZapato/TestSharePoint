package com.example.sharepointlab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio que representa un pago de seguro
 * Incluye información del cliente, póliza, y estado del pago e imputación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    /**
     * RUT del cliente (ej: 12345678-9)
     */
    private String rut;
    
    /**
     * Email del cliente
     */
    private String email;
    
    /**
     * Número de póliza
     */
    private String poliza;
    
    /**
     * Prima del seguro
     */
    private BigDecimal prima;
    
    /**
     * Número de cuota
     */
    private Integer cuotaNumero;
    
    /**
     * Monto del pago
     */
    private BigDecimal monto;
    
    /**
     * Canal de pago: PAGO_EN_LINEA o PAGO_AUTOMATICO
     */
    private CanalPago canal;
    
    /**
     * Pasarela de pago (ej: FLOW)
     */
    private String pasarela;
    
    /**
     * Estado del cobro: PENDIENTE, CONFIRMADO, RECHAZADO
     */
    private EstadoCobro estadoCobro;
    
    /**
     * Indica si el pago fue imputado (registrado en caja)
     */
    private Boolean imputado;
    
    /**
     * Fecha y hora del pago
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaPago;
    
    /**
     * Fecha y hora de imputación
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaImputacion;
    
    /**
     * Descripción adicional
     */
    private String descripcion;
    
    /**
     * Canal de pago
     */
    public enum CanalPago {
        PAGO_EN_LINEA,
        PAGO_AUTOMATICO
    }
    
    /**
     * Estado del cobro
     */
    public enum EstadoCobro {
        PENDIENTE,
        CONFIRMADO,
        RECHAZADO
    }
}
