import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService, Student } from '../../services/student.service';
import { CertificateService, Certificate } from '../../services/certificate.service';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { ProgramService, Program } from '../../services/program.service';
import { catchError, switchMap } from 'rxjs/operators';
import { of, forkJoin } from 'rxjs';

@Component({
  selector: 'app-certificates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './certificates.component.html',
  styleUrl: './certificates.component.css'
})
export class CertificatesComponent implements OnInit {
  @Output() message = new EventEmitter<{ type: 'success' | 'error', message: string }>();

  // Data lists
  activeStudents: Student[] = [];
  filteredStudents: Student[] = [];
  certificates: { [key: number]: Certificate[] } = {}; // student ID -> certificates
  
  // Search and filters
  searchQuery: string = '';
  
  // UI states
  isLoading: boolean = false;
  selectedStudent: Student | null = null;
  
  // Certificate upload modal
  showCertificateUploadModal: boolean = false;
  selectedEnrollmentForCertificate: any = null;
  certificateFile: File | null = null;
  isUploadingCertificate: boolean = false;
  modalMessage: {type: 'success' | 'error', text: string} | null = null;
  
  // Student courses (for certificate management)
  studentCourses: any[] = [];
  isLoadingStudentCourses: boolean = false;

  constructor(
    private studentService: StudentService,
    private certificateService: CertificateService,
    private enrollmentService: EnrollmentService,
    private programService: ProgramService
  ) {}

  ngOnInit() {
    this.loadActiveStudents();
  }

  // === DATA LOADING ===

  /**
   * Load only active students
   */
  loadActiveStudents() {
    this.isLoading = true;
    
    this.studentService.getAllStudents()
      .pipe(
        catchError(error => {
          console.error('Error loading students:', error);
          this.emitMessage('error', 'Error cargando estudiantes');
          this.isLoading = false;
          return of([]);
        })
      )
      .subscribe(students => {
        // Filter only active students
        this.activeStudents = students.filter(student => student.active);
        this.filteredStudents = [...this.activeStudents];
        
        // Load certificates for each student
        this.loadCertificatesForStudents();
        
        this.isLoading = false;
      });
  }

  /**
   * Load certificates for all students
   */
  loadCertificatesForStudents() {
    this.activeStudents.forEach(student => {
      this.certificateService.getCertificatesByStudent(student.id)
        .pipe(
          catchError(error => {
            console.log(`No certificates found for student ${student.id}:`, error);
            return of([]);
          })
        )
        .subscribe(certificates => {
          this.certificates[student.id] = certificates;
        });
    });
  }

  // === SEARCH AND FILTERING ===

  /**
   * Search students by name, email, or document
   */
  searchStudents() {
    if (!this.searchQuery.trim()) {
      this.filteredStudents = [...this.activeStudents];
      return;
    }

    const query = this.searchQuery.toLowerCase().trim();
    this.filteredStudents = this.activeStudents.filter(student => {
      const fullName = `${student.person?.firstName || ''} ${student.person?.lastName || ''}`.toLowerCase();
      const email = (student.person?.email || '').toLowerCase();
      const document = (student.person?.documentNumber || '').toLowerCase();
      
      return fullName.includes(query) || 
             email.includes(query) || 
             document.includes(query);
    });
  }

  // === STUDENT COURSES ===

  /**
   * View completed courses for a student (for certificate management)
   */
  viewStudentCompletedCourses(student: Student) {
    this.selectedStudent = student;
    this.isLoadingStudentCourses = true;
    this.studentCourses = [];
    this.modalMessage = null;
    
    console.log('Loading completed courses for student:', student.id);
    this.showModal('studentCoursesModal');
    
    // Load only completed enrollments
    this.enrollmentService.getEnrollmentsByUser(student.id)
      .pipe(
        switchMap(enrollments => {
          console.log('Loaded enrollments for student:', enrollments);
          
          // Filter only completed enrollments
          const completedEnrollments = enrollments.filter(e => e.status === 'completed');
          
          if (completedEnrollments.length === 0) {
            return of([]);
          }
          
          // Get unique program IDs
          const programIds = [...new Set(completedEnrollments.map(e => e.programId))];
          console.log('Loading program details for completed courses:', programIds);
          
          // Create requests for each program
          const programRequests = programIds.map(id => 
            this.programService.getProgramById(id).pipe(
              catchError(error => {
                console.error(`Error loading program ${id}:`, error);
                return of(null);
              })
            )
          );
          
          // Execute all program requests in parallel
          return forkJoin(programRequests).pipe(
            switchMap(programs => {
              // Filter out null results and create program map
              const programMap = new Map();
              programs.filter(p => p !== null).forEach(program => {
                programMap.set(program.id, program);
              });
              
              console.log('Loaded programs:', programMap);
              
              // Enrich enrollments with program data
              const enrichedEnrollments = completedEnrollments.map(enrollment => ({
                ...enrollment,
                program: programMap.get(enrollment.programId) || {
                  id: enrollment.programId,
                  title: `Programa ${enrollment.programId}`,
                  description: 'Información no disponible',
                  duration: 'N/A',
                  credits: 0
                }
              }));
              
              return of(enrichedEnrollments);
            })
          );
        }),
        catchError(error => {
          console.error('Error loading student completed courses:', error);
          this.showModalMessage('error', 'Error cargando cursos completados del estudiante');
          this.isLoadingStudentCourses = false;
          return of([]);
        })
      )
      .subscribe(enrichedCourses => {
        console.log('Final enriched completed courses:', enrichedCourses);
        this.studentCourses = enrichedCourses;
        this.isLoadingStudentCourses = false;
      });
  }

