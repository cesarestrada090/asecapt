import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StudentInfo {
  fullName: string;
  documentNumber: string;
  email: string;
}

export interface ProgramInfo {
  name: string;
  credits: number;
  hours: number;
  duration: string;
}

export interface EnrollmentInfo {
  startDate: string;
  completionDate: string;
  finalGrade: number;
  attendancePercentage: number;
  student: StudentInfo;
  program: ProgramInfo;
}

export interface CertificateInfo {
  certificateNumber: string;
  issueDate: string;
  expirationDate?: string;
  enrollment: EnrollmentInfo;
}

export interface CertificateVerificationResponse {
  valid: boolean;
  status: 'valid' | 'revoked' | 'expired' | 'not_found' | 'invalid';
  message: string;
  verificationToken: string;
  verifiedAt: string;
  scanCount: number;
  certificateInfo?: CertificateInfo;
  revokedAt?: string;
  revokedReason?: string;
}

export interface CertificateStatusResponse {
  exists: boolean;
  status: string;
  certificateNumber?: string;
  issueDate?: string;
}

@Injectable({
  providedIn: 'root'
})
export class VerificationService {
  private apiUrl = 'http://localhost:8080/api/verify'; // TODO: Move to environment

  constructor(private http: HttpClient) { }

  // Verify certificate using token from QR code
  verifyCertificate(token: string): Observable<CertificateVerificationResponse> {
    return this.http.get<CertificateVerificationResponse>(`${this.apiUrl}/${token}`);
  }

  // Get certificate status without logging (lightweight check)
  getCertificateStatus(token: string): Observable<CertificateStatusResponse> {
    return this.http.get<CertificateStatusResponse>(`${this.apiUrl}/${token}/status`);
  }

  // Extract token from QR URL
  extractTokenFromUrl(url: string): string | null {
    try {
      // Handle URLs like: https://asecapt.com/verify/ASC-24-A1B2C3D4
      const urlObj = new URL(url);
      const pathParts = urlObj.pathname.split('/');
      const verifyIndex = pathParts.indexOf('verify');
      
      if (verifyIndex !== -1 && pathParts.length > verifyIndex + 1) {
        return pathParts[verifyIndex + 1];
      }
      
      return null;
    } catch (error) {
      // If it's not a valid URL, assume it's already a token
      return url;
    }
  }

  // Validate token format
  isValidTokenFormat(token: string): boolean {
    // Token format: ASC-24-A1B2C3D4
    const tokenRegex = /^ASC-\d{2}-[A-Z0-9]{8}$/;
    return tokenRegex.test(token);
  }

  // Get status display text in Spanish
  getStatusDisplayText(status: string): string {
    switch (status) {
      case 'valid':
        return 'Certificado Válido';
      case 'revoked':
        return 'Certificado Revocado';
      case 'expired':
        return 'Certificado Expirado';
      case 'not_found':
        return 'Certificado No Encontrado';
      case 'invalid':
        return 'Certificado Inválido';
      default:
        return 'Estado Desconocido';
    }
  }

  // Get status icon for UI
  getStatusIcon(status: string): string {
    switch (status) {
      case 'valid':
        return 'fas fa-check-circle';
      case 'revoked':
        return 'fas fa-ban';
      case 'expired':
        return 'fas fa-clock';
      case 'not_found':
        return 'fas fa-question-circle';
      case 'invalid':
        return 'fas fa-exclamation-triangle';
      default:
        return 'fas fa-question';
    }
  }

  // Get status color class for UI
  getStatusColorClass(status: string): string {
    switch (status) {
      case 'valid':
        return 'text-success';
      case 'revoked':
        return 'text-danger';
      case 'expired':
        return 'text-warning';
      case 'not_found':
        return 'text-muted';
      case 'invalid':
        return 'text-danger';
      default:
        return 'text-secondary';
    }
  }

  // Get status background class for UI
  getStatusBgClass(status: string): string {
    switch (status) {
      case 'valid':
        return 'bg-success';
      case 'revoked':
        return 'bg-danger';
      case 'expired':
        return 'bg-warning';
      case 'not_found':
        return 'bg-secondary';
      case 'invalid':
        return 'bg-danger';
      default:
        return 'bg-light';
    }
  }

  // Format date for display
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  // Format date and time for display
  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Calculate course duration in a readable format
  calculateCourseDuration(startDate: string, completionDate: string): string {
    const start = new Date(startDate);
    const end = new Date(completionDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 30) {
      return `${diffDays} días`;
    } else if (diffDays < 365) {
      const months = Math.floor(diffDays / 30);
      return `${months} ${months === 1 ? 'mes' : 'meses'}`;
    } else {
      const years = Math.floor(diffDays / 365);
      const remainingMonths = Math.floor((diffDays % 365) / 30);
      if (remainingMonths === 0) {
        return `${years} ${years === 1 ? 'año' : 'años'}`;
      }
      return `${years} ${years === 1 ? 'año' : 'años'} y ${remainingMonths} ${remainingMonths === 1 ? 'mes' : 'meses'}`;
    }
  }

  // Generate verification report text
  generateVerificationReport(verification: CertificateVerificationResponse): string {
    if (!verification.valid || !verification.certificateInfo) {
      return `Verificación fallida: ${verification.message}`;
    }

    const info = verification.certificateInfo;
    const student = info.enrollment.student;
    const program = info.enrollment.program;

    return `
CERTIFICADO VERIFICADO EXITOSAMENTE

Estudiante: ${student.fullName}
Documento: ${student.documentNumber}
Email: ${student.email}

Programa: ${program.name}
Créditos: ${program.credits}
Horas académicas: ${program.hours}
Duración: ${program.duration}

Calificación final: ${info.enrollment.finalGrade}
Asistencia: ${info.enrollment.attendancePercentage}%

Fecha de inicio: ${this.formatDate(info.enrollment.startDate)}
Fecha de finalización: ${this.formatDate(info.enrollment.completionDate)}
Fecha de emisión: ${this.formatDate(info.issueDate)}

Número de certificado: ${info.certificateNumber}
Token de verificación: ${verification.verificationToken}
Verificado el: ${this.formatDateTime(verification.verifiedAt)}
Número de escaneos: ${verification.scanCount}
    `.trim();
  }
} 