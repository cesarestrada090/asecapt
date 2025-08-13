import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService, User } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { CertificateService } from '../../services/certificate.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-student-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-courses.component.html',
  styleUrl: './student-courses.component.css'
})
export class StudentCoursesComponent implements OnInit {
  // User data
  currentUser: User | null = null;

  // Enrollments data
  enrollments: any[] = [];
  loadingEnrollments: boolean = false;

  // Certificates data - Map to store which enrollments have certificates
  enrollmentCertificates: Map<number, boolean> = new Map();
  loadingCertificates: boolean = false;

  // Messages
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private certificateService: CertificateService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadEnrollments();
  }

  // === ENROLLMENTS ===

  loadEnrollments() {
    if (!this.currentUser) return;

    this.loadingEnrollments = true;
    this.userService.getEnrollments(this.currentUser.id).subscribe({
      next: (response) => {
        this.enrollments = response;
        this.loadingEnrollments = false;
        // Después de cargar enrollments, verificar certificados
        this.loadCertificatesInfo();
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.errorMessage = 'Error al cargar las inscripciones';
        this.loadingEnrollments = false;
      }
    });
  }

  // === CERTIFICATES ===

  loadCertificatesInfo() {
    // Obtener solo los enrollments completados para verificar certificados
    const completedEnrollments = this.enrollments.filter(e => e.status === 'completed');

    if (completedEnrollments.length === 0) {
      return; // No hay enrollments completados
    }

    this.loadingCertificates = true;

    // Crear requests para verificar certificados de todos los enrollments completados
    const certificateRequests = completedEnrollments.map(enrollment =>
      this.certificateService.getCertificateByEnrollment(enrollment.id).pipe(
        catchError(error => {
          // Si hay error (404 = no certificado), retornar null
          console.log(`No certificate found for enrollment ${enrollment.id}`);
          return of(null);
        })
      )
    );

    // Ejecutar todas las requests en paralelo
    forkJoin(certificateRequests).subscribe({
      next: (certificates) => {
        // Limpiar el mapa de certificados
        this.enrollmentCertificates.clear();

        // Mapear resultados: true si existe certificado, false si no
        completedEnrollments.forEach((enrollment, index) => {
          const hasCertificate = certificates[index] !== null;
          this.enrollmentCertificates.set(enrollment.id, hasCertificate);
        });

        this.loadingCertificates = false;
        console.log('Certificates info loaded:', this.enrollmentCertificates);
      },
      error: (error) => {
        console.error('Error loading certificates info:', error);
        this.loadingCertificates = false;
        // En caso de error, marcar todos como sin certificado
        completedEnrollments.forEach(enrollment => {
          this.enrollmentCertificates.set(enrollment.id, false);
        });
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'badge bg-success';
      case 'in_progress':
        return 'badge bg-primary';
      case 'enrolled':
        return 'badge bg-warning';
      case 'suspended':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      case 'in_progress':
        return 'En Progreso';
      case 'enrolled':
        return 'Inscrito';
      case 'suspended':
        return 'Suspendido';
      default:
        return 'Desconocido';
    }
  }

  canDownloadCertificate(enrollment: any): boolean {
    // Un estudiante puede descargar su certificado si:
    // 1. Ha completado el curso Y
    // 2. Existe un certificado generado para ese enrollment
    return enrollment.status === 'completed' &&
           this.enrollmentCertificates.get(enrollment.id) === true;
  }

  downloadCertificate(enrollment: any) {
    if (!this.canDownloadCertificate(enrollment)) {
      this.errorMessage = 'No se puede descargar el certificado. El curso debe estar completado y debe existir un certificado generado.';
      return;
    }

    // Primero obtener el certificado por enrollment ID para conseguir el certificate ID
    this.certificateService.getCertificateByEnrollment(enrollment.id).subscribe({
      next: (certificate) => {
        if (certificate && certificate.id) {
          // Ahora usar el certificate ID para descargarlo
          this.certificateService.downloadCertificate(certificate.id).subscribe({
            next: (blob) => {
              const url = window.URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = `certificado_${enrollment.program?.title || 'curso'}.pdf`;
              document.body.appendChild(a);
              a.click();
              document.body.removeChild(a);
              window.URL.revokeObjectURL(url);
              this.successMessage = 'Certificado descargado exitosamente';
            },
            error: (error) => {
              console.error('Error downloading certificate file:', error);
              this.errorMessage = 'Error al descargar el archivo del certificado';
            }
          });
        } else {
          this.errorMessage = 'No se pudo obtener la información del certificado';
        }
      },
      error: (error) => {
        console.error('Error getting certificate info:', error);
        this.errorMessage = 'Error al obtener la información del certificado';
      }
    });
  }

  getCompletedCount(): number {
    return this.enrollments.filter(e => e.status === 'completed').length;
  }

  getInProgressCount(): number {
    return this.enrollments.filter(e => e.status === 'in_progress' || e.status === 'enrolled').length;
  }

  getCertificatesCount(): number {
    return this.enrollments.filter(e => this.canDownloadCertificate(e)).length;
  }

  getAverageGrade(): number {
    const completedWithGrades = this.enrollments.filter(e =>
      e.status === 'completed' &&
      e.finalGrade !== null &&
      e.finalGrade !== undefined
    );

    if (completedWithGrades.length === 0) return 0;

    const totalGrades = completedWithGrades.reduce((sum, e) => sum + e.finalGrade, 0);
    return Math.round(totalGrades / completedWithGrades.length);
  }

  // === UTILITIES ===

  clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'No especificado';
    try {
      // Si es una fecha en formato YYYY-MM-DD (solo fecha), crear Date de manera local
      if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
        const [year, month, day] = dateString.split('-').map(Number);
        const date = new Date(year, month - 1, day); // month - 1 porque Date usa 0-indexado para meses
        return date.toLocaleDateString('es-ES');
      }

      // Para otros formatos de fecha, usar el comportamiento normal
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES');
    } catch {
      return 'Fecha inválida';
    }
  }

  refreshData() {
    this.clearMessages();
    this.loadEnrollments();
  }

  // Additional helper methods for the template
  trackByEnrollmentId(index: number, enrollment: any): any {
    return enrollment.id || index;
  }

  getGradeBadgeClass(grade: number): string {
    if (grade >= 90) return 'grade-badge excellent';
    if (grade >= 80) return 'grade-badge good';
    if (grade >= 70) return 'grade-badge satisfactory';
    return 'grade-badge needs-improvement';
  }

  getGradeProgressClass(grade: number): string {
    if (grade >= 90) return 'progress-bar bg-excellent';
    if (grade >= 80) return 'progress-bar bg-good';
    if (grade >= 70) return 'progress-bar bg-satisfactory';
    return 'progress-bar bg-needs-improvement';
  }

  getAttendanceBadgeClass(attendance: number): string {
    if (attendance >= 85) return 'attendance-badge excellent-attendance';
    if (attendance >= 70) return 'attendance-badge good-attendance';
    return 'attendance-badge poor-attendance';
  }
}
