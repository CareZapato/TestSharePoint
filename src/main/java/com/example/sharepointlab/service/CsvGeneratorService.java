package com.example.sharepointlab.service;

import com.example.sharepointlab.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generación de archivos CSV con reportes de pagos
 */
@Service
@Slf4j
public class CsvGeneratorService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final DateTimeFormatter FILE_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    
    /**
     * Genera un archivo CSV con los pagos proporcionados
     * 
     * @param payments Lista de pagos a incluir en el reporte
     * @param outputDirectory Directorio donde guardar el archivo
     * @return Ruta completa del archivo generado
     */
    public Path generateCsvReport(List<Payment> payments, String outputDirectory) throws IOException {
        // Crear directorio si no existe
        Path dirPath = Paths.get(outputDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            log.info("Directorio creado: {}", dirPath);
        }
        
        // Generar nombre de archivo con timestamp
        String fileName = String.format("reporte-pagos-%s.csv", 
            LocalDateTime.now().format(FILE_DATE_FORMATTER));
        Path filePath = dirPath.resolve(fileName);
        
        // Generar contenido CSV
        String csvContent = generateCsvContent(payments);
        
        // Escribir archivo
        Files.writeString(filePath, csvContent);
        log.info("Archivo CSV generado: {}", filePath);
        
        return filePath;
    }
    
    /**
     * Genera el contenido CSV como String
     */
    public String generateCsvContent(List<Payment> payments) throws IOException {
        StringWriter sw = new StringWriter();
        
        // Configurar formato CSV
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(
                "RUT",
                "Email",
                "Poliza",
                "Prima",
                "Cuota",
                "Monto",
                "Canal",
                "Pasarela",
                "Estado",
                "Imputado",
                "Fecha Pago",
                "Fecha Imputacion",
                "Descripcion"
            )
            .build();
        
        try (CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            for (Payment payment : payments) {
                printer.printRecord(
                    payment.getRut(),
                    payment.getEmail(),
                    payment.getPoliza(),
                    payment.getPrima(),
                    payment.getCuotaNumero(),
                    payment.getMonto(),
                    payment.getCanal(),
                    payment.getPasarela(),
                    payment.getEstadoCobro(),
                    payment.getImputado() ? "SI" : "NO",
                    payment.getFechaPago() != null ? 
                        payment.getFechaPago().format(DATE_FORMATTER) : "",
                    payment.getFechaImputacion() != null ? 
                        payment.getFechaImputacion().format(DATE_FORMATTER) : "",
                    payment.getDescripcion()
                );
            }
        }
        
        return sw.toString();
    }
    
    /**
     * Genera nombre de archivo con timestamp actual
     */
    public String generateFileName() {
        return String.format("reporte-pagos-%s.csv", 
            LocalDateTime.now().format(FILE_DATE_FORMATTER));
    }
}
