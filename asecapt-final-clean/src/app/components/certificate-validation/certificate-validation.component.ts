import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { buildApiUrl } from '../../constants';
import { catchError, of } from 'rxjs';

interface SearchResponse {
  success: boolean;
  certificates?: any[];
  count?: number;
  errorCode?: string;
  errorMessage?: string;
}

@Component({
  selector: 'app-certificate-validation',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './certificate-validation.component.html',
  styleUrl: './certificate-validation.component.css'
})
export class CertificateValidationComponent {
  documentNumber: string = '';
  searchResult: SearchResponse | null = null;
  isSearching: boolean = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  onSearch() {
    if (this.documentNumber.trim()) {
      this.isSearching = true;
      this.searchResult = null;
      
      const apiUrl = buildApiUrl(`public/certificate/search?documentNumber=${encodeURIComponent(this.documentNumber.trim())}`);
      
      this.http.get<SearchResponse>(apiUrl)
        .pipe(
          catchError(error => {
            console.error('Error searching certificates:', error);
            return of({
              success: false,
              errorCode: 'NETWORK_ERROR',
              errorMessage: 'Error de conexión. Por favor, intente nuevamente.'
            });
          })
        )
        .subscribe(response => {
          this.searchResult = response;
          this.isSearching = false;
        });
    }
  }

  resetSearch() {
    this.searchResult = null;
    this.documentNumber = '';
  }

  /**
   * View full certificate details
   */
  viewCertificate(certificate: any) {
    if (certificate.certificateCode) {
      this.router.navigate(['/public/certificate', certificate.certificateCode]);
    }
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      return dateString;
    }
  }

  /**
   * Get student display name safely
   */
  getStudentName(certificate: any): string {
    if (certificate.student?.firstName && certificate.student?.lastName) {
      return `${certificate.student.firstName} ${certificate.student.lastName}`;
    }
    return 'N/A';
  }

  /**
   * Get program title safely
   */
  getProgramTitle(certificate: any): string {
    return certificate.program?.title || 'N/A';
  }

  /**
   * Get certificate status badge class
   */
  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'badge bg-success';
      case 'active':
        return 'badge bg-primary';
      case 'inactive':
        return 'badge bg-secondary';
      default:
        return 'badge bg-secondary';
    }
  }

  /**
   * Get certificate status text
   */
  getStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      case 'active':
        return 'Activo';
      case 'inactive':
        return 'Inactivo';
      default:
        return 'Sin estado';
    }
  }

  /**
   * Track by function for certificates list
   */
  trackByCertificateCode(index: number, certificate: any): string {
    return certificate.certificateCode || index.toString();
  }
}
