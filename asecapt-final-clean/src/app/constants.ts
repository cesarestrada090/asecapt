/**
 * Application Constants and Configuration
 * Centralized configuration for ASECAPT application
 */

// Environment Detection
const isProduction = window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1';

// Base Configuration
export const APP_CONFIG = {
  // API Configuration
  api: {
    baseUrl: isProduction ? 'https://asecapt.com/api' : 'http://localhost:8081/api',
    baseUrlV1: isProduction ? 'https://asecapt.com/v1' : 'http://localhost:8081/v1',
    timeout: 30000,
    retryAttempts: 3
  },

  // Frontend Configuration
  frontend: {
    baseUrl: isProduction ? 'https://asecapt.com' : 'http://localhost:4200',
    pageSize: 10,
    maxPageSize: 100
  },

  // API Endpoints (usando /api)
  endpoints: {
    enrollments: '/enrollments',
    certificates: '/certificates',
    verification: '/verify',
    programs: '/programs',
    contents: '/contents',
    users: '/users',
    students: '/students'
  },

  // V1 API Endpoints (usando /v1)
  endpointsV1: {
    userType: '/app/user-type',
    clients: '/app/clients',
    person: '/app/person',
    user: '/app/user',
    profile: '/app/profile',
    serviceTypes: '/app/service-types',
    logs: '/logs'
  }
};

// QR Configuration
export const QR_CONFIG = {
  verificationUrlBase: 'https://asecapt.com/verify',
  size: 300,
  errorCorrectionLevel: 'M' as const,
  format: 'PNG' as const,
  margin: 4,
  darkColor: '#000000',
  lightColor: '#FFFFFF'
};

// File Upload Configuration
export const UPLOAD_CONFIG = {
  maxFileSize: 10 * 1024 * 1024, // 10MB
  allowedTypes: ['application/pdf'],
  allowedExtensions: ['.pdf'],
  uploadPath: '/uploads',
  maxFilesPerUpload: 1
};

// Certificate Configuration
export const CERTIFICATE_CONFIG = {
  numberPrefix: 'ASECAPT',
  tokenPrefix: 'ASC',
  validityYears: 5,
  qrCodePrefix: 'QR',
  formats: {
    dateFormat: 'DD/MM/YYYY',
    dateTimeFormat: 'DD/MM/YYYY HH:mm',
    numberFormat: 'ASECAPT-YYYY-#####'
  }
};

// UI Configuration
export const UI_CONFIG = {
  debounceTime: 300,
  toastDuration: 5000,
  loadingTimeout: 30000,
  pagination: {
    defaultPageSize: 10,
    pageSizeOptions: [10, 25, 50, 100]
  },
  theme: {
    primaryColor: '#007bff',
    successColor: '#28a745',
    warningColor: '#ffc107',
    dangerColor: '#dc3545',
    infoColor: '#17a2b8'
  }
};

// Validation Rules
export const VALIDATION_RULES = {
  certificate: {
    minNumberLength: 8,
    maxNumberLength: 50,
    tokenLength: 32
  },
  enrollment: {
    minGrade: 0,
    maxGrade: 100,
    minAttendance: 0,
    maxAttendance: 100
  },
  search: {
    minQueryLength: 2,
    maxQueryLength: 100
  }
};

// Error Messages
export const ERROR_MESSAGES = {
  api: {
    connectionError: 'Error de conexión con el servidor',
    timeout: 'Tiempo de espera agotado',
    unauthorized: 'No autorizado',
    forbidden: 'Acceso denegado',
    notFound: 'Recurso no encontrado',
    serverError: 'Error interno del servidor'
  },
  validation: {
    required: 'Este campo es requerido',
    invalidEmail: 'Email inválido',
    invalidFile: 'Archivo inválido',
    fileTooLarge: 'Archivo demasiado grande',
    invalidFileType: 'Tipo de archivo no permitido'
  },
  certificate: {
    generationFailed: 'Error al generar certificado',
    notFound: 'Certificado no encontrado',
    expired: 'Certificado expirado',
    revoked: 'Certificado revocado'
  }
};

// Success Messages
export const SUCCESS_MESSAGES = {
  certificate: {
    generated: 'Certificado generado exitosamente',
    uploaded: 'Archivo subido exitosamente',
    updated: 'Certificado actualizado',
    revoked: 'Certificado revocado'
  },
  enrollment: {
    created: 'Inscripción creada exitosamente',
    updated: 'Inscripción actualizada',
    completed: 'Inscripción completada'
  }
};

// Build full API URLs helper
export const buildApiUrl = (endpoint: string): string => {
  // Ensure endpoint starts with '/'
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  return `${APP_CONFIG.api.baseUrl}${cleanEndpoint}`;
};

// Build verification URL helper
export const buildVerificationUrl = (token: string): string => {
  return `${QR_CONFIG.verificationUrlBase}/${token}`;
};

// Build frontend URL helper
export const buildFrontendUrl = (path: string): string => {
  return `${APP_CONFIG.frontend.baseUrl}${path}`;
};
