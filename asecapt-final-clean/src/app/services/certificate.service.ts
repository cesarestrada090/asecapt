import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { buildApiUrl } from '../constants';

export interface Certificate {
  id: number;
  certificateCode: string;
  enrollment: any;
  filePath: string;
  fileName: string;
  qrCodePath: string;
  issuedDate: string;
  createdAt: string;
  updatedAt?: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private apiUrl = buildApiUrl('certificates');

  constructor(private http: HttpClient) {}

  /**
   * Upload certificate file for an enrollment
   */
  uploadCertificate(enrollmentId: number, file: File, issuedDate?: Date): Observable<Certificate> {
    const formData = new FormData();
    formData.append('enrollmentId', enrollmentId.toString());
    formData.append('file', file);
    
    if (issuedDate) {
      formData.append('issuedDate', issuedDate.toISOString());
    }

    return this.http.post<Certificate>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Get certificate by code
   */
  getCertificateByCode(certificateCode: string): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.apiUrl}/code/${certificateCode}`);
  }

  /**
   * Get all certificates for a student
   */
  getCertificatesByStudent(studentId: number): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(`${this.apiUrl}/student/${studentId}`);
  }

  /**
   * Get all certificates for a program
   */
  getCertificatesByProgram(programId: number): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(`${this.apiUrl}/program/${programId}`);
  }

  /**
   * Get certificate by enrollment ID
   */
  getCertificateByEnrollment(enrollmentId: number): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.apiUrl}/enrollment/${enrollmentId}`);
  }

  /**
   * Get all certificates
   */
  getAllCertificates(): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(this.apiUrl);
  }

  /**
   * Delete certificate
   */
  deleteCertificate(certificateId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${certificateId}`);
  }

  /**
   * Get certificate download URL
   */
  getCertificateDownloadUrl(certificateId: number): string {
    return `${this.apiUrl}/download/${certificateId}`;
  }

  /**
   * Get QR code download URL
   */
  getQRCodeDownloadUrl(certificateId: number): string {
    return `${this.apiUrl}/qr/${certificateId}`;
  }

  /**
   * Download certificate file
   */
  downloadCertificate(certificateId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${certificateId}`, {
      responseType: 'blob'
    });
  }

  /**
   * Download QR code
   */
  downloadQRCode(certificateId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/qr/${certificateId}`, {
      responseType: 'blob'
    });
  }

  /**
   * Check if enrollment has certificate
   */
  checkCertificateExists(enrollmentId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/enrollment/${enrollmentId}`);
  }
}