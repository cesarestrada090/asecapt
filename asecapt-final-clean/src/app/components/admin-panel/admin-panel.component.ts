import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { CertificateService, Certificate, CertificateResponse, GenerateCertificateRequest } from '../../services/certificate.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent implements OnInit {
  // Navigation
  activeSection: string = 'qr';
  sidebarOpen: boolean = false;

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
  generatedCertificates: Certificate[] = [];
  filteredCertificates: Certificate[] = [];
  
  // Mock data for students and courses (for backward compatibility with template)
  mockStudents: any[] = [];
  mockCourses: any[] = [];
  filteredStudents: any[] = [];
  filteredCourses: any[] = [];

  // Loading states
  isGenerating: boolean = false;
  isUploadingPDF: boolean = false;
  isLoadingEnrollments: boolean = false;
  isLoadingCertificates: boolean = false;

  // UI states
  pdfPreviewURL: string | null = null;
  generatedCertificateResponse: CertificateResponse | null = null;
  errorMessage: string = '';
  successMessage: string = '';

  // Search forms
  searchForm = {
    enrollmentQuery: '',
    certificateQuery: '',
    certificateStatus: '',
    qrQuery: '',
    qrType: '',
    studentQuery: '',
    studentCourse: '',
    courseQuery: '',
    courseStatus: ''
  };

  // Current user (normally would come from auth service)
  currentUserId: number = 1; // TODO: Get from authentication service

  constructor(
    private router: Router,
    private enrollmentService: EnrollmentService,
    private certificateService: CertificateService
  ) {}

  ngOnInit() {
    this.loadCompletedEnrollments();
    this.loadCertificates();
  }

  // === DATA LOADING ===

  loadCompletedEnrollments() {
    this.isLoadingEnrollments = true;
    this.enrollmentService.getCompletedEnrollments()
      .pipe(
        catchError(error => {
          console.error('Error loading enrollments:', error);
          this.errorMessage = 'Error cargando inscripciones completadas';
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.completedEnrollments = enrollments;
        this.filteredEnrollments = [...enrollments];
        this.isLoadingEnrollments = false;
      });
  }

  loadCertificates() {
    this.isLoadingCertificates = true;
    this.certificateService.getAllCertificates()
      .pipe(
        catchError(error => {
          console.error('Error loading certificates:', error);
          this.errorMessage = 'Error cargando certificados';
          return of([]);
        })
      )
      .subscribe(certificates => {
        this.generatedCertificates = certificates;
        this.filteredCertificates = [...certificates];
        this.isLoadingCertificates = false;
      });
  }

  // === CERTIFICATE GENERATION ===

  onPDFFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (!this.certificateService.isValidPDFFile(file)) {
        this.errorMessage = 'Solo se permiten archivos PDF';
        return;
      }

      this.certificateForm.pdfFile = file;
      this.isUploadingPDF = true;
      this.errorMessage = '';

      // Upload PDF to server
      this.certificateService.uploadCertificatePDF(file)
        .pipe(
          catchError(error => {
            console.error('Error uploading PDF:', error);
            this.errorMessage = 'Error subiendo archivo PDF';
            this.isUploadingPDF = false;
            return of(null);
          })
        )
        .subscribe(response => {
          if (response && response.success) {
            // Create preview URL for the PDF
            const reader = new FileReader();
            reader.onload = (e) => {
              this.pdfPreviewURL = e.target?.result as string;
              this.isUploadingPDF = false;
              this.successMessage = 'PDF subido exitosamente';
            };
            reader.readAsDataURL(file);
          } else {
            this.errorMessage = response?.error || 'Error subiendo PDF';
            this.isUploadingPDF = false;
          }
        });
    }
  }

  generateCertificate() {
    if (!this.isValidCertificateForm()) {
      this.errorMessage = 'Por favor complete todos los campos requeridos';
      return;
    }

    this.isGenerating = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: GenerateCertificateRequest = {
      enrollmentId: this.certificateForm.selectedEnrollmentId!,
      certificateFilePath: this.pdfPreviewURL || '', // In real implementation, use the file path from upload response
      issuedByUserId: this.currentUserId
    };

    this.certificateService.generateCertificate(request)
      .pipe(
        catchError(error => {
          console.error('Error generating certificate:', error);
          this.errorMessage = error.error?.message || 'Error generando certificado';
          this.isGenerating = false;
          return of(null);
        })
      )
      .subscribe(response => {
        this.isGenerating = false;
        if (response && response.success) {
          this.generatedCertificateResponse = response;
          this.successMessage = 'Certificado generado exitosamente';
          this.resetCertificateForm();
          this.loadCertificates(); // Refresh certificates list
        } else {
          this.errorMessage = response?.message || 'Error generando certificado';
        }
      });
  }

  downloadCertificateQR(certificate: Certificate) {
    this.certificateService.getQRCode(certificate.id)
      .pipe(
        catchError(error => {
          console.error('Error getting QR code:', error);
          this.errorMessage = 'Error obteniendo código QR';
          return of(null);
        })
      )
      .subscribe(response => {
        if (response) {
          this.certificateService.downloadQRCode(response.qrCodeDataURL, certificate.certificateNumber);
        }
      });
  }

  // === SEARCH FUNCTIONALITY ===

  searchEnrollments() {
    const query = this.searchForm.enrollmentQuery.toLowerCase().trim();
    
    if (!query) {
      this.filteredEnrollments = [...this.completedEnrollments];
      return;
    }

    this.enrollmentService.searchCompletedEnrollments(query)
      .pipe(
        catchError(error => {
          console.error('Error searching enrollments:', error);
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.filteredEnrollments = enrollments;
      });
  }

  clearEnrollmentSearch() {
    this.searchForm.enrollmentQuery = '';
    this.filteredEnrollments = [...this.completedEnrollments];
  }

  searchCertificates() {
    const query = this.searchForm.certificateQuery.toLowerCase().trim();
    const status = this.searchForm.certificateStatus;

    this.certificateService.searchCertificates(query || undefined, status || undefined)
      .pipe(
        catchError(error => {
          console.error('Error searching certificates:', error);
          return of([]);
        })
      )
      .subscribe(certificates => {
        this.filteredCertificates = certificates;
      });
  }

  clearCertificateSearch() {
    this.searchForm.certificateQuery = '';
    this.searchForm.certificateStatus = '';
    this.filteredCertificates = [...this.generatedCertificates];
  }

  // === VALIDATION ===

  isValidCertificateForm(): boolean {
    return !!(
      this.certificateForm.selectedEnrollmentId &&
      this.pdfPreviewURL
    );
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
  }

  getStudentName(enrollment: Enrollment): string {
    // TODO: Implement proper student name retrieval from separate API calls
    return `Estudiante ID: ${enrollment.userId}`;
  }

  getProgramName(enrollment: Enrollment): string {
    // TODO: Implement proper program name retrieval from separate API calls
    return `Programa ID: ${enrollment.programId}`;
  }

  getStatusBadgeClass(status: string): string {
    return this.certificateService.getStatusBadgeClass(status);
  }

  getStatusText(status: string): string {
    return this.certificateService.getStatusText(status);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  // === NAVIGATION ===

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  setActiveSection(section: string) {
    this.activeSection = section;
    this.sidebarOpen = false;
    this.clearMessages();
  }

  getSectionTitle(): string {
    switch (this.activeSection) {
      case 'qr': return 'Generador de Certificados';
      case 'qr-search': return 'Buscar Certificados';
      case 'students': return 'Alumnos Completados';
      case 'courses': return 'Gestión de Cursos';
      default: return 'Dashboard';
    }
  }

  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'qr': return 'Genera certificados con QR de verificación';
      case 'qr-search': return 'Busca certificados por estudiante, curso o número';
      case 'students': return 'Estudiantes que han completado programas';
      case 'courses': return 'Gestiona cursos y programas';
      default: return 'Panel de administración ASECAPT';
    }
  }

  logout() {
    this.router.navigate(['/virtual-classroom']);
  }

  // === MISSING METHODS FOR TEMPLATE COMPATIBILITY ===

  trackByQRId(index: number, certificate: Certificate): number {
    return certificate.id;
  }

  getCourseTypeText(courseType: string | undefined): string {
    switch (courseType) {
      case 'course': return 'Curso';
      case 'specialization': return 'Especialización';
      case 'certification': return 'Certificación';
      default: return 'Programa';
    }
  }

  deleteCertificate(certificateId: number) {
    // TODO: Implement certificate deletion
    console.log('Delete certificate:', certificateId);
  }

  searchStudents() {
    // TODO: Implement student search
    console.log('Search students:', this.searchForm.studentQuery);
  }

  clearStudentSearch() {
    this.searchForm.studentQuery = '';
    this.searchForm.studentCourse = '';
    this.searchStudents();
  }

  searchCourses() {
    // TODO: Implement course search
    console.log('Search courses:', this.searchForm.courseQuery);
  }

  clearCourseSearch() {
    this.searchForm.courseQuery = '';
    this.searchForm.courseStatus = '';
    this.searchCourses();
  }

  // === TRACKING ===

  trackByEnrollmentId(index: number, item: Enrollment): number {
    return item.id;
  }

  trackByCertificateId(index: number, item: Certificate): number {
    return item.id;
  }
} 