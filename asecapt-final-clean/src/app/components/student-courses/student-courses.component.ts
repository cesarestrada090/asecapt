import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService, User } from '../../services/auth.service';
import { UserService } from '../../services/user.service';

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

  // Messages
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
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
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.errorMessage = 'Error al cargar las inscripciones';
        this.loadingEnrollments = false;
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
    return enrollment.status === 'completed' && enrollment.certificate;
  }

  downloadCertificate(enrollment: any) {
    if (!this.canDownloadCertificate(enrollment)) {
      this.errorMessage = 'No se puede descargar el certificado. El curso debe estar completado.';
      return;
    }

    this.userService.downloadCertificate(enrollment.id).subscribe({
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
        console.error('Error downloading certificate:', error);
        this.errorMessage = 'Error al descargar el certificado';
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
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
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
