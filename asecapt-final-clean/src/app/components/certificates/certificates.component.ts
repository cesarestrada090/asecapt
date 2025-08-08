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
  certificates: { [key: number]: Certificate[] } = {}; // student ID -> certificates array
  completedCoursesCount: { [key: number]: number } = {}; // student ID -> completed courses count

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

  // Date editing functionality
  editingCourse: { [key: number]: boolean } = {};
  courseEditForm: { [key: number]: { enrollmentDate: string, issueDate: string } } = {};
  isUpdatingCourse: { [key: number]: boolean } = {};

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
        this.activeStudents = (students || []).filter(student => student && student.active);
        this.filteredStudents = [...this.activeStudents];

        // Load certificates and completed courses for each student
        this.loadCertificatesForStudents();
        this.loadCompletedCoursesCount();

        this.isLoading = false;
      });
  }

  /**
   * Load certificates for all students
   */
  loadCertificatesForStudents() {
    this.activeStudents.forEach(student => {
      if (student && student.id) {
        this.certificateService.getCertificatesByStudent(student.id)
          .pipe(
            catchError(error => {
              console.log(`No certificates found for student ${student.id}:`, error);
              return of([]);
            })
          )
          .subscribe(certificates => {
            if (certificates && Array.isArray(certificates)) {
              this.certificates[student.id] = certificates;
            } else {
              this.certificates[student.id] = [];
            }
          });
      }
    });
  }

  /**
   * Load completed courses count for all students
   */
  loadCompletedCoursesCount() {
    this.activeStudents.forEach(student => {
      if (student && student.id) {
        this.enrollmentService.getEnrollmentsByUser(student.id)
          .pipe(
            catchError(error => {
              console.log(`No enrollments found for student ${student.id}:`, error);
              return of([]);
            })
          )
          .subscribe(enrollments => {
            if (enrollments && Array.isArray(enrollments)) {
              const completedCount = enrollments.filter(e => e && e.status === 'completed').length;
              this.completedCoursesCount[student.id] = completedCount;
            } else {
              this.completedCoursesCount[student.id] = 0;
            }
          });
      }
    });
  }

  // === SEARCH AND FILTERING ===

  /**
   * Search students by name, email, or document
   */
  searchStudents() {
    if (!this.searchQuery?.trim()) {
      this.filteredStudents = [...this.activeStudents];
      return;
    }

    const query = this.searchQuery.toLowerCase().trim();
    this.filteredStudents = this.activeStudents.filter(student => {
      if (!student || !student.person) return false;

      const fullName = `${student.person.firstName || ''} ${student.person.lastName || ''}`.toLowerCase();
      const email = (student.person.email || '').toLowerCase();
      const document = (student.person.documentNumber || '').toLowerCase();

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
    if (!student || !student.id) {
      this.emitMessage('error', 'Estudiante no válido');
      return;
    }

    this.selectedStudent = student;
    this.isLoadingStudentCourses = true;
    this.studentCourses = [];
    this.modalMessage = null;

    console.log('Loading completed courses for student:', student.id);

    // Ensure DOM is ready, then show modal
    setTimeout(() => {
      this.showModal('studentCoursesModal');
    }, 150);

    // Load only completed enrollments
    this.enrollmentService.getEnrollmentsByUser(student.id)
      .pipe(
        switchMap(enrollments => {
          console.log('Loaded enrollments for student:', enrollments);
          if (!enrollments || !Array.isArray(enrollments)) {
            console.error('Invalid enrollments data:', enrollments);
            return of([]);
          }

          // Filter only completed enrollments
          const completedEnrollments = (enrollments || []).filter(e => e && e.status === 'completed');

          if (!completedEnrollments || completedEnrollments.length === 0) {
            return of([]);
          }

          // Get unique program IDs
          const programIds = [...new Set(completedEnrollments.map(e => e && e.programId).filter(id => id))];
          console.log('Loading program details for completed courses:', programIds);

          if (!programIds || programIds.length === 0) {
            console.log('No program IDs found');
            return of([]);
          }

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
          return forkJoin(programRequests || []).pipe(
            switchMap(programs => {
              if (!programs || !Array.isArray(programs)) {
                console.error('Invalid programs data:', programs);
                return of([]);
              }
              // Filter out null results and create program map
              const programMap = new Map();
              (programs || []).filter(p => p !== null).forEach(program => {
                if (program && program.id) {
                  programMap.set(program.id, program);
                }
              });

              console.log('Loaded programs:', programMap);

              // Enrich enrollments with program data
              const enrichedEnrollments = completedEnrollments.map(enrollment => {
                if (!enrollment) return null;
                return {
                  ...enrollment,
                  program: programMap.get(enrollment.programId) || {
                    id: enrollment.programId,
                    title: `Programa ${enrollment.programId}`,
                    description: 'Información no disponible',
                    duration: 'N/A',
                    credits: 0
                  }
                };
              }).filter(enrollment => enrollment !== null);

              return of(enrichedEnrollments || []);
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
        this.studentCourses = enrichedCourses || [];
        this.isLoadingStudentCourses = false;
      });
  }

  // === CERTIFICATE MANAGEMENT ===

  /**
   * Show certificate upload form for a course
   */
  showCertificateUpload(course: any) {
    if (!course || !course.id) {
      this.showModalMessage('error', 'Curso no válido');
      return;
    }

    this.selectedEnrollmentForCertificate = course;
    this.showCertificateUploadModal = true;
    this.certificateFile = null;

    // Show the modal after a brief delay to ensure DOM is updated
    setTimeout(() => {
      this.showModal('certificateUploadModal');
    }, 150);
  }

  /**
   * Handle certificate file selection
   */
  onCertificateFileSelected(event: any) {
    const file = event?.target?.files?.[0];
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
    if (!this.selectedEnrollmentForCertificate || !this.selectedEnrollmentForCertificate.id || !this.certificateFile) {
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
      } else {
        this.showModalMessage('error', 'Error al procesar el certificado');
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
    if (!studentId) {
      console.error('Invalid student ID:', studentId);
      return;
    }

    this.certificateService.getCertificatesByStudent(studentId)
      .pipe(
        catchError(error => {
          console.log(`No certificates found for student ${studentId}:`, error);
          return of([]);
        })
      )
      .subscribe(certificates => {
        if (certificates && Array.isArray(certificates)) {
          this.certificates[studentId] = certificates;
        } else {
          this.certificates[studentId] = [];
        }
      });
  }

  /**
   * Check if a course has a certificate
   */
  hasCertificate(course: any): boolean {
    const studentId = this.selectedStudent?.id;
    if (!studentId || !course || !course.id) return false;

    const studentCertificates = this.certificates[studentId] || [];
    return studentCertificates.some(cert => cert.enrollment && cert.enrollment.id === course.id);
  }

  /**
   * Get certificate for a course
   */
  getCertificate(course: any): Certificate | null {
    const studentId = this.selectedStudent?.id;
    if (!studentId || !course || !course.id) return null;

    const studentCertificates = this.certificates[studentId] || [];
    return studentCertificates.find(cert => cert.enrollment && cert.enrollment.id === course.id) || null;
  }

  /**
   * Download certificate file
   */
  downloadCertificate(course: any) {
    const certificate = this.getCertificate(course);
    if (!certificate || !certificate.id) {
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
          link.download = certificate.fileName || 'certificate.pdf';
          link.click();
          window.URL.revokeObjectURL(url);
        } else {
          this.showModalMessage('error', 'No se pudo descargar el archivo');
        }
      });
  }

  /**
   * Download QR code
   */
  downloadQRCode(course: any) {
    const certificate = this.getCertificate(course);
    if (!certificate || !certificate.id) {
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
          link.download = `${certificate.certificateCode || 'certificate'}_QR.png`;
          link.click();
          window.URL.revokeObjectURL(url);
        } else {
          this.showModalMessage('error', 'No se pudo descargar el código QR');
        }
      });
  }

  /**
   * View certificates issued for a student
   */
  viewStudentCertificates(student: Student) {
    if (!student || !student.id) {
      this.emitMessage('error', 'Estudiante no válido');
      return;
    }
    this.selectedStudent = student;

    // Load certificates if not already loaded
    if (!this.certificates[student.id]) {
      this.loadCertificatesForStudent(student.id);
    }

    // Ensure DOM is ready, then show modal
    setTimeout(() => {
      this.showModal('viewCertificatesModal');
    }, 150);
  }

  // === UTILITY METHODS ===

  /**
   * Get student display name
   */
  getStudentDisplayName(student: Student): string {
    if (!student) return 'Estudiante desconocido';
    if (student.person?.firstName && student.person?.lastName) {
      return `${student.person.firstName} ${student.person.lastName}`;
    }
    return `Usuario ${student.id}`;
  }

  /**
   * Get student document number
   */
  getStudentDocument(student: Student): string {
    if (!student) return 'Sin documento';
    return student.person?.documentNumber || 'Sin documento';
  }

  /**
   * Get student email
   */
  getStudentEmail(student: Student): string {
    if (!student) return 'Sin email';
    return student.person?.email || 'Sin email';
  }

  /**
   * Get certificates count for a student
   */
  getCertificatesCount(student: Student): number {
    if (!student || !student.id) return 0;
    const studentCertificates = this.certificates[student.id];
    return studentCertificates ? studentCertificates.length : 0;
  }

  /**
   * Check if student has certificates (safe method for template)
   */
  hasStudentCertificates(student: Student | null): boolean {
    if (!student || !student.id) return false;
    const studentCertificates = this.certificates[student.id];
    return !!(studentCertificates && studentCertificates.length > 0);
  }

  /**
   * Get student certificates (safe method for template)
   */
  getStudentCertificates(student: Student | null): Certificate[] {
    if (!student || !student.id) return [];
    return this.certificates[student.id] || [];
  }

  /**
   * Get completed courses count for display
   */
  getCompletedCoursesCount(student: Student): number {
    if (!student || !student.id) return 0;
    return this.completedCoursesCount[student.id] || 0;
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string | Date | null | undefined): string {
    if (!dateString) return 'N/A';
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

  /**
   * Format date for input field (YYYY-MM-DD format)
   */
  private formatDateForInput(dateString: string | null | undefined): string {
    if (!dateString) return '';

    try {
      // Si ya está en formato YYYY-MM-DD, devolverlo tal como está
      if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
        return dateString;
      }

      // Si tiene tiempo (YYYY-MM-DDTHH:mm:ss), extraer solo la fecha
      if (typeof dateString === 'string' && dateString.includes('T')) {
        return dateString.split('T')[0];
      }

      // Para otros formatos, convertir a Date y extraer componentes
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return '';
      }

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');

      return `${year}-${month}-${day}`;
    } catch (error) {
      console.error('Error formatting date for input:', error);
      return '';
    }
  }

  /**
   * Get course status badge class
   */
  getCourseStatusBadgeClass(status: string | null | undefined): string {
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
  getCourseStatusText(status: string | null | undefined): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      default:
        return 'Estado desconocido';
    }
  }

  // === MODAL MANAGEMENT ===

  /**
   * Show modal using Bootstrap Modal API or fallback to CSS manipulation
   */
  showModal(modalId: string) {
    if (!modalId) {
      console.error('Modal ID is required');
      return;
    }

    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error('Modal element not found:', modalId);
      return;
    }

    console.log('Showing modal:', modalId);

    try {
      // Try using Bootstrap Modal API first
      if ((window as any).bootstrap?.Modal) {
        console.log('Using Bootstrap Modal API for:', modalId);
        const modal = new (window as any).bootstrap.Modal(modalElement, {
          backdrop: 'static',  // Prevent closing when clicking backdrop
          keyboard: true,
          focus: true
        });
        modal.show();
      } else {
        console.log('Using CSS fallback for modal:', modalId);
        // Fallback to CSS manipulation
        modalElement.classList.add('show');
        modalElement.style.display = 'block';
        modalElement.style.zIndex = '1050'; // Ensure modal is above backdrop
        modalElement.setAttribute('aria-modal', 'true');
        modalElement.setAttribute('aria-hidden', 'false');

        // Remove any existing backdrop first
        const existingBackdrop = document.querySelector('.modal-backdrop');
        if (existingBackdrop) {
          existingBackdrop.remove();
        }

        // Create new backdrop with forced visibility
        const backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';
        backdrop.style.cssText = `
          position: fixed !important;
          top: 0 !important;
          left: 0 !important;
          width: 100vw !important;
          height: 100vh !important;
          z-index: 1040 !important;
          background-color: rgba(0, 0, 0, 0.7) !important;
          opacity: 1 !important;
        `;
        document.body.appendChild(backdrop);
        document.body.classList.add('modal-open');
      }
    } catch (error) {
      console.error('Error showing modal:', error);
    }
  }

  /**
   * Close modal using Bootstrap Modal API or fallback to CSS manipulation
   */
  closeModal(modalId: string) {
    if (!modalId) {
      console.error('Modal ID is required');
      return;
    }

    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;

    try {
      // Try using Bootstrap Modal API first
      if ((window as any).bootstrap?.Modal) {
        const modal = (window as any).bootstrap.Modal.getInstance(modalElement);
        if (modal) {
          modal.hide();
        }
      } else {
        // Fallback to CSS manipulation
        modalElement.classList.remove('show');
        modalElement.style.display = 'none';
        modalElement.setAttribute('aria-modal', 'false');
        modalElement.setAttribute('aria-hidden', 'true');

        // Remove all backdrops
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());

        document.body.classList.remove('modal-open');
        document.body.style.overflow = '';
        document.body.style.paddingRight = ''; // Remove any padding added by Bootstrap
      }
    } catch (error) {
      console.error('Error closing modal:', error);
    }
  }

  /**
   * Show modal message
   */
  showModalMessage(type: 'success' | 'error', text: string) {
    if (!text) return;

    this.modalMessage = { type, text };
    setTimeout(() => {
      this.modalMessage = null;
    }, 4000);
  }

  /**
   * Emit message to parent component
   */
  emitMessage(type: 'success' | 'error', message: string) {
    if (!message) return;
    this.message.emit({ type, message });
  }

  /**
   * Track by function for student list
   */
  trackByStudentId(index: number, student: Student): number {
    return student?.id || index;
  }

  /**
   * Track by function for course list
   */
  trackByCourseId(index: number, course: any): number {
    return course?.id || index;
  }

  /**
   * Check if a certificate can be generated for a course
   */
  canGenerateCertificate(course: any): boolean {
    // Must be completed status
    if (course.status !== 'completed') {
      return false;
    }

    // Must have a valid final grade (not null, undefined, or 0)
    if (course.finalGrade === null || course.finalGrade === undefined || course.finalGrade <= 0) {
      return false;
    }

    // Must have a valid attendance percentage (not null, undefined, or 0)
    if (course.attendancePercentage === null || course.attendancePercentage === undefined || course.attendancePercentage <= 0) {
      return false;
    }

    // Must meet minimum requirements
    if (course.finalGrade < 60) {
      return false;
    }

    if (course.attendancePercentage < 80) {
      return false;
    }

    return true;
  }

  /**
   * Get detailed validation status for certificate generation
   */
  getCertificateValidationDetails(course: any): { missingGrade: boolean, missingAttendance: boolean, lowGrade: boolean, lowAttendance: boolean, notCompleted: boolean } {
    return {
      notCompleted: course.status !== 'completed',
      missingGrade: course.finalGrade === null || course.finalGrade === undefined || course.finalGrade <= 0,
      missingAttendance: course.attendancePercentage === null || course.attendancePercentage === undefined || course.attendancePercentage <= 0,
      lowGrade: course.finalGrade !== null && course.finalGrade !== undefined && course.finalGrade > 0 && course.finalGrade < 60,
      lowAttendance: course.attendancePercentage !== null && course.attendancePercentage !== undefined && course.attendancePercentage > 0 && course.attendancePercentage < 80
    };
  }

  /**
   * Get validation message for why a certificate cannot be generated
   */
  getCertificateValidationMessage(course: any): string {
    if (course.status !== 'completed') {
      return 'Curso no completado';
    }

    // Check for missing grade
    if (course.finalGrade === null || course.finalGrade === undefined || course.finalGrade <= 0) {
      return 'Falta asignar nota final';
    }

    // Check for missing attendance
    if (course.attendancePercentage === null || course.attendancePercentage === undefined || course.attendancePercentage <= 0) {
      return 'Falta asignar asistencia';
    }

    // Check for both missing
    if ((course.finalGrade === null || course.finalGrade === undefined || course.finalGrade <= 0) &&
        (course.attendancePercentage === null || course.attendancePercentage === undefined || course.attendancePercentage <= 0)) {
      return 'Falta nota y asistencia';
    }

    // Check minimum grade requirement
    if (course.finalGrade < 60) {
      return `Nota insuficiente: ${course.finalGrade} (mín. 60)`;
    }

    // Check minimum attendance requirement
    if (course.attendancePercentage < 80) {
      return `Asistencia insuficiente: ${course.attendancePercentage}% (mín. 80%)`;
    }

    return 'Requisitos no cumplidos';
  }

  // === DATE EDITING METHODS ===

  /**
   * Start editing dates for a course
   */
  startEditingDates(course: any) {
    console.log('Starting to edit dates for course:', course);

    const certificate = this.getCertificate(course);

    // Initialize edit form with current values
    this.courseEditForm[course.id] = {
      enrollmentDate: course.enrollmentDate || '',
      issueDate: this.formatDateForInput(certificate?.issuedDate) || ''
    };

    // Mark as editing
    this.editingCourse[course.id] = true;

    console.log('Course edit form initialized:', this.courseEditForm[course.id]);
    console.log('Certificate found:', certificate);
    console.log('Raw issuedDate:', certificate?.issuedDate);
    console.log('Formatted issueDate:', this.formatDateForInput(certificate?.issuedDate));
  }

  /**
   * Cancel editing dates for a course
   */
  cancelEditingDates(courseId: number) {
    console.log('Canceling edit for course:', courseId);

    // Remove from editing state
    delete this.editingCourse[courseId];
    delete this.courseEditForm[courseId];
    delete this.isUpdatingCourse[courseId];
  }

  /**
   * Save date changes for a course
   */
  saveDateChanges(course: any) {
    const courseId = course.id;
    const formData = this.courseEditForm[courseId];

    if (!formData) {
      console.error('No form data found for course:', courseId);
      return;
    }

    console.log('Saving date changes:', { courseId, formData });

    // Validate data
    if (!formData.enrollmentDate) {
      this.showModalMessage('error', 'La fecha de matrícula es obligatoria');
      return;
    }

    // Mark as updating
    this.isUpdatingCourse[courseId] = true;

    // Prepare update request for enrollment
    const enrollmentUpdateRequest: any = {
      enrollmentDate: formData.enrollmentDate,
      issueDate: formData.issueDate
    };

    console.log('Sending enrollment update request:', enrollmentUpdateRequest);

    // Call the API to update enrollment
    this.enrollmentService.updateEnrollment(courseId, enrollmentUpdateRequest)
      .pipe(
        catchError(error => {
          console.error('Error updating enrollment:', error);
          this.showModalMessage('error', 'Error actualizando fecha de matrícula');
          this.isUpdatingCourse[courseId] = false;
          return of(null);
        })
      )
      .subscribe(updatedEnrollment => {
        if (updatedEnrollment) {
          console.log('Enrollment updated successfully:', updatedEnrollment);

          // Update the local data
          const courseIndex = this.studentCourses.findIndex(c => c.id === courseId);
          if (courseIndex !== -1) {
            this.studentCourses[courseIndex].enrollmentDate = formData.enrollmentDate;
          }

          // Update certificate issue date in local data if a certificate exists
          if (formData.issueDate) {
            const studentId = this.selectedStudent?.id;
            if (studentId && this.certificates[studentId]) {
              const certificate = this.certificates[studentId].find(cert =>
                cert.enrollment && cert.enrollment.id === courseId
              );
              if (certificate) {
                certificate.issuedDate = formData.issueDate;
                console.log('Updated local certificate issue date:', certificate.issuedDate);
              }
            }
          }

          // Show success message
          this.showModalMessage('success', 'Fecha de matrícula actualizada');

          // Reset editing state after successful save
          this.cancelEditingDates(courseId);

          // Refresh certificates for the student
          const studentId = this.selectedStudent?.id;
          if (studentId) {
            this.loadCertificatesForStudent(studentId);
          }
        } else {
          this.showModalMessage('error', 'Error al actualizar la matrícula');
        }
        this.isUpdatingCourse[courseId] = false;
      });
  }
}