  // === CERTIFICATE MANAGEMENT ===

  /**
   * Show certificate upload form for a course
   */
  showCertificateUpload(course: any) {
    this.selectedEnrollmentForCertificate = course;
    this.showCertificateUploadModal = true;
    this.certificateFile = null;
    
    // Show the modal after a brief delay to ensure DOM is updated
    setTimeout(() => {
      this.showModal('certificateUploadModal');
    }, 100);
  }

  /**
   * Handle certificate file selection
   */
  onCertificateFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
      if (!allowedTypes.includes(file.type)) {
        this.showModalMessage('error', 'Solo se permiten archivos de imagen (JPG, PNG) o PDF');
        return;
      }

      // Validate file size (max 10MB)
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        this.showModalMessage('error', 'El archivo no puede ser mayor a 10MB');
        return;
      }

      this.certificateFile = file;
    }
  }

  /**
   * Upload certificate for the selected enrollment
   */
  uploadCertificate() {
    if (!this.selectedEnrollmentForCertificate || !this.certificateFile) {
      this.showModalMessage('error', 'Seleccione un archivo de certificado');
      return;
    }

    this.isUploadingCertificate = true;

    this.certificateService.uploadCertificate(
      this.selectedEnrollmentForCertificate.id,
      this.certificateFile
    )
    .pipe(
      catchError(error => {
        console.error('Error uploading certificate:', error);
        this.showModalMessage('error', error.error || 'Error subiendo certificado');
        this.isUploadingCertificate = false;
        return of(null);
      })
    )
    .subscribe(certificate => {
      if (certificate) {
        console.log('Certificate uploaded successfully:', certificate);
        
        // Add certificate to the student's certificates
        const studentId = this.selectedStudent?.id;
        if (studentId) {
          if (!this.certificates[studentId]) {
            this.certificates[studentId] = [];
          }
          this.certificates[studentId].push(certificate);
        }
        
        // Show success message
        this.showModalMessage('success', 'Certificado subido exitosamente. QR generado automáticamente.');
        
        // Reset form
        this.cancelCertificateUpload();
        
        // Reload student certificates
        if (studentId) {
          this.loadCertificatesForStudent(studentId);
        }
      }
      this.isUploadingCertificate = false;
    });
  }

  /**
   * Cancel certificate upload
   */
  cancelCertificateUpload() {
    this.closeModal('certificateUploadModal');
    this.showCertificateUploadModal = false;
    this.selectedEnrollmentForCertificate = null;
    this.certificateFile = null;
    
    // Clear file input
    const fileInput = document.getElementById('certificateFileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  /**
   * Load certificates for a specific student
   */
  loadCertificatesForStudent(studentId: number) {
    this.certificateService.getCertificatesByStudent(studentId)
      .pipe(
        catchError(error => {
          console.log(`No certificates found for student ${studentId}:`, error);
          return of([]);
        })
      )
      .subscribe(certificates => {
        this.certificates[studentId] = certificates;
      });
  }

  /**
   * Check if a course has a certificate
   */
  hasCertificate(course: any): boolean {
    const studentId = this.selectedStudent?.id;
    if (!studentId) return false;
    
    const studentCertificates = this.certificates[studentId] || [];
    return studentCertificates.some(cert => cert.enrollment.id === course.id);
  }

  /**
   * Get certificate for a course
   */
  getCertificate(course: any): Certificate | null {
    const studentId = this.selectedStudent?.id;
    if (!studentId) return null;
    
    const studentCertificates = this.certificates[studentId] || [];
    return studentCertificates.find(cert => cert.enrollment.id === course.id) || null;
  }

  /**
   * Download certificate file
   */
  downloadCertificate(course: any) {
    const certificate = this.getCertificate(course);
    if (!certificate) {
      this.showModalMessage('error', 'Certificado no encontrado');
      return;
    }

    this.certificateService.downloadCertificate(certificate.id)
      .pipe(
        catchError(error => {
          console.error('Error downloading certificate:', error);
          this.showModalMessage('error', 'Error descargando certificado');
          return of(null);
        })
      )
      .subscribe(blob => {
        if (blob) {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = certificate.fileName;
          link.click();
          window.URL.revokeObjectURL(url);
        }
      });
  }

  /**
   * Download QR code
   */
  downloadQRCode(course: any) {
    const certificate = this.getCertificate(course);
    if (!certificate) {
      this.showModalMessage('error', 'Certificado no encontrado');
      return;
    }

    this.certificateService.downloadQRCode(certificate.id)
      .pipe(
        catchError(error => {
          console.error('Error downloading QR code:', error);
          this.showModalMessage('error', 'Error descargando código QR');
          return of(null);
        })
      )
      .subscribe(blob => {
        if (blob) {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `${certificate.certificateCode}_QR.png`;
          link.click();
          window.URL.revokeObjectURL(url);
        }
      });
  }

  /**
   * View certificates issued for a student
   */
  viewStudentCertificates(student: Student) {
    this.selectedStudent = student;
    this.showModal('viewCertificatesModal');
  }

  // === UTILITY METHODS ===

  /**
   * Get student display name
   */
  getStudentDisplayName(student: Student): string {
    if (student.person?.firstName && student.person?.lastName) {
      return `${student.person.firstName} ${student.person.lastName}`;
    }
    return `Usuario ${student.id}`;
  }

  /**
   * Get student document number
   */
  getStudentDocument(student: Student): string {
    return student.person?.documentNumber || 'Sin documento';
  }

  /**
   * Get student email
   */
  getStudentEmail(student: Student): string {
    return student.person?.email || 'Sin email';
  }

  /**
   * Get certificates count for a student
   */
  getCertificatesCount(student: Student): number {
    return this.certificates[student.id]?.length || 0;
  }

  /**
   * Check if student has certificates (safe method for template)
   */
  hasStudentCertificates(student: Student | null): boolean {
    return !!(student?.id && this.certificates[student.id]?.length > 0);
  }

  /**
   * Get student certificates (safe method for template)
   */
  getStudentCertificates(student: Student | null): Certificate[] {
    return student?.id ? (this.certificates[student.id] || []) : [];
  }

  /**
   * Get completed courses count for display
   */
  getCompletedCoursesCount(student: Student): number {
    // This could be optimized by storing completion counts
    return this.getCertificatesCount(student); // Simplified for now
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('es-ES');
    } catch (error) {
      return 'Fecha inválida';
    }
  }

  /**
   * Get course status badge class
   */
  getCourseStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'bg-success';
      default:
        return 'bg-secondary';
    }
  }

  /**
   * Get course status text
   */
  getCourseStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      default:
        return 'Estado desconocido';
    }
  }

  // === MODAL MANAGEMENT ===

  /**
   * Show modal with pure CSS manipulation
   */
  showModal(modalId: string) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error('Modal element not found:', modalId);
      return;
    }
    try {
      modalElement.classList.add('show');
      modalElement.style.display = 'block';
      modalElement.setAttribute('aria-modal', 'true');
      modalElement.setAttribute('aria-hidden', 'false');
      if (!document.querySelector('.modal-backdrop')) {
        const backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';
        document.body.appendChild(backdrop);
      }
      document.body.classList.add('modal-open');
    } catch (error) {
      console.error('Error showing modal:', error);
    }
  }

  /**
   * Close modal with pure CSS manipulation
   */
  closeModal(modalId: string) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;
    try {
      modalElement.classList.remove('show');
      modalElement.style.display = 'none';
      modalElement.setAttribute('aria-modal', 'false');
      modalElement.setAttribute('aria-hidden', 'true');
      const backdrop = document.querySelector('.modal-backdrop');
      if (backdrop) {
        backdrop.remove();
      }
      document.body.classList.remove('modal-open');
      document.body.style.overflow = '';
    } catch (error) {
      console.error('Error closing modal:', error);
    }
  }

  /**
   * Show modal message
   */
  showModalMessage(type: 'success' | 'error', text: string) {
    this.modalMessage = { type, text };
    setTimeout(() => {
      this.modalMessage = null;
    }, 4000);
  }

  /**
   * Emit message to parent component
   */
  emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  /**
   * Track by function for student list
   */
  trackByStudentId(index: number, student: Student): number {
    return student.id;
  }

  /**
   * Track by function for course list
   */
  trackByCourseId(index: number, course: any): number {
    return course.id;
  }
}