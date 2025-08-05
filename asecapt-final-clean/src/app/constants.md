# Configuración Centralizada de ASECAPT

Este archivo contiene toda la configuración centralizada de la aplicación ASECAPT.

## Estructura

### APP_CONFIG
Configuración principal de la aplicación:
- `api.baseUrl`: URL base del API backend (puerto 8081)
- `frontend.baseUrl`: URL base del frontend
- `endpoints`: Endpoints relativos para cada módulo

### QR_CONFIG
Configuración para códigos QR:
- `verificationUrlBase`: URL base para verificación
- `size`: Tamaño del código QR
- `errorCorrectionLevel`: Nivel de corrección de errores

### UPLOAD_CONFIG
Configuración de subida de archivos:
- `maxFileSize`: Tamaño máximo (10MB)
- `allowedTypes`: Tipos de archivo permitidos
- `allowedExtensions`: Extensiones permitidas

### CERTIFICATE_CONFIG
Configuración de certificados:
- `numberPrefix`: Prefijo para números de certificado
- `tokenPrefix`: Prefijo para tokens
- `formats`: Formatos de fecha y numeración

### UI_CONFIG
Configuración de interfaz de usuario:
- `debounceTime`: Tiempo de debounce para búsquedas
- `pagination`: Configuración de paginación
- `theme`: Colores del tema

### VALIDATION_RULES
Reglas de validación para formularios

### ERROR_MESSAGES / SUCCESS_MESSAGES
Mensajes de error y éxito centralizados

## Funciones Helper

- `buildApiUrl(endpoint)`: Construye URLs completas del API
- `buildVerificationUrl(token)`: Construye URLs de verificación
- `buildFrontendUrl(path)`: Construye URLs del frontend

## Uso en Servicios

```typescript
import { APP_CONFIG, buildApiUrl } from '../constants';

private apiUrl = buildApiUrl(APP_CONFIG.endpoints.certificates);
```

## Migración desde Environment

Los archivos `environment.ts` ahora son legacy y redirigen a las constantes.
Toda nueva configuración debe añadirse a `constants.ts`.

## Configuración de Puertos

- **Backend**: Puerto 8081 (actualizado desde 8080)
- **Frontend**: Puerto 4200 