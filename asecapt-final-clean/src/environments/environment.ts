export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  baseUrl: 'http://localhost:4200',
  
  // API Endpoints
  endpoints: {
    enrollments: '/enrollments',
    certificates: '/certificates',
    verification: '/verify',
    programs: '/programs',
    users: '/users'
  },
  
  // QR Configuration
  qr: {
    verificationUrlBase: 'https://asecapt.com/verify',
    size: 300,
    errorCorrectionLevel: 'M'
  },
  
  // File Upload Configuration
  upload: {
    maxFileSize: 10 * 1024 * 1024, // 10MB
    allowedTypes: ['application/pdf'],
    uploadPath: '/uploads'
  },
  
  // Certificate Configuration
  certificate: {
    numberPrefix: 'ASECAPT',
    tokenPrefix: 'ASC',
    validityYears: 5
  }
}; 