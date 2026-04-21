package com.example.sharepointlab.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.example.sharepointlab.config.SharePointConfig;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de integración con SharePoint usando Microsoft Graph API
 * Permite subir archivos a una biblioteca de documentos de SharePoint
 */
@Service
@Slf4j
public class SharePointService {
    
    private final SharePointConfig config;
    private GraphServiceClient<Request> graphClient;
    
    @Value("${app.local-reports-path}")
    private String localReportsPath;
    
    public SharePointService(SharePointConfig config) {
        this.config = config;
    }
    
    /**
     * Inicializa el cliente de Graph API si la configuración está completa
     */
    private void initializeGraphClient() {
        if (!config.isConfigured()) {
            log.warn("SharePoint no está configurado completamente");
            return;
        }
        
        if (graphClient == null) {
            log.info("Inicializando cliente de Microsoft Graph...");
            
            try {
                // Crear credenciales con Client Secret
                ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .clientId(config.getClientId())
                    .clientSecret(config.getClientSecret())
                    .tenantId(config.getTenantId())
                    .build();
                
                // Crear proveedor de autenticación
                TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                    Arrays.asList("https://graph.microsoft.com/.default"),
                    credential
                );
                
                // Crear cliente de Graph
                graphClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
                
                log.info("Cliente de Microsoft Graph inicializado correctamente");
            } catch (Exception e) {
                log.error("Error al inicializar cliente de Graph: {}", e.getMessage());
                graphClient = null;
            }
        }
    }
    
    /**
     * Sube un archivo a SharePoint
     * 
     * @param filePath Ruta del archivo local a subir
     * @return Mapa con información del archivo subido (url, webUrl, etc.)
     */
    public Map<String, String> uploadFile(Path filePath) throws IOException {
        Map<String, String> result = new HashMap<>();
        
        // Si SharePoint no está configurado, guardar localmente
        if (!config.isConfigured()) {
            log.warn("SharePoint no configurado. Archivo guardado localmente en: {}", filePath);
            result.put("status", "local");
            result.put("message", "SharePoint no configurado. Archivo guardado localmente.");
            result.put("localPath", filePath.toString());
            return result;
        }
        
        // Inicializar cliente si es necesario
        initializeGraphClient();
        
        if (graphClient == null) {
            log.error("No se pudo inicializar el cliente de Graph");
            result.put("status", "error");
            result.put("message", "Error al conectar con SharePoint");
            result.put("localPath", filePath.toString());
            return result;
        }
        
        try {
            String fileName = filePath.getFileName().toString();
            long fileSize = Files.size(filePath);
            
            log.info("Subiendo archivo a SharePoint: {} ({} bytes)", fileName, fileSize);
            
            // Construir la ruta en SharePoint
            String itemPath = config.getFolderPath() + "/" + fileName;
            
            // Para archivos pequeños (< 4MB), usar upload simple
            if (fileSize < 4 * 1024 * 1024) {
                DriveItem uploadedItem = uploadSmallFile(filePath, itemPath);
                
                result.put("status", "success");
                result.put("message", "Archivo subido exitosamente a SharePoint");
                result.put("fileName", fileName);
                result.put("itemId", uploadedItem.id);
                result.put("webUrl", uploadedItem.webUrl);
                result.put("size", String.valueOf(fileSize));
                
                log.info("Archivo subido exitosamente. WebUrl: {}", uploadedItem.webUrl);
            } else {
                // Para archivos grandes, usar upload session
                result.put("status", "error");
                result.put("message", "Archivos grandes no implementados en este demo");
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("Error al subir archivo a SharePoint: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "Error: " + e.getMessage());
            result.put("localPath", filePath.toString());
            return result;
        }
    }
    
    /**
     * Sube un archivo pequeño (< 4MB) a SharePoint
     */
    private DriveItem uploadSmallFile(Path filePath, String itemPath) throws IOException {
        byte[] fileContent = Files.readAllBytes(filePath);
        
        // PUT /sites/{site-id}/drives/{drive-id}/root:/{item-path}:/content
        DriveItem item = graphClient
            .sites(config.getSiteId())
            .drives(config.getDriveId())
            .root()
            .itemWithPath(itemPath)
            .content()
            .buildRequest()
            .put(fileContent);
        
        return item;
    }
    
    /**
     * Verifica si SharePoint está configurado y disponible
     */
    public boolean isAvailable() {
        return config.isConfigured();
    }
    
    /**
     * Obtiene el estado de la configuración de SharePoint
     */
    public Map<String, Object> getConfigStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", config.isEnabled());
        status.put("configured", config.isConfigured());
        status.put("tenantId", config.getTenantId() != null && !config.getTenantId().isEmpty() ? "✓" : "✗");
        status.put("clientId", config.getClientId() != null && !config.getClientId().isEmpty() ? "✓" : "✗");
        status.put("clientSecret", config.getClientSecret() != null && !config.getClientSecret().isEmpty() ? "✓" : "✗");
        status.put("siteId", config.getSiteId() != null && !config.getSiteId().isEmpty() ? "✓" : "✗");
        status.put("driveId", config.getDriveId() != null && !config.getDriveId().isEmpty() ? "✓" : "✗");
        status.put("folderPath", config.getFolderPath());
        
        return status;
    }
}
