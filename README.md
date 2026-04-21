# SharePoint Payment Lab 📊

Laboratorio completo para simular un sistema de pagos de seguros con integración a SharePoint. Este proyecto permite experimentar con el flujo completo de recaudación, confirmación, imputación y generación de reportes CSV enviados a SharePoint usando Microsoft Graph API.

## 🎯 Objetivo

Proporcionar un entorno de aprendizaje local para:
- Simular operaciones de pago (en línea y automático)
- Gestionar estados de cobro (confirmado, pendiente, rechazado)
- Generar reportes CSV desde datos simulados
- Integrar con SharePoint Online usando Microsoft Graph
- Probar diferentes escenarios de negocio sin bases de datos reales

## 🏗️ Arquitectura

```
┌─────────────────┐
│  Frontend HTML  │
│   + CSS + JS    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Spring Boot    │
│   Controllers   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐
│    Services     │ ──▶ │  Datos JSON  │
│                 │     │    (mock)    │
└────────┬────────┘     └──────────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐
│  CSV Generator  │ ──▶ │  Archivo CSV │
└────────┬────────┘     └──────────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐
│ SharePoint Svc  │ ──▶ │  SharePoint  │
│ Microsoft Graph │     │    Online    │
└─────────────────┘     └──────────────┘
```

## 🚀 Inicio Rápido

### Prerrequisitos

- **Java 17** o superior
- **Maven 3.8+**
- **Navegador web** moderno

### Instalación y Ejecución

1. **Clonar o descargar el proyecto**

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

4. **Abrir en el navegador**
   ```
   http://localhost:8080
   ```

¡Listo! El sistema funcionará en **modo local** sin necesidad de configurar SharePoint.

> 📘 **¿Quieres integrar con SharePoint?** Consulta la [Guía de Configuración de SharePoint](CONFIGURACION-SHAREPOINT.md) para instrucciones paso a paso.

## 📋 Funcionalidades

### Panel de Control
- **Cargar Datos Mock**: Carga datos de ejemplo desde JSON
- **Generar CSV**: Crea un archivo CSV con los datos actuales
- **Enviar a SharePoint**: Sube el CSV a SharePoint (si está configurado) o guarda localmente
- **Estado SharePoint**: Verifica la configuración de integración

### Escenarios de Simulación
1. **Pago en Línea Confirmado**: Simula pago online exitoso e imputado
2. **Pago Automático Confirmado**: Simula pago recurrente procesado
3. **Pago Rechazado**: Simula rechazo por fondos insuficientes
4. **Lote de Confirmados**: Genera múltiples pagos confirmados
5. **Cuotas Pendientes**: Simula cuotas sin pagar

### Consultas
- Buscar pagos por RUT
- Ver cuotas pendientes
- Filtrar por estado de cobro

## 🔌 API REST Endpoints

### Pagos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/payments/mock` | Carga datos mock desde JSON |
| GET | `/api/payments` | Obtiene todos los pagos |
| GET | `/api/payments/by-rut/{rut}` | Busca pagos por RUT |
| GET | `/api/payments/pending/{rut}` | Obtiene cuotas pendientes |
| POST | `/api/payments/scenario/{name}` | Aplica un escenario |

