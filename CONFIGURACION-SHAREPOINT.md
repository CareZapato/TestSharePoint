# 📘 Guía Completa de Configuración de SharePoint

Esta guía te llevará paso a paso para configurar la integración con SharePoint Online usando Microsoft Graph API.

## 📋 Requisitos Previos

- ✅ Cuenta de Microsoft 365 con permisos de administrador
- ✅ Acceso al portal de Azure (https://portal.azure.com)
- ✅ Un sitio de SharePoint Online creado
- ✅ Biblioteca de documentos donde subir los archivos

---

## 🚀 Paso 1: Registrar Aplicación en Azure AD

### 1.1 Crear el Registro de App

1. Ve a [Azure Portal](https://portal.azure.com)
2. Busca **"Azure Active Directory"** o **"Entra ID"**
3. En el menú lateral, selecciona **"App registrations"**
4. Haz clic en **"+ New registration"**

### 1.2 Configurar el Registro

```
Name: SharePoint Payment Lab
Supported account types: ☑ Accounts in this organizational directory only (Single tenant)
Redirect URI: (dejar vacío por ahora)
```

5. Haz clic en **"Register"**

### 1.3 Anotar el Client ID y Tenant ID

Una vez creada la aplicación, verás:
- **Application (client) ID** → Este es tu `SHAREPOINT_CLIENT_ID`
- **Directory (tenant) ID** → Este es tu `SHAREPOINT_TENANT_ID`

**📝 Anota estos valores en un lugar seguro**

---

## 🔐 Paso 2: Crear Client Secret

1. En tu aplicación registrada, ve al menú lateral → **"Certificates & secrets"**
2. Selecciona la pestaña **"Client secrets"**
3. Haz clic en **"+ New client secret"**
4. Configura:
   ```
   Description: SharePoint Lab Secret
   Expires: 24 months (o según tu política de seguridad)
   ```
5. Haz clic en **"Add"**

⚠️ **IMPORTANTE:** 
- Copia el **Value** (no el Secret ID) INMEDIATAMENTE
- Este valor solo se muestra UNA VEZ
- Este es tu `SHAREPOINT_CLIENT_SECRET`

**📝 Anota este valor en un lugar seguro**

---

## 🔑 Paso 3: Configurar Permisos de API

### 3.1 Agregar Permisos

1. En tu aplicación, ve a **"API permissions"**
2. Haz clic en **"+ Add a permission"**
3. Selecciona **"Microsoft Graph"**
4. Selecciona **"Application permissions"** (NO Delegated permissions)

### 3.2 Permisos Necesarios

Busca y selecciona los siguientes permisos:

- ✅ **Sites.ReadWrite.All** - Para acceder a sitios de SharePoint
- ✅ **Files.ReadWrite.All** - Para leer/escribir archivos

### 3.3 Otorgar Consentimiento de Administrador

⚠️ **CRÍTICO:** Sin este paso, la aplicación NO funcionará

1. Después de agregar los permisos, verás una advertencia amarilla
2. Haz clic en **"Grant admin consent for [Tu organización]"**
3. Confirma haciendo clic en **"Yes"**
4. Verifica que los permisos muestren una marca verde ✅ en la columna "Status"

---

## 🌐 Paso 4: Obtener el Site ID

Hay varias formas de obtener el Site ID. Aquí te muestro las más comunes:

### Opción A: Usando Microsoft Graph Explorer (Recomendado)

1. Ve a [Microsoft Graph Explorer](https://developer.microsoft.com/graph/graph-explorer)
2. Inicia sesión con tu cuenta de Microsoft 365
3. Ejecuta esta consulta (reemplaza los valores):

```http
GET https://graph.microsoft.com/v1.0/sites/{hostname}:/sites/{site-name}
```

**Ejemplo:**
```http
GET https://graph.microsoft.com/v1.0/sites/miempresa.sharepoint.com:/sites/pagos
```

4. En la respuesta JSON, busca el campo **"id"** → Este es tu `SHAREPOINT_SITE_ID`

### Opción B: Usando PowerShell

```powershell
# Instalar módulo si no lo tienes
Install-Module -Name Microsoft.Graph -Scope CurrentUser

# Conectar
Connect-MgGraph -Scopes "Sites.Read.All"

# Obtener site
Get-MgSite -Search "Nombre de tu sitio"
```

### Opción C: Desde la URL del Sitio

Si tu sitio es: `https://miempresa.sharepoint.com/sites/pagos`

Puedes construir la URL para Graph:
```
https://graph.microsoft.com/v1.0/sites/miempresa.sharepoint.com:/sites/pagos
```

**📝 Anota el Site ID**

---

## 📂 Paso 5: Obtener el Drive ID

El Drive ID corresponde a una biblioteca de documentos en SharePoint.

### 5.1 Listar Drives del Sitio

Usando Microsoft Graph Explorer:

```http
GET https://graph.microsoft.com/v1.0/sites/{site-id}/drives
```

Reemplaza `{site-id}` con el Site ID que obtuviste en el paso anterior.

### 5.2 Identificar el Drive Correcto

En la respuesta JSON verás una lista de drives. Busca el que corresponda a tu biblioteca de documentos:

```json
{
  "value": [
    {
      "id": "b!abc123...",  ← Este es tu SHAREPOINT_DRIVE_ID
      "name": "Documents",
      "webUrl": "https://miempresa.sharepoint.com/sites/pagos/Shared Documents",
      "driveType": "documentLibrary"
    }
  ]
}
```

**📝 Anota el Drive ID** (campo "id")

---

## ⚙️ Paso 6: Configurar Variables de Entorno

Ahora que tienes todos los valores, configúralos en tu aplicación.

### Opción A: Archivo .env (Para Desarrollo Local)

1. En la raíz del proyecto, crea un archivo llamado `.env`
2. Copia y pega el siguiente contenido:

```properties
# Habilitar SharePoint
SHAREPOINT_ENABLED=true

# Credenciales de Azure AD
SHAREPOINT_TENANT_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
SHAREPOINT_CLIENT_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
SHAREPOINT_CLIENT_SECRET=tu-client-secret-aquí

# IDs de SharePoint
SHAREPOINT_SITE_ID=tu-site-id-aquí
SHAREPOINT_DRIVE_ID=tu-drive-id-aquí

# Carpeta destino (debe existir en SharePoint)
SHAREPOINT_FOLDER_PATH=Reportes
```

3. Reemplaza cada valor con los que anotaste
4. **NUNCA** subas este archivo a Git (ya está en .gitignore)

### Opción B: Variables de Sistema Windows

Abre PowerShell como Administrador:

```powershell
# Configurar variables de entorno del sistema
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_ENABLED', 'true', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_TENANT_ID', 'tu-tenant-id', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_CLIENT_ID', 'tu-client-id', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_CLIENT_SECRET', 'tu-secret', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_SITE_ID', 'tu-site-id', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_DRIVE_ID', 'tu-drive-id', 'User')
[System.Environment]::SetEnvironmentVariable('SHAREPOINT_FOLDER_PATH', 'Reportes', 'User')
```

**Nota:** Después de configurar variables de sistema, reinicia tu IDE/terminal.

---

## 📁 Paso 7: Crear Carpeta en SharePoint

1. Ve a tu sitio de SharePoint
2. Abre la biblioteca de documentos (Drive) que configuraste
3. Crea una carpeta llamada **"Reportes"** (o el nombre que pusiste en `SHAREPOINT_FOLDER_PATH`)

---

## ✅ Paso 8: Verificar la Configuración

### 8.1 Reiniciar la Aplicación

```bash
mvn spring-boot:run
```

### 8.2 Abrir el Navegador

Ve a: http://localhost:8080

### 8.3 Verificar Estado

1. Haz clic en el botón **"⚙️ Estado SharePoint"**
2. Deberías ver algo como:

```json
{
  "enabled": "true",
  "configured": true,
  "tenantId": "✓",
  "clientId": "✓",
  "clientSecret": "✓",
  "siteId": "✓",
  "driveId": "✓",
  "folderPath": "Reportes"
}
```

### 8.4 Probar la Subida

1. Haz clic en **"🔄 Cargar Datos Mock"**
2. Haz clic en **"☁️ Enviar a SharePoint"**
3. Si todo está bien, verás:
   - ✅ Mensaje de éxito
   - 🔗 Link al archivo en SharePoint
   - 📦 Item ID del archivo

4. Ve a tu biblioteca de documentos en SharePoint y verifica que el archivo CSV esté ahí

---

## 🐛 Solución de Problemas

### Error: "Unauthorized" o "401"

**Causa:** Permisos no otorgados o credenciales incorrectas

**Solución:**
1. Verifica que otorgaste consentimiento de administrador en Azure AD
2. Confirma que copiaste correctamente el Client Secret (es case-sensitive)
3. Verifica que el Tenant ID y Client ID son correctos

### Error: "Drive not found" o "404"

**Causa:** El Drive ID es incorrecto o la app no tiene acceso

**Solución:**
1. Vuelve a obtener el Drive ID usando Graph Explorer
2. Verifica que el Site ID es correcto
3. Confirma que los permisos están otorgados

### Error: "Path not found"

**Causa:** La carpeta especificada en `SHAREPOINT_FOLDER_PATH` no existe

**Solución:**
1. Ve a SharePoint y crea la carpeta
2. O cambia `SHAREPOINT_FOLDER_PATH` a una carpeta existente

### El archivo se guarda localmente en vez de SharePoint

**Causa:** `SHAREPOINT_ENABLED=false` o faltan credenciales

**Solución:**
1. Verifica que `SHAREPOINT_ENABLED=true`
2. Usa el botón "Estado SharePoint" para ver qué falta
3. Reinicia la aplicación después de configurar variables

### Error: "Client secret is expired"

**Causa:** El Client Secret caducó

**Solución:**
1. Ve a Azure Portal → App registrations → Tu app → Certificates & secrets
2. Crea un nuevo Client Secret
3. Actualiza la variable `SHAREPOINT_CLIENT_SECRET`
4. Reinicia la aplicación

---

## 🔒 Mejores Prácticas de Seguridad

### ✅ Hacer:
- Usar variables de entorno para credenciales
- Rotar el Client Secret regularmente
- Limitar permisos solo a lo necesario
- Usar diferentes credenciales para desarrollo y producción
- Verificar que .gitignore incluye `.env`

### ❌ No Hacer:
- Nunca commitear credenciales al repositorio
- No compartir el Client Secret por email o chat
- No usar la misma cuenta de servicio para todo
- No dar más permisos de los necesarios
- No dejar Client Secrets sin fecha de expiración

---

## 📚 Referencias Útiles

- [Microsoft Graph Documentation](https://docs.microsoft.com/graph/)
- [SharePoint Sites API](https://docs.microsoft.com/graph/api/resources/sharepoint)
- [Azure AD App Registration](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Graph Explorer](https://developer.microsoft.com/graph/graph-explorer)
- [Graph Permissions Reference](https://docs.microsoft.com/graph/permissions-reference)

---

## 💡 Consejos Adicionales

### Para Desarrollo
- Usa el Graph Explorer para probar tus consultas antes de implementarlas
- Guarda una copia de backup de tus credenciales en un gestor de contraseñas
- Documenta los IDs en un archivo aparte (no en el código)

### Para Producción
- Usa Azure Key Vault para almacenar secrets
- Implementa logging para auditar subidas de archivos
- Considera usar certificados en lugar de secrets para mayor seguridad
- Configura alertas en Azure para accesos sospechosos

---

## 🆘 ¿Necesitas Ayuda?

Si después de seguir esta guía sigues teniendo problemas:

1. Revisa los logs de la aplicación en la consola
2. Usa el botón "Estado SharePoint" para diagnóstico
3. Verifica la documentación oficial de Microsoft Graph
4. Consulta el [README.md](README.md) del proyecto

---

**¡Listo! Ahora tienes SharePoint completamente configurado y funcionando. 🎉**
