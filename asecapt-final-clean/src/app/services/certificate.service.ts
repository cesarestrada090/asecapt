import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, buildApiUrl, UPLOAD_CONFIG, ERROR_MESSAGES, SUCCESS_MESSAGES } from '../constants';

export interface Certificate {
  id: number;
  enrollmentId: number;
  certificateNumber: string;
  issueDate: string;
  expirationDate?: string;
  certificateFilePath?: string;
  verificationToken: string;
  verificationUrl: string;
  status: 'active' | 'revoked' | 'expired';
  issuedByUserId?: number;
  scanCount: number;
  lastScannedAt?: string;
  revokedAt?: string;
  revokedReason?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  // TODO: Add enrollment details when implementing complex joins
  // enrollment?: any;
  
  // Additional properties for UI compatibility
  qrCode?: {
    qrDataURL: string;
  };
  studentName?: string;
  courseName?: string;
  courseType?: string;
}

export interface GenerateCertificateRequest {
  enrollmentId: number;
  certificateFilePath: string;
  issuedByUserId: number;
}

export interface CertificateResponse {
  success: boolean;
  message: string;
  certificate?: Certificate;
  qrCodeDataURL?: string;
}

export interface RevokeCertificateRequest {
  reason: string;
  revokedByUserId: number;
}

export interface CertificateStats {
  activeCertificates: number;
  revokedCertificates: number;
  mostScanned: Certificate[];
}

export interface UploadResponse {
  success: string;
  filePath: string;
  fileName: string;
  message: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private apiUrl = buildApiUrl(APP_CONFIG.endpoints.certificates);

  constructor(private http: HttpClient) { }

  // Get all certificates
  getAllCertificates(): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(this.apiUrl);
  }

  // Get active certificates
  getActiveCertificates(): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(`${this.apiUrl}/active`);
  }

  // Search certificates
  searchCertificates(query?: string, status?: string): Observable<Certificate[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (status) params = params.set('status', status);
    
    return this.http.get<Certificate[]>(`${this.apiUrl}/search`, { params });
  }

  // Get certificate by ID
  getCertificateById(id: number): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.apiUrl}/${id}`);
  }

  // Generate new certificate
  generateCertificate(request: GenerateCertificateRequest): Observable<CertificateResponse> {
    return this.http.post<CertificateResponse>(`${this.apiUrl}/generate`, request);
  }

  // Upload certificate PDF
  uploadCertificatePDF(file: File): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('certificate', file);
    
    return this.http.post<UploadResponse>(`${this.apiUrl}/upload-pdf`, formData);
  }

  // Get QR code for certificate
  getQRCode(certificateId: number): Observable<{qrCodeDataURL: string, verificationUrl: string, certificateNumber: string}> {
    return this.http.get<{qrCodeDataURL: string, verificationUrl: string, certificateNumber: string}>(`${this.apiUrl}/${certificateId}/qr`);
  }

  // Revoke certificate
  revokeCertificate(id: number, request: RevokeCertificateRequest): Observable<Certificate> {
    return this.http.put<Certificate>(`${this.apiUrl}/${id}/revoke`, request);
  }

  // Reactivate certificate
  reactivateCertificate(id: number): Observable<Certificate> {
    return this.http.put<Certificate>(`${this.apiUrl}/${id}/reactivate`, {});
  }

  // Get certificate statistics
  getCertificateStats(): Observable<CertificateStats> {
    return this.http.get<CertificateStats>(`${this.apiUrl}/stats`);
  }

  // Helper method to download QR code
  downloadQRCode(qrCodeDataURL: string, certificateNumber: string): void {
    const link = document.createElement('a');
    link.href = qrCodeDataURL;
    link.download = `QR_${certificateNumber}.svg`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // Helper method to validate file type
  isValidPDFFile(file: File): boolean {
    return file.type === 'application/pdf';
  }

  // Helper method to format file size
  formatFileSize(bytes: number): string {
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    if (bytes === 0) return '0 Bytes';
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
  }

  // Generate certificate number preview (for UI)
  generateCertificateNumberPreview(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const randomPart = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    return `ASECAPT-${year}${month}-${randomPart}`;
  }

  // Get certificate type display text
  getCertificateTypeText(type: string): string {
    switch (type) {
      case 'curso':
        return 'Curso';
      case 'diplomado':
        return 'Diplomado';
      case 'especializacion':
        return 'Especialización';
      default:
        return 'Curso';
    }
  }

  // Get status badge class for UI
  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'active':
        return 'bg-success';
      case 'revoked':
        return 'bg-danger';
      case 'expired':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }

  // Get status text in Spanish
  getStatusText(status: string): string {
    switch (status) {
      case 'active':
        return 'Activo';
      case 'revoked':
        return 'Revocado';
      case 'expired':
        return 'Expirado';
      default:
        return 'Desconocido';
    }
  }
} 