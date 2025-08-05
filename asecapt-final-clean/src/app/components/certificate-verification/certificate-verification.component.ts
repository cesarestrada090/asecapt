import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VerificationService, CertificateVerificationResponse } from '../../services/verification.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-certificate-verification',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './certificate-verification.component.html',
  styleUrl: './certificate-verification.component.css'
})
export class CertificateVerificationComponent implements OnInit {
  
  // Verification data
  verificationToken: string | null = null;
  verificationResult: CertificateVerificationResponse | null = null;
  
  // UI states
  isLoading: boolean = false;
  isValid: boolean = false;
  errorMessage: string = '';
  showDetails: boolean = false;

  // Manual verification
  manualToken: string = '';
  showManualForm: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private verificationService: VerificationService
  ) {}

  ngOnInit() {
    // Get token from URL params
    this.route.params.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.verificationToken = token;
        this.verifyCertificate(token);
      } else {
        this.showManualForm = true;
      }
    });
  }

  verifyCertificate(token: string) {
    if (!token) {
      this.errorMessage = 'Token de verificación requerido';
      return;
    }

    // Validate token format
    if (!this.verificationService.isValidTokenFormat(token)) {
      this.errorMessage = 'Formato de token inválido';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.verificationResult = null;

    this.verificationService.verifyCertificate(token)
      .pipe(
        catchError(error => {
          console.error('Error verifying certificate:', error);
          this.errorMessage = 'Error al verificar el certificado. Inténtelo nuevamente.';
          this.isLoading = false;
          return of(null);
        })
      )
      .subscribe(response => {
        this.isLoading = false;
        if (response) {
          this.verificationResult = response;
          this.isValid = response.valid;
          this.showDetails = response.valid;
        } else {
          this.errorMessage = 'No se pudo verificar el certificado';
        }
      });
  }

  verifyManualToken() {
    const token = this.manualToken.trim();
    if (!token) {
      this.errorMessage = 'Por favor ingrese un token de verificación';
      return;
    }

    // Extract token from URL if needed
    const extractedToken = this.verificationService.extractTokenFromUrl(token);
    if (extractedToken) {
      this.verificationToken = extractedToken;
      this.verifyCertificate(extractedToken);
      this.showManualForm = false;
    } else {
      this.errorMessage = 'Token o URL inválido';
    }
  }

  // UI Helper methods
  getStatusIcon(): string {
    if (!this.verificationResult) return 'fas fa-question';
    return this.verificationService.getStatusIcon(this.verificationResult.status);
  }

  getStatusColorClass(): string {
    if (!this.verificationResult) return 'text-secondary';
    return this.verificationService.getStatusColorClass(this.verificationResult.status);
  }

  getStatusBgClass(): string {
    if (!this.verificationResult) return 'bg-light';
    return this.verificationService.getStatusBgClass(this.verificationResult.status);
  }

  getStatusDisplayText(): string {
    if (!this.verificationResult) return 'Estado Desconocido';
    return this.verificationService.getStatusDisplayText(this.verificationResult.status);
  }

  formatDate(dateString: string): string {
    return this.verificationService.formatDate(dateString);
  }

  formatDateTime(dateString: string): string {
    return this.verificationService.formatDateTime(dateString);
  }

  calculateCourseDuration(): string {
    if (!this.verificationResult?.certificateInfo?.enrollment) return '';
    const enrollment = this.verificationResult.certificateInfo.enrollment;
    return this.verificationService.calculateCourseDuration(enrollment.startDate, enrollment.completionDate);
  }

  toggleDetails() {
    this.showDetails = !this.showDetails;
  }

  printVerification() {
    window.print();
  }

  generateReport(): string {
    if (!this.verificationResult) return '';
    return this.verificationService.generateVerificationReport(this.verificationResult);
  }

  downloadReport() {
    const report = this.generateReport();
    const blob = new Blob([report], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `verificacion_certificado_${this.verificationResult?.verificationToken}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  goBack() {
    this.router.navigate(['/']);
  }

  verifyAnother() {
    this.verificationToken = null;
    this.verificationResult = null;
    this.manualToken = '';
    this.showManualForm = true;
    this.errorMessage = '';
    this.isValid = false;
    this.showDetails = false;
  }

  // Share verification result
  shareVerification() {
    if (!this.verificationResult || !this.verificationToken) return;

    const shareData = {
      title: 'Verificación de Certificado ASECAPT',
      text: `Certificado ${this.verificationResult.valid ? 'VÁLIDO' : 'INVÁLIDO'}: ${this.verificationResult.message}`,
      url: `${window.location.origin}/verify/${this.verificationToken}`
    };

    if (navigator.share) {
      navigator.share(shareData).catch(console.error);
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(shareData.url).then(() => {
        alert('URL copiada al portapapeles');
      }).catch(() => {
        alert('No se pudo compartir automáticamente');
      });
    }
  }

  // Get grade color class based on score
  getGradeColorClass(grade: number): string {
    if (grade >= 90) return 'text-success';
    if (grade >= 80) return 'text-primary';
    if (grade >= 70) return 'text-warning';
    return 'text-danger';
  }

  // Get attendance color class based on percentage
  getAttendanceColorClass(attendance: number): string {
    if (attendance >= 95) return 'text-success';
    if (attendance >= 90) return 'text-primary';
    if (attendance >= 85) return 'text-warning';
    return 'text-danger';
  }

  // Format grade display
  formatGrade(grade: number): string {
    return `${grade.toFixed(1)}/100`;
  }

  // Format attendance display
  formatAttendance(attendance: number): string {
    return `${attendance.toFixed(1)}%`;
  }
} 