#### Escenarios disponibles:
- `pago-online-confirmado`
- `pago-automatico-confirmado`
- `pago-rechazado`
- `lote-confirmados`
- `cuotas-pendientes`

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/reports/csv` | Genera CSV localmente |
| POST | `/api/reports/upload-sharepoint` | Genera y sube CSV a SharePoint |
| GET | `/api/reports/sharepoint-status` | Verifica estado de SharePoint |

## 📊 Modelo de Datos

```json
{
  "rut": "12345678-9",
  "email": "cliente@example.com",
  "poliza": "POL-2024-001",
  "prima": 50000,
  "cuotaNumero": 1,
  "monto": 50000,
  "canal": "PAGO_EN_LINEA",
  "pasarela": "FLOW",
  "estadoCobro": "CONFIRMADO",
  "imputado": true,
  "fechaPago": "2024-04-20T10:30:00",
  "fechaImputacion": "2024-04-20T11:00:00",
  "descripcion": "Pago confirmado"
}
```

### Campos

- **rut**: RUT del cliente (formato: 12345678-9)
- **email**: Email del cliente
- **poliza**: Número de póliza
- **prima**: Monto de la prima del seguro
- **cuotaNumero**: Número de cuota
- **monto**: Monto del pago
- **canal**: `PAGO_EN_LINEA` o `PAGO_AUTOMATICO`
- **pasarela**: Pasarela de pago (ej: FLOW)
- **estadoCobro**: `PENDIENTE`, `CONFIRMADO`, o `RECHAZADO`
- **imputado**: Indica si el pago fue registrado en caja
- **fechaPago**: Fecha/hora del pago
- **fechaImputacion**: Fecha/hora de imputación
- **descripcion**: Descripción del pago

## ☁️ Configuración de SharePoint

### Modo Local (Sin SharePoint)

Por defecto, el sistema funciona en **modo local**:
- Los archivos CSV se guardan en `./generated-reports/`
- No requiere configuración adicional
- Ideal para desarrollo y pruebas

### Modo SharePoint (Integración completa)

Para habilitar la integración con SharePoint Online:

> 📘 **Guía Completa Disponible:** Consulta [CONFIGURACION-SHAREPOINT.md](CONFIGURACION-SHAREPOINT.md) para una guía detallada paso a paso con capturas y solución de problemas.

#### 1. Registrar una Aplicación en Azure AD

1. Ir a [Azure Portal](https://portal.azure.com)
2. Navegar a **Azure Active Directory** > **App registrations**
3. Hacer clic en **New registration**
4. Configurar:
   - **Name**: SharePoint Payment Lab
   - **Supported account types**: Single tenant
   - **Redirect URI**: (dejar en blanco)
5. Hacer clic en **Register**

#### 2. Configurar Permisos

1. En la aplicación registrada, ir a **API permissions**
2. Agregar permisos:
   - **Microsoft Graph** > **Application permissions**
   - Seleccionar:
     - `Sites.ReadWrite.All`
     - `Files.ReadWrite.All`
3. Hacer clic en **Grant admin consent**

#### 3. Crear Client Secret

1. Ir a **Certificates & secrets**
2. Hacer clic en **New client secret**
3. Configurar:
   - **Description**: SharePoint Lab Secret
   - **Expires**: 24 months (o según necesidad)
4. **Copiar el valor** (solo se muestra una vez)

#### 4. Obtener IDs Necesarios

**Tenant ID**:
- Azure Portal > Azure Active Directory > Properties > Tenant ID

**Client ID**:
- Azure Portal > App registrations > Tu aplicación > Application (client) ID

**Site ID**:
```bash
# Usando Microsoft Graph Explorer (https://developer.microsoft.com/graph/graph-explorer)
GET https://graph.microsoft.com/v1.0/sites/{hostname}:/sites/{site-name}
```

**Drive ID**:
```bash
# Después de obtener el Site ID
GET https://graph.microsoft.com/v1.0/sites/{site-id}/drives
```

#### 5. Configurar Variables de Entorno

**Opción A: Archivo `.env` (Desarrollo)**

Copiar `.env.example` a `.env`:
```bash
cp .env.example .env
```

Editar `.env`:
```properties
SHAREPOINT_ENABLED=true
SHAREPOINT_TENANT_ID=tu-tenant-id-aqui
SHAREPOINT_CLIENT_ID=tu-client-id-aqui
SHAREPOINT_CLIENT_SECRET=tu-client-secret-aqui
SHAREPOINT_SITE_ID=tu-site-id-aqui
SHAREPOINT_DRIVE_ID=tu-drive-id-aqui
SHAREPOINT_FOLDER_PATH=Reportes
```

**Opción B: Variables de Sistema (Producción)**

Windows PowerShell:
```powershell
$env:SHAREPOINT_ENABLED="true"
$env:SHAREPOINT_TENANT_ID="tu-tenant-id"
$env:SHAREPOINT_CLIENT_ID="tu-client-id"
$env:SHAREPOINT_CLIENT_SECRET="tu-client-secret"
$env:SHAREPOINT_SITE_ID="tu-site-id"
$env:SHAREPOINT_DRIVE_ID="tu-drive-id"
$env:SHAREPOINT_FOLDER_PATH="Reportes"
```

Linux/Mac:
```bash
export SHAREPOINT_ENABLED=true
export SHAREPOINT_TENANT_ID=tu-tenant-id
export SHAREPOINT_CLIENT_ID=tu-client-id
export SHAREPOINT_CLIENT_SECRET=tu-client-secret
export SHAREPOINT_SITE_ID=tu-site-id
export SHAREPOINT_DRIVE_ID=tu-drive-id
export SHAREPOINT_FOLDER_PATH=Reportes
```

**Opción C: application.yml (No recomendado para producción)**

Editar `src/main/resources/application.yml`:
```yaml
app:
  sharepoint:
    enabled: true
    tenant-id: tu-tenant-id
    client-id: tu-client-id
    client-secret: tu-client-secret
    site-id: tu-site-id
    drive-id: tu-drive-id
    folder-path: Reportes
