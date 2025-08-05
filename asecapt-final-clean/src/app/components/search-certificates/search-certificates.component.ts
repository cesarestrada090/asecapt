import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CertificateService, Certificate } from '../../services/certificate.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-search-certificates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-certificates.component.html',
  styleUrl: './search-certificates.component.css'
})
export class SearchCertificatesComponent implements OnInit {
  @Output() message = new EventEmitter<{ type: 'success' | 'error', message: string }>();

  // Data lists
  allCertificates: Certificate[] = [];
  filteredCertificates: Certificate[] = [];

  // Loading states
  isLoadingCertificates: boolean = false;
  isSearching: boolean = false;

  // Search form
  searchForm = {
    query: '',
    status: 'active'
  };

  constructor(private certificateService: CertificateService) {}

  ngOnInit() {
    this.loadCertificates();
  }

  // === DATA LOADING ===

  loadCertificates() {
    this.isLoadingCertificates = true;
    this.certificateService.getAllCertificates()
      .pipe(
        catchError(error => {
          console.error('Error loading certificates:', error);
          this.emitMessage('error', 'Error cargando certificados');
          return of([]);
        })
      )
      .subscribe(certificates => {
        this.allCertificates = certificates;
        this.filteredCertificates = [...certificates];
        this.isLoadingCertificates = false;
      });
  }

  // === SEARCH ===

  searchCertificates() {
    if (!this.searchForm.query.trim()) {
      this.filteredCertificates = this.allCertificates.filter(cert => 
        !this.searchForm.status || cert.status === this.searchForm.status
      );
      return;
    }

    this.isSearching = true;
    this.certificateService.searchCertificates(this.searchForm.query, this.searchForm.status)
      .pipe(
        catchError(error => {
          console.error('Error searching certificates:', error);
          this.emitMessage('error', 'Error en la búsqueda de certificados');
          return of([]);
        })
      )
      .subscribe(certificates => {
        this.filteredCertificates = certificates;
        this.isSearching = false;
      });
  }

  clearSearch() {
    this.searchForm.query = '';
    this.searchForm.status = 'active';
    this.filteredCertificates = [...this.allCertificates];
  }

  // === CERTIFICATE ACTIONS ===

  downloadCertificateQR(certificate: Certificate) {
    if (certificate.qrCode?.qrDataURL) {
      this.certificateService.downloadQRCode(
        certificate.qrCode.qrDataURL, 
        certificate.certificateNumber
      );
      this.emitMessage('success', 'QR descargado exitosamente');
    } else {
      this.emitMessage('error', 'QR no disponible para descarga');
    }
  }

  viewCertificateDetails(certificate: Certificate) {
    // Open verification URL in new tab
    if (certificate.verificationUrl) {
      window.open(certificate.verificationUrl, '_blank');
    } else {
      this.emitMessage('error', 'URL de verificación no disponible');
    }
  }

  revokeCertificate(certificateId: number) {
    const reason = prompt('Ingresa la razón para revocar este certificado:');
    if (!reason?.trim()) {
      return;
    }

    this.certificateService.revokeCertificate(certificateId, { 
      reason: reason.trim(), 
      revokedByUserId: 1 // TODO: Get from auth service
    })
    .pipe(
      catchError(error => {
        console.error('Error revoking certificate:', error);
        this.emitMessage('error', 'Error revocando certificado');
        return of(null);
      })
    )
         .subscribe(certificate => {
       if (certificate) {
         this.emitMessage('success', 'Certificado revocado exitosamente');
         this.loadCertificates(); // Reload to update status
       } else {
         this.emitMessage('error', 'Error revocando certificado');
       }
     });
  }

  reactivateCertificate(certificateId: number) {
    this.certificateService.reactivateCertificate(certificateId)
      .pipe(
        catchError(error => {
          console.error('Error reactivating certificate:', error);
          this.emitMessage('error', 'Error reactivando certificado');
          return of(null);
        })
      )
      .subscribe(certificate => {
        if (certificate) {
          this.emitMessage('success', 'Certificado reactivado exitosamente');
          this.loadCertificates(); // Reload to update status
        } else {
          this.emitMessage('error', 'Error reactivando certificado');
        }
      });
  }

  // === UI HELPERS ===

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'active': return 'badge bg-success';
      case 'revoked': return 'badge bg-danger';
      case 'expired': return 'badge bg-warning';
      default: return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'active': return 'Activo';
      case 'revoked': return 'Revocado';
      case 'expired': return 'Expirado';
      default: return status;
    }
  }

  formatDate(date: string | Date | null): string {
    if (!date) return 'N/A';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('es-ES');
  }

  formatDateTime(date: string | Date | null): string {
    if (!date) return 'N/A';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleString('es-ES');
  }

  getStudentName(certificate: Certificate): string {
    // TODO: Implement proper student name retrieval
    return certificate.studentName || `Certificado #${certificate.id}`;
  }

  getProgramName(certificate: Certificate): string {
    // TODO: Implement proper program name retrieval
    return certificate.courseName || `Programa #${certificate.enrollmentId}`;
  }

  getCourseTypeText(courseType: string | undefined): string {
    switch (courseType) {
      case 'course': return 'Curso';
      case 'specialization': return 'Especialización';
      case 'certification': return 'Certificación';
      default: return 'Programa';
    }
  }

  // === FILTERS ===

  get availableStatuses() {
    return [
      { value: '', label: 'Todos los estados' },
      { value: 'active', label: 'Activos' },
      { value: 'revoked', label: 'Revocados' },
      { value: 'expired', label: 'Expirados' }
    ];
  }

  onStatusFilterChange() {
    this.searchCertificates();
  }

  // === STATISTICS ===

  get totalCertificates(): number {
    return this.allCertificates.length;
  }

  get activeCertificates(): number {
    return this.allCertificates.filter(cert => cert.status === 'active').length;
  }

  get revokedCertificates(): number {
    return this.allCertificates.filter(cert => cert.status === 'revoked').length;
  }

  get expiredCertificates(): number {
    return this.allCertificates.filter(cert => cert.status === 'expired').length;
  }

  // === UTILITY ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === TRACKING ===

  trackByCertificateId(index: number, item: Certificate): number {
    return item.id;
  }
} 