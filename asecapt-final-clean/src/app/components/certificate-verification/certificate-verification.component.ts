import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { buildApiUrl } from '../../constants';

interface CertificateVerificationResponse {
  valid: boolean;
  certificate?: {
    id: number;
    certificateCode: string;
    issuedDate: string;
    createdAt: string;
    student: {
      firstName: string;
      lastName: string;
      documentNumber: string;
      email: string;
    };
    program: {
      title: string;
      description: string;
      duration: string;
      credits: number;
    };
    enrollment: {
      status: string;
      enrollmentDate: string;
      completionDate: string;
      finalGrade: number;
      attendancePercentage: number;
    };
  };
  errorCode?: string;
  errorMessage?: string;
}

@Component({
  selector: 'app-certificate-verification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './certificate-verification.component.html',
  styleUrl: './certificate-verification.component.css'
})
export class CertificateVerificationComponent implements OnInit {
  certificateCode: string = '';
  certificateData: CertificateVerificationResponse | null = null;
  isLoading: boolean = false;
  isDownloading: boolean = false;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.certificateCode = params['certificateCode'];
      if (this.certificateCode) {
        this.verifyCertificate();
      }
    });
  }

  verifyCertificate() {
    this.isLoading = true;
    this.error = '';

    const apiUrl = buildApiUrl(`public/certificate/${this.certificateCode}`);

    this.http.get<CertificateVerificationResponse>(apiUrl)
      .pipe(
        catchError(error => {
          console.error('Error verifying certificate:', error);
          this.error = 'Error al verificar el certificado. Por favor, intente nuevamente.';
          this.isLoading = false;
          return of(null);
        })
      )
      .subscribe(response => {
        this.certificateData = response;
        this.isLoading = false;

        if (response && !response.valid) {
          this.error = response.errorMessage || 'Certificado no válido';
        }
      });
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    try {
      // Si es una fecha en formato YYYY-MM-DD (solo fecha), crear Date de manera local
      if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
        const [year, month, day] = dateString.split('-').map(Number);
        const date = new Date(year, month - 1, day); // month - 1 porque Date usa 0-indexado para meses
        return date.toLocaleDateString('es-ES', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        });
      }

      // Para otros formatos de fecha, usar el comportamiento normal
      return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      return 'Fecha inválida';
    }
  }

  /**
   * Get status badge class
   */
  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'badge bg-success';
      case 'in_progress':
        return 'badge bg-warning text-dark';
      case 'enrolled':
        return 'badge bg-info';
      default:
        return 'badge bg-secondary';
    }
  }

  /**
   * Get status text
   */
  getStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      case 'in_progress':
        return 'En Progreso';
      case 'enrolled':
        return 'Matriculado';
      default:
        return status || 'Desconocido';
    }
  }

  /**
   * Get grade color class
   */
  getGradeColorClass(grade: number): string {
    if (grade >= 80) return 'text-success';
    if (grade >= 70) return 'text-warning';
    return 'text-danger';
  }

  /**
   * Get attendance color class
   */
  getAttendanceColorClass(attendance: number): string {
    if (attendance >= 80) return 'text-success';
    if (attendance >= 70) return 'text-warning';
    return 'text-danger';
  }

  /**
   * Print certificate
   */
  printCertificate() {
    window.print();
  }

  /**
   * Get current date formatted
   */
  getCurrentDate(): string {
    return this.formatDate(new Date().toISOString());
  }

  /**
   * Download certificate file
   */
  downloadCertificate(): void {
    if (!this.certificateData?.certificate?.id) {
      console.error('No certificate ID available for download');
      return;
    }

    this.isDownloading = true;

    const certificateId = this.certificateData.certificate.id;
    const downloadUrl = buildApiUrl(`public/certificate/download/${certificateId}`);

    this.http.get(downloadUrl, {
      responseType: 'blob',
      observe: 'response'
    }).pipe(
      catchError(error => {
        console.error('Error downloading certificate:', error);
        alert('Error al descargar el certificado. Por favor, intente nuevamente.');
        return of(null);
      })
    ).subscribe(response => {
      this.isDownloading = false;

      if (response && response.body) {
        // Get filename from Content-Disposition header or use default
        let filename = 'certificado.pdf';
        const contentDisposition = response.headers.get('Content-Disposition');
        if (contentDisposition) {
          const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
          if (matches != null && matches[1]) {
            filename = matches[1].replace(/['"]/g, '');
          }
        }

        // Create download link
        const blob = new Blob([response.body], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }
    });
  }

  // Safe getter methods to avoid null reference errors

  /**
   * Safe access to certificate data
   */
  getCertificate() {
    return this.certificateData?.certificate || null;
  }

  /**
   * Safe access to student data
   */
  getStudent() {
    return this.getCertificate()?.student || null;
  }

  /**
   * Safe access to program data
   */
  getProgram() {
    return this.getCertificate()?.program || null;
  }

  /**
   * Safe access to enrollment data
   */
  getEnrollment() {
    return this.getCertificate()?.enrollment || null;
  }

  /**
   * Get student full name safely
   */
  getStudentFullName(): string {
    const student = this.getStudent();
    if (!student?.firstName || !student?.lastName) return 'N/A';
    return `${student.firstName} ${student.lastName}`;
  }

  /**
   * Get student document safely
   */
  getStudentDocument(): string {
    return this.getStudent()?.documentNumber || 'N/A';
  }

  /**
   * Get student email safely
   */
  getStudentEmail(): string {
    return this.getStudent()?.email || 'N/A';
  }

  /**
   * Get program title safely
   */
  getProgramTitle(): string {
    return this.getProgram()?.title || 'N/A';
  }

  /**
   * Get program description safely
   */
  getProgramDescription(): string {
    return this.getProgram()?.description || 'N/A';
  }

  /**
   * Get program duration safely
   */
  getProgramDuration(): string {
    return this.getProgram()?.duration || 'N/A';
  }

  /**
   * Get program credits safely
   */
  getProgramCredits(): number {
    return this.getProgram()?.credits || 0;
  }

  /**
   * Get enrollment status safely
   */
  getEnrollmentStatus(): string {
    return this.getEnrollment()?.status || '';
  }

  /**
   * Get final grade safely
   */
  getFinalGrade(): number {
    return this.getEnrollment()?.finalGrade || 0;
  }

  /**
   * Get attendance percentage safely
   */
  getAttendancePercentage(): number {
    return this.getEnrollment()?.attendancePercentage || 0;
  }

  /**
   * Get certificate code safely
   */
  getCertificateCode(): string {
    return this.getCertificate()?.certificateCode || '';
  }

  /**
   * Get issued date safely
   */
  getIssuedDate(): string {
    return this.getCertificate()?.issuedDate || '';
  }

  /**
   * Get enrollment date safely
   */
  getEnrollmentDate(): string {
    return this.getEnrollment()?.enrollmentDate || '';
  }

  /**
   * Get completion date safely
   */
  getCompletionDate(): string {
    return this.getEnrollment()?.completionDate || '';
  }
}