```

#### 6. Verificar Configuración

Ejecutar la aplicación y hacer clic en **"Estado SharePoint"** en el panel web para verificar que todas las credenciales están configuradas correctamente.

## 📁 Estructura del Proyecto

```
sharepoint-payment-lab/
├── pom.xml                          # Dependencias Maven
├── README.md                        # Este archivo
├── .env.example                     # Plantilla de variables de entorno
├── .gitignore                       # Archivos ignorados por Git
├── generated-reports/               # Carpeta para CSV generados
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/sharepointlab/
    │   │       ├── SharepointLabApplication.java      # Clase principal
    │   │       ├── controller/
    │   │       │   ├── PaymentController.java         # API de pagos
    │   │       │   └── ReportController.java          # API de reportes
    │   │       ├── service/
    │   │       │   ├── PaymentService.java            # Lógica de pagos
    │   │       │   ├── CsvGeneratorService.java       # Generación CSV
    │   │       │   └── SharePointService.java         # Integración SharePoint
    │   │       ├── model/
    │   │       │   └── Payment.java                   # Modelo de datos
    │   │       └── config/
    │   │           ├── SharePointConfig.java          # Config SharePoint
    │   │           └── WebConfig.java                 # Config CORS
    │   └── resources/
    │       ├── application.yml                        # Configuración Spring
    │       ├── data/
    │       │   └── mock-payments.json                 # Datos de prueba
    │       └── static/
    │           ├── index.html                         # Frontend
    │           ├── styles.css                         # Estilos
    │           └── app.js                             # Lógica frontend
    └── test/
        └── java/                                      # Tests (vacío por ahora)
```

## 🧪 Casos de Uso

### 1. Consultar cuotas pendientes de un cliente

1. En el navegador, ingresar un RUT en el campo de búsqueda (ej: `15555666-7`)
2. Hacer clic en **Buscar**
3. Aplicar el escenario **"Cuotas Pendientes"**
4. Buscar nuevamente por el RUT

### 2. Generar reporte de pagos confirmados

1. Aplicar escenario **"Lote de Confirmados"**
2. Hacer clic en **Generar CSV**
3. Verificar en consola el archivo generado en `./generated-reports/`

### 3. Enviar reporte a SharePoint

1. Configurar credenciales de SharePoint (ver sección anterior)
2. Verificar estado con **"Estado SharePoint"**
3. Aplicar cualquier escenario
4. Hacer clic en **Enviar a SharePoint**
5. Si está configurado, se subirá; si no, se guardará localmente

### 4. Simular pago rechazado

1. Aplicar escenario **"Pago Rechazado"**
2. Observar en la tabla el estado `RECHAZADO` e `Imputado: NO`

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.2.5**: Framework principal
- **Java 17**: Lenguaje de programación
- **Maven**: Gestor de dependencias
- **Lombok**: Reducción de boilerplate
- **Apache Commons CSV**: Generación de archivos CSV
- **Microsoft Graph SDK 5.80.0**: Integración con SharePoint
- **Azure Identity**: Autenticación con Azure AD

### Frontend
- **HTML5**: Estructura
- **CSS3**: Estilos (sin frameworks)
- **JavaScript Vanilla**: Lógica (sin frameworks)
- **Fetch API**: Llamadas REST

### Integración
- **Microsoft Graph API**: Comunicación con SharePoint
- **OAuth 2.0 Client Credentials Flow**: Autenticación

## 📝 Notas Importantes

### Seguridad
- **NO commitear** el archivo `.env` con credenciales reales
- Usar variables de entorno para producción
- El `client-secret` es sensible, tratarlo como contraseña

### Limitaciones del Demo
- Archivos > 4MB requieren upload session (no implementado en este demo)
- Solo soporta autenticación con Client Secret (no certificados)
- Los datos mock se pierden al reiniciar la aplicación

### Permisos de SharePoint
- La aplicación necesita permisos de **Application** (no Delegated)
- Requiere consentimiento del administrador del tenant
- La carpeta destino debe existir en SharePoint

## 🐛 Troubleshooting

### Error: "SharePoint no configurado"
- Verificar que `SHAREPOINT_ENABLED=true`
- Revisar que todas las variables de entorno estén configuradas
- Usar el botón **"Estado SharePoint"** para diagnóstico

### Error: "Unauthorized" al subir archivo
- Verificar que los permisos fueron otorgados en Azure AD
- Confirmar que el Client Secret es válido y no expiró
- Revisar que el Tenant ID, Client ID y Site ID son correctos

### Error: "Drive not found"
- Verificar el `driveId` con Graph Explorer
- Asegurar que la aplicación tiene acceso al sitio

### Los archivos no aparecen en SharePoint
- Verificar la carpeta destino (`folderPath`) existe
- Revisar permisos de la biblioteca de documentos
- Consultar logs de la aplicación para detalles

## 📚 Referencias

- [Microsoft Graph Documentation](https://docs.microsoft.com/graph/)
- [SharePoint REST API](https://docs.microsoft.com/sharepoint/dev/sp-add-ins/get-to-know-the-sharepoint-rest-service)
- [Azure AD App Registration](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## 🤝 Contribuciones

Este es un proyecto de aprendizaje. Sugerencias y mejoras son bienvenidas.

## 📄 Licencia

Proyecto de ejemplo para fines educativos. Usar libremente.

---

**¡Feliz experimentación con SharePoint! 🚀**
