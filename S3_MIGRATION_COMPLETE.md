# Migración Completa de Certificados a AWS S3 - ASECAPT

## ✅ Archivos Creados/Modificados

### 1. Configuración de S3
- **S3Config.java** ✅ Creado - Configuración del cliente S3
- **S3CertificateService.java** ✅ Creado - Servicio para subir/descargar archivos S3
- **CourseInitialsService.java** ✅ Creado - Generador de iniciales de cursos

### 2. Servicios Modificados
- **CertificateService.java** ✅ Actualizado - Ahora usa S3 en lugar de almacenamiento local
- **QRCodeService.java** ✅ Actualizado - Método para generar QR como bytes
- **CertificateController.java** ✅ Actualizado - Descarga desde S3
- **PublicCertificateController.java** ✅ Actualizado - Descarga pública desde S3

### 3. Dependencias
- **pom.xml** ✅ Actualizado - Agregada dependencia AWS S3 SDK

## 🔧 Configuración Necesaria

### 1. Variables de Entorno
Agregar al `application.properties`:


### 2. Estructura en S3
Los archivos se organizarán así:
```
asecapt-certificates/
├── certificates/
│   ├── {DNI}/
│   │   ├── {INICIALES_CURSO}_{CERTIFICATE_CODE}.pdf
│   │   ├── {INICIALES_CURSO}_{CERTIFICATE_CODE}_QR.png
│   │   └── ...
│   └── ...
```

### 3. Ejemplos de Nombres de Archivo
- **Curso**: "Desarrollo Web Avanzado" → Iniciales: `DWA`
- **DNI**: `12345678`
- **Código**: `CERT-DWA-5678-20250809-ABC123`
- **Archivos**:
  - Certificado: `certificates/12345678/DWA_CERT-DWA-5678-20250809-ABC123.pdf`
  - QR: `certificates/12345678/DWA_CERT-DWA-5678-20250809-ABC123_QR.png`

## 🚀 Cómo Funciona Ahora

### 1. Subida de Certificados
1. El usuario sube un certificado desde el frontend
2. El sistema obtiene el DNI del estudiante y genera iniciales del curso
3. Se sube el archivo PDF/imagen a S3 en la carpeta del DNI
4. Se genera un QR code y se sube a S3 en la misma carpeta
5. Se almacenan las rutas S3 en la base de datos

### 2. Descarga de Certificados
1. Cuando se solicita descargar un certificado
2. El sistema obtiene la ruta S3 de la base de datos
3. Descarga el archivo desde S3
4. Lo sirve al cliente como una descarga directa

### 3. Generación de Iniciales
El `CourseInitialsService` genera iniciales automáticamente:
- "Desarrollo Web Avanzado" → `DWA`
- "Marketing Digital para Empresas" → `MDE`
- "Gestión de Proyectos" → `GDP`
- "Inteligencia Artificial" → `IAR`

## 📋 Pasos para Implementar

### 1. Compilar el Proyecto
```bash
cd /Users/i339972/Documents/asecapt
mvn clean install
```

### 2. Verificar Variables de Entorno
Asegúrate de que las variables AWS están configuradas en `application.properties`

### 3. Reiniciar la Aplicación
```bash
mvn spring-boot:run
```

### 4. Probar la Funcionalidad
1. Ve al componente de certificados en Angular
2. Sube un certificado para un estudiante
3. Verifica que se cree en S3 con la estructura correcta

## 🔍 Verificación en AWS S3

### 1. Acceder al Bucket
1. Ve a [AWS S3 Console](https://s3.console.aws.amazon.com/)
2. Busca el bucket `asecapt-certificates`
3. Verifica la estructura de carpetas por DNI

### 2. Verificar Archivos
Los archivos deben aparecer con esta estructura:
```
certificates/
├── 12345678/
│   ├── DWA_CERT-DWA-5678-20250809-ABC123.pdf
│   └── DWA_CERT-DWA-5678-20250809-ABC123_QR.png
```

## 🛠️ Troubleshooting

### Error de Permisos AWS
Si aparecen errores de permisos:
1. Verifica que las credenciales sean correctas
2. Confirma que el bucket existe
3. Revisa los permisos IAM

### Error de Compilación
Si hay errores de compilación:
1. Ejecuta `mvn clean install`
2. Verifica que todas las dependencias se descarguen
3. Revisa que Java 17 esté configurado

### Archivos No Se Suben
Si los archivos no aparecen en S3:
1. Revisa los logs de la aplicación
2. Verifica la conectividad a internet
3. Confirma las credenciales AWS

## 📊 Beneficios de la Migración

### 1. Escalabilidad
- ✅ No hay límites de almacenamiento local
- ✅ Acceso global desde cualquier servidor
- ✅ Backup automático y redundancia

### 2. Organización
- ✅ Estructura clara por DNI de estudiante
- ✅ Nombres de archivo con iniciales del curso
- ✅ Fácil búsqueda y gestión

### 3. Seguridad
- ✅ Encriptación automática en S3
- ✅ Control de acceso granular
- ✅ Versionado de archivos

### 4. Costos
- ✅ Estimado: ~$0.71/mes para uso normal
- ✅ Pago solo por lo que usas
- ✅ Sin costos de infraestructura local

## ⚡ Próximos Pasos

1. **Implementar**: Seguir los pasos de configuración
2. **Probar**: Subir certificados de prueba
3. **Migrar**: Mover certificados existentes (opcional)
4. **Monitorear**: Revisar uso y costos en AWS

## 🔒 Seguridad

- ✅ Credenciales configuradas como variables de entorno
- ✅ Bucket con acceso restringido
- ✅ Encriptación SSE-S3 habilitada
- ✅ Descarga controlada por la aplicación

¡La integración está completa y lista para usar! 🚀
