# INICIO RÁPIDO - SharePoint Payment Lab

## ⚡ Ejecutar en 3 pasos

### 1. Compilar
```bash
mvn clean install
```

### 2. Ejecutar
```bash
mvn spring-boot:run
```

### 3. Abrir navegador
```
http://localhost:8080
```

## 🎮 Primeros Pasos en la Interfaz

1. Hacer clic en **"Cargar Datos Mock"**
2. Explorar los pagos en la tabla
3. Probar escenarios con los botones de simulación
4. Generar un CSV con **"Generar CSV"**
5. Ver el archivo en la carpeta `generated-reports/`

## 📋 Comandos Útiles

### Compilar sin tests
```bash
mvn clean install -DskipTests
```

### Ejecutar en modo debug
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug"
```

### Limpiar reportes generados
```bash
# Windows PowerShell
Remove-Item -Path generated-reports\*.csv

# Linux/Mac
rm -rf generated-reports/*.csv
```

## 🔧 Configuración Opcional de SharePoint

Si quieres probar la integración real con SharePoint:

1. Copiar `.env.example` a `.env`
2. Completar las credenciales (ver README.md para detalles)
3. Reiniciar la aplicación
4. Verificar con botón **"Estado SharePoint"**

## 📝 Datos de Prueba

RUTs disponibles en datos mock:
- `12345678-9` - Pago confirmado
- `98765432-1` - Pago confirmado
- `11223344-5` - Pago rechazado
- `15555666-7` - Cuotas pendientes (2)
- `22334455-6` - Pago confirmado
- `33445566-7` - Pago automático

## 🆘 Problemas Comunes

**Puerto 8080 ocupado:**
```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

**Error de compilación:**
- Verificar Java 17 instalado: `java -version`
- Verificar Maven instalado: `mvn -version`

**No se ven los datos:**
- Refrescar el navegador (F5)
- Hacer clic en "Cargar Datos Mock"

## 📚 Más Información

Ver [README.md](README.md) para documentación completa.
