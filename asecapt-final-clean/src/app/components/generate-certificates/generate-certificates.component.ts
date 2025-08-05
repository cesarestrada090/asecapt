import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { CertificateService, Certificate, CertificateResponse, GenerateCertificateRequest } from '../../services/certificate.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-generate-certificates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './generate-certificates.component.html',
  styleUrl: './generate-certificates.component.css'
})
export class GenerateCertificatesComponent implements OnInit {
  @Output() message = new EventEmitter<{ type: 'success' | 'error', message: string }>();

  // Certificate form data
  certificateForm = {
    selectedEnrollmentId: null as number | null,
    pdfFile: null as File | null,
    studentName: '',
    studentDNI: '',
    studentEmail: '',
    courseName: '',
    courseType: '',
    completionDate: '',
    issueDate: ''
  };

  // Data lists
  completedEnrollments: Enrollment[] = [];
  filteredEnrollments: Enrollment[] = [];

  // Loading states
  isGenerating: boolean = false;
  isUploadingPDF: boolean = false;
  isLoadingEnrollments: boolean = false;

  // UI states
  pdfPreviewURL: string | null = null;
  generatedCertificateResponse: CertificateResponse | null = null;

  // Search
  enrollmentSearchQuery: string = '';

  // Current user (normally would come from auth service)
  currentUserId: number = 1; // TODO: Get from authentication service

  constructor(
    private enrollmentService: EnrollmentService,
    private certificateService: CertificateService
  ) {}

  ngOnInit() {
    this.loadCompletedEnrollments();
  }

  // === DATA LOADING ===

  loadCompletedEnrollments() {
    this.isLoadingEnrollments = true;
    this.enrollmentService.getCompletedEnrollments()
      .pipe(
        catchError(error => {
          console.error('Error loading enrollments:', error);
          this.emitMessage('error', 'Error cargando inscripciones completadas');
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.completedEnrollments = enrollments;
        this.filteredEnrollments = [...enrollments];
        this.isLoadingEnrollments = false;
      });
  }

  // === SEARCH ===

  searchEnrollments() {
    if (!this.enrollmentSearchQuery.trim()) {
      this.filteredEnrollments = [...this.completedEnrollments];
      return;
    }

    this.enrollmentService.searchCompletedEnrollments(this.enrollmentSearchQuery)
      .pipe(
        catchError(error => {
          console.error('Error searching enrollments:', error);
          this.emitMessage('error', 'Error en la búsqueda');
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.filteredEnrollments = enrollments;
      });
  }

  clearSearch() {
    this.enrollmentSearchQuery = '';
    this.filteredEnrollments = [...this.completedEnrollments];
  }

  // === PDF UPLOAD ===

  onPDFFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];

    if (!file) {
      this.certificateForm.pdfFile = null;
      this.pdfPreviewURL = null;
      return;
    }

    if (file.type !== 'application/pdf') {
      this.emitMessage('error', 'Solo se permiten archivos PDF');
      target.value = '';
      return;
    }

    if (file.size > 10 * 1024 * 1024) { // 10MB
      this.emitMessage('error', 'El archivo es demasiado grande (máximo 10MB)');
      target.value = '';
      return;
    }

    this.isUploadingPDF = true;
    this.certificateForm.pdfFile = file;

    // Create preview URL
    this.pdfPreviewURL = URL.createObjectURL(file);

    // Upload PDF to server
    this.certificateService.uploadCertificatePDF(file)
      .pipe(
        catchError(error => {
          console.error('Error uploading PDF:', error);
          this.emitMessage('error', 'Error subiendo archivo PDF');
          this.isUploadingPDF = false;
          return of(null);
        })
      )
      .subscribe(response => {
        this.isUploadingPDF = false;
        if (response?.success) {
          this.emitMessage('success', 'PDF subido exitosamente');
        }
      });
  }

  // === CERTIFICATE GENERATION ===

  generateCertificate() {
    if (!this.certificateForm.selectedEnrollmentId) {
      this.emitMessage('error', 'Selecciona una inscripción completada');
      return;
    }

    if (!this.certificateForm.pdfFile) {
      this.emitMessage('error', 'Sube un archivo PDF del certificado');
      return;
    }

    this.isGenerating = true;

    const request: GenerateCertificateRequest = {
      enrollmentId: this.certificateForm.selectedEnrollmentId,
      certificateFilePath: '/uploads/' + this.certificateForm.pdfFile.name,
      issuedByUserId: this.currentUserId
    };

    this.certificateService.generateCertificate(request)
      .pipe(
        catchError(error => {
          console.error('Error generating certificate:', error);
          this.emitMessage('error', 'Error generando certificado');
          this.isGenerating = false;
          return of(null);
        })
      )
      .subscribe(response => {
        this.isGenerating = false;
        if (response?.success) {
          this.generatedCertificateResponse = response;
          this.emitMessage('success', 'Certificado generado exitosamente');
          this.resetCertificateForm();
        } else {
          this.emitMessage('error', response?.message || 'Error generando certificado');
        }
      });
  }

  downloadCertificateQR(certificate: Certificate) {
    if (certificate.qrCode?.qrDataURL) {
      this.certificateService.downloadQRCode(
        certificate.qrCode.qrDataURL, 
        certificate.certificateNumber
      );
    } else {
      this.emitMessage('error', 'QR no disponible para descarga');
    }
  }

  // === UI HELPERS ===

  resetCertificateForm() {
    this.certificateForm = {
      selectedEnrollmentId: null,
      pdfFile: null,
      studentName: '',
      studentDNI: '',
      studentEmail: '',
      courseName: '',
      courseType: '',
      completionDate: '',
      issueDate: ''
    };
    this.pdfPreviewURL = null;
    this.generatedCertificateResponse = null;
  }

  selectEnrollment(enrollment: Enrollment) {
    this.certificateForm.selectedEnrollmentId = enrollment.id;
    
    // Auto-fill form data (using placeholder data for now)
    this.certificateForm.studentName = this.getStudentName(enrollment);
    this.certificateForm.courseName = this.getProgramName(enrollment);
    this.certificateForm.completionDate = enrollment.completionDate?.toString() || '';
    this.certificateForm.issueDate = new Date().toISOString().split('T')[0];
  }

  getStudentName(enrollment: Enrollment): string {
    // TODO: Implement proper student name retrieval
    return `Estudiante #${enrollment.userId}`;
  }

  getProgramName(enrollment: Enrollment): string {
    // TODO: Implement proper program name retrieval
    return `Programa #${enrollment.programId}`;
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'completed': return 'badge bg-success';
      case 'in_progress': return 'badge bg-primary';
      case 'enrolled': return 'badge bg-info';
      default: return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'completed': return 'Completado';
      case 'in_progress': return 'En Progreso';
      case 'enrolled': return 'Inscrito';
      default: return status;
    }
  }

  formatDate(date: string | Date | null): string {
    if (!date) return 'N/A';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('es-ES');
  }

  // === UTILITY ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === TRACKING ===

  trackByEnrollmentId(index: number, item: Enrollment): number {
    return item.id;
  }
} 