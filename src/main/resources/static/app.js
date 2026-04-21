// API Base URL
const API_BASE = '/api';

// Estado de la aplicación
let currentPayments = [];

/**
 * Muestra un mensaje en el área de mensajes
 */
function showMessage(message, type = 'info') {
    const messageArea = document.getElementById('messageArea');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.innerHTML = message;
    
    messageArea.innerHTML = '';
    messageArea.appendChild(messageDiv);
    
    // Auto-ocultar después de 8 segundos
    setTimeout(() => {
        messageDiv.style.opacity = '0';
        setTimeout(() => messageDiv.remove(), 300);
    }, 8000);
}

/**
 * Formatea una fecha ISO a formato legible
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('es-CL');
}

/**
 * Formatea un número como moneda chilena
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP'
    }).format(amount);
}

/**
 * Crea un badge para el estado
 */
function createBadge(text, type) {
    return `<span class="badge badge-${type}">${text}</span>`;
}

/**
 * Renderiza la tabla de pagos
 */
function renderPaymentsTable(payments) {
    currentPayments = payments;
    const tbody = document.getElementById('paymentsTableBody');
    const recordCount = document.getElementById('recordCount');
    
    if (!payments || payments.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align: center; padding: 40px; color: #6b7280;">
                    No hay datos para mostrar
                </td>
            </tr>
        `;
        recordCount.textContent = '';
        return;
    }
    
    recordCount.textContent = `(${payments.length} registros)`;
    
    tbody.innerHTML = payments.map(payment => {
        // Determinar clases de estado
        let estadoClass = '';
        let estadoText = payment.estadoCobro;
        
        if (payment.estadoCobro === 'CONFIRMADO') {
            estadoClass = 'confirmed';
        } else if (payment.estadoCobro === 'PENDIENTE') {
            estadoClass = 'pending';
        } else if (payment.estadoCobro === 'RECHAZADO') {
            estadoClass = 'rejected';
        }
        
        return `
            <tr>
                <td><strong>${payment.rut}</strong></td>
                <td>${payment.email}</td>
                <td>${payment.poliza}</td>
                <td>${payment.cuotaNumero}</td>
                <td><strong>${formatCurrency(payment.monto)}</strong></td>
                <td>${payment.canal.replace('_', ' ')}</td>
                <td>${createBadge(estadoText, estadoClass)}</td>
                <td>${createBadge(payment.imputado ? 'SÍ' : 'NO', payment.imputado ? 'yes' : 'no')}</td>
                <td>${formatDate(payment.fechaPago)}</td>
                <td>${payment.descripcion}</td>
            </tr>
        `;
    }).join('');
}

/**
 * Carga los datos mock desde el backend
 */
async function loadMockData() {
    try {
        showMessage('⏳ Cargando datos mock...', 'info');
        
        const response = await fetch(`${API_BASE}/payments/mock`);
        const data = await response.json();
        
        if (data.success) {
            renderPaymentsTable(data.data);
            showMessage(`✅ ${data.message} - ${data.count} registros cargados`, 'success');
        } else {
            showMessage(`❌ Error: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al cargar datos: ${error.message}`, 'error');
    }
}

/**
 * Busca pagos por RUT
 */
async function searchByRut() {
    const rutInput = document.getElementById('rutInput');
    const rut = rutInput.value.trim();
    
    if (!rut) {
        showMessage('⚠️ Por favor ingrese un RUT', 'warning');
        return;
    }
    
    try {
        showMessage(`🔍 Buscando pagos para RUT: ${rut}...`, 'info');
        
        const response = await fetch(`${API_BASE}/payments/by-rut/${encodeURIComponent(rut)}`);
        const data = await response.json();
        
        if (data.success) {
            renderPaymentsTable(data.data);
            
            if (data.count === 0) {
                showMessage(`ℹ️ No se encontraron pagos para el RUT: ${rut}`, 'info');
            } else {
                showMessage(`✅ Se encontraron ${data.count} pago(s) para el RUT: ${rut}`, 'success');
            }
        } else {
            showMessage(`❌ Error: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al buscar pagos: ${error.message}`, 'error');
    }
}

/**
 * Aplica un escenario de simulación
 */
async function applyScenario(scenarioName) {
    try {
        showMessage(`⏳ Aplicando escenario: ${scenarioName}...`, 'info');
        
        const response = await fetch(`${API_BASE}/payments/scenario/${scenarioName}`, {
            method: 'POST'
        });
        const data = await response.json();
        
        if (data.success) {
            renderPaymentsTable(data.data);
            showMessage(`✅ ${data.message} - ${data.count} registros generados`, 'success');
        } else {
            showMessage(`❌ Error: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al aplicar escenario: ${error.message}`, 'error');
    }
}

/**
 * Genera un archivo CSV
 */
async function generateCsv() {
    if (currentPayments.length === 0) {
        showMessage('⚠️ No hay datos para generar el CSV. Primero carga datos o aplica un escenario.', 'warning');
        return;
    }
    
    try {
        showMessage('⏳ Generando archivo CSV...', 'info');
        
        const response = await fetch(`${API_BASE}/reports/csv`, {
            method: 'POST'
        });
        const data = await response.json();
        
        if (data.success) {
            showMessage(
                `✅ ${data.message}<br>` +
                `📄 Archivo: <strong>${data.fileName}</strong><br>` +
                `📊 Registros: ${data.recordCount}<br>` +
                `📁 Ruta: ${data.filePath}`,
                'success'
            );
        } else {
            showMessage(`❌ Error: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al generar CSV: ${error.message}`, 'error');
    }
}

/**
 * Genera CSV y lo sube a SharePoint
 */
async function uploadToSharePoint() {
    if (currentPayments.length === 0) {
        showMessage('⚠️ No hay datos para generar el reporte. Primero carga datos o aplica un escenario.', 'warning');
        return;
    }
    
    try {
        showMessage('⏳ Generando reporte y enviando a SharePoint...', 'info');
        
        const response = await fetch(`${API_BASE}/reports/upload-sharepoint`, {
            method: 'POST'
        });
        const data = await response.json();
        
        if (data.success) {
            let messageHtml = `✅ ${data.message}<br>` +
                `📄 Archivo: <strong>${data.fileName}</strong><br>` +
                `📊 Registros: ${data.recordCount}<br>`;
            
            // Agregar información de SharePoint si está disponible
            const sp = data.sharepoint;
            if (sp.status === 'success') {
                messageHtml += `<br><strong>SharePoint:</strong><br>` +
                    `✓ Estado: Subido exitosamente<br>` +
                    `🔗 URL: <a href="${sp.webUrl}" target="_blank">${sp.webUrl}</a><br>` +
                    `📦 Item ID: ${sp.itemId}`;
                showMessage(messageHtml, 'success');
            } else if (sp.status === 'local') {
                messageHtml += `<br><strong>SharePoint:</strong><br>` +
                    `ℹ️ ${sp.message}<br>` +
                    `📁 Archivo guardado en: ${sp.localPath}`;
                showMessage(messageHtml, 'warning');
            } else {
                messageHtml += `<br><strong>SharePoint:</strong><br>` +
                    `⚠️ ${sp.message}<br>` +
                    `📁 Archivo guardado localmente en: ${sp.localPath}`;
                showMessage(messageHtml, 'warning');
            }
        } else {
            showMessage(`❌ Error: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al procesar reporte: ${error.message}`, 'error');
    }
}

/**
 * Verifica el estado de la configuración de SharePoint
 */
async function checkSharePointStatus() {
    try {
        showMessage('⏳ Verificando configuración de SharePoint...', 'info');
        
        const response = await fetch(`${API_BASE}/reports/sharepoint-status`);
        const data = await response.json();
        
        if (data.success) {
            const status = data.status;
            
            let messageHtml = '<strong>Estado de SharePoint:</strong><br>';
            messageHtml += `• Habilitado: ${status.enabled ? '✅' : '❌'}<br>`;
            messageHtml += `• Configurado: ${status.configured ? '✅' : '❌'}<br>`;
            messageHtml += `<br><strong>Credenciales:</strong><br>`;
            messageHtml += `• Tenant ID: ${status.tenantId}<br>`;
            messageHtml += `• Client ID: ${status.clientId}<br>`;
            messageHtml += `• Client Secret: ${status.clientSecret}<br>`;
            messageHtml += `• Site ID: ${status.siteId}<br>`;
            messageHtml += `• Drive ID: ${status.driveId}<br>`;
            messageHtml += `• Carpeta: ${status.folderPath}<br>`;
            
            if (status.configured) {
                messageHtml += '<br>✅ SharePoint está listo para usar';
                showMessage(messageHtml, 'success');
            } else {
                messageHtml += '<br>⚠️ SharePoint no está completamente configurado. ' +
                    'Los archivos se guardarán localmente.<br>' +
                    'Revisa el README.md para configurar las credenciales.';
                showMessage(messageHtml, 'warning');
            }
        } else {
            showMessage(`❌ Error al verificar estado: ${data.message}`, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(`❌ Error al verificar SharePoint: ${error.message}`, 'error');
    }
}

/**
 * Abre el modal de ayuda
 */
function openHelpModal() {
    const modal = document.getElementById('helpModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

/**
 * Cierra el modal de ayuda
 */
function closeHelpModal() {
    const modal = document.getElementById('helpModal');
    modal.classList.remove('show');
    document.body.style.overflow = 'auto';
}

// Cerrar modal al hacer clic fuera de él
window.addEventListener('click', (event) => {
    const modal = document.getElementById('helpModal');
    if (event.target === modal) {
        closeHelpModal();
    }
});

// Cerrar modal con tecla Escape
window.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
        closeHelpModal();
    }
});

// Cargar datos al iniciar (opcional)
window.addEventListener('DOMContentLoaded', () => {
    console.log('SharePoint Payment Lab - Cargado');
    showMessage('👋 Bienvenido al SharePoint Payment Lab. Haz clic en ❓ para ver las instrucciones.', 'info');
});
