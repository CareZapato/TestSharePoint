package com.example.sharepointlab.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de SharePoint cargada desde application.yml o variables de entorno
 */
@Configuration
@ConfigurationProperties(prefix = "app.sharepoint")
@Getter
@Setter
public class SharePointConfig {
    
    /**
     * Indica si la integración con SharePoint está habilitada
     */
    private boolean enabled = false;
    
    /**
     * Tenant ID de Azure AD
     */
    private String tenantId;
    
    /**
     * Client ID de la aplicación registrada en Azure AD
     */
    private String clientId;
    
    /**
     * Client Secret de la aplicación
     */
    private String clientSecret;
    
    /**
     * ID del sitio de SharePoint
     */
    private String siteId;
    
    /**
     * ID del drive (biblioteca de documentos)
     */
    private String driveId;
    
    /**
     * Ruta de la carpeta destino en SharePoint
     */
    private String folderPath = "Reportes";
    
    /**
     * Valida si la configuración está completa para conectar a SharePoint
     */
    public boolean isConfigured() {
        return enabled 
            && tenantId != null && !tenantId.isEmpty()
            && clientId != null && !clientId.isEmpty()
            && clientSecret != null && !clientSecret.isEmpty()
            && siteId != null && !siteId.isEmpty()
            && driveId != null && !driveId.isEmpty();
    }
}
