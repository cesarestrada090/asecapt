import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService, Student, CreateStudentRequest, UpdateStudentRequest, StudentStatistics } from '../../services/student.service';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './students.component.html',
  styleUrl: './students.component.css'
})
export class StudentsComponent implements OnInit {
  @Output() message = new EventEmitter<{ type: 'success' | 'error', message: string }>();

  // Data lists
  allStudents: Student[] = [];
  filteredStudents: Student[] = [];
  allEnrollments: Enrollment[] = [];
  statistics: StudentStatistics = { totalStudents: 0, activeStudents: 0, inactiveStudents: 0 };

  // Loading states
  isLoadingStudents: boolean = false;
  isLoadingEnrollments: boolean = false;
  isSearching: boolean = false;
  isSaving: boolean = false;

  // Search form
  searchForm = {
    query: '',
    status: ''
  };

  // Student form for create/edit
  studentForm: CreateStudentRequest = {
    firstName: '',
    lastName: '',
    documentNumber: '',
    documentType: 'DNI',
    email: '',
    phoneNumber: '',
    gender: '',
    birthDate: '',
    username: '',
    password: ''
  };

  // UI states
  showStudentForm: boolean = false;
  editingStudent: Student | null = null;
  selectedStudent: Student | null = null;
  studentCourses: any[] = [];
  isLoadingStudentCourses: boolean = false;
  modalMessage: {type: 'success' | 'error', text: string} | null = null;

  // Document types
  documentTypes = [
    { value: 'DNI', label: 'DNI' },
    { value: 'CE', label: 'Carnet de Extranjería' },
    { value: 'PASSPORT', label: 'Pasaporte' }
  ];

  // Genders
  genders = [
    { value: 'M', label: 'Masculino' },
    { value: 'F', label: 'Femenino' },
    { value: 'O', label: 'Otro' }
  ];

  // Status options
  availableStatuses = [
    { value: '', label: 'Todos los Estados' },
    { value: 'true', label: 'Activo' },
    { value: 'false', label: 'Inactivo' }
  ];

  // Program options for filtering
  availablePrograms = [
    { value: '', label: 'Todos los Programas' }
  ];

  // Recent enrollments for display
  filteredEnrollments: Enrollment[] = [];

  constructor(
    private studentService: StudentService,
    private enrollmentService: EnrollmentService
  ) {}

  ngOnInit() {
    this.loadStudents();
    this.loadStatistics();
    this.loadEnrollments();
  }

  // === DATA LOADING ===

  loadStudents() {
    this.isLoadingStudents = true;
    this.studentService.getAllStudents()
      .pipe(
        catchError(error => {
          console.error('Error loading students:', error);
          this.emitMessage('error', 'Error cargando estudiantes');
          return of([]);
        })
      )
      .subscribe(students => {
        this.allStudents = students;
        this.filteredStudents = [...students];
        this.isLoadingStudents = false;
      });
  }

  loadStatistics() {
    this.studentService.getStudentStatistics()
      .pipe(
        catchError(error => {
          console.error('Error loading statistics:', error);
          return of({ totalStudents: 0, activeStudents: 0, inactiveStudents: 0 });
        })
      )
      .subscribe(stats => {
        this.statistics = stats;
      });
  }

  loadEnrollments() {
    this.isLoadingEnrollments = true;
    this.enrollmentService.getAllEnrollments()
      .pipe(
        catchError(error => {
          console.error('Error loading enrollments:', error);
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.allEnrollments = enrollments;
        this.filteredEnrollments = enrollments.slice().reverse(); // Show newest first
        this.isLoadingEnrollments = false;
      });
  }

  // === SEARCH AND FILTER ===

  searchStudents() {
    console.log('Searching students with:', this.searchForm);
    
    // Start with all students or search results
    if (!this.searchForm.query.trim()) {
      this.filteredStudents = [...this.allStudents];
    } else {
      this.isSearching = true;
      this.studentService.searchStudents(this.searchForm.query)
        .pipe(
          catchError(error => {
            console.error('Error searching students:', error);
            this.emitMessage('error', 'Error buscando estudiantes');
            this.isSearching = false;
            return of([]);
          })
        )
        .subscribe(students => {
          console.log(`Backend search returned ${students.length} students for query: "${this.searchForm.query}"`);
          this.filteredStudents = students;
          this.isSearching = false;
          this.applyFilters();
        });
      return; // Exit early to avoid applying filters twice
    }

    // Apply all filters
    this.applyFilters();
  }

  private applyFilters() {
    let filtered = [...this.filteredStudents];
    console.log(`Starting with ${filtered.length} students before filters`);

    // Apply status filter
    if (this.searchForm.status) {
      const isActive = this.searchForm.status === 'true';
      const beforeFilter = filtered.length;
      filtered = filtered.filter(student => student.active === isActive);
      console.log(`Status filter (${isActive ? 'active' : 'inactive'}): ${beforeFilter} → ${filtered.length} students`);
    }



    this.filteredStudents = filtered;
    console.log(`Final filtered students: ${this.filteredStudents.length}`);
  }

  clearStudentSearch() {
    this.searchForm = {
      query: '',
      status: ''
    };
    this.filteredStudents = [...this.allStudents];
    console.log('Search cleared, showing all students:', this.filteredStudents.length);
  }

  // === STUDENT MANAGEMENT ===

  showCreateStudentForm() {
    this.editingStudent = null;
    this.resetStudentForm();
    this.showModal('studentModal');
  }

  editStudent(student: Student) {
    this.editingStudent = student;
    this.studentForm = {
      firstName: student.person.firstName,
      lastName: student.person.lastName,
      documentNumber: student.person.documentNumber,
      documentType: student.person.documentType,
      email: student.person.email,
      phoneNumber: student.person.phoneNumber,
      gender: student.person.gender || '',
      birthDate: student.person.birthDate || '',
      username: student.username,
      password: '' // Don't show password
    };
    this.showModal('studentModal');
  }

  saveStudent() {
    if (!this.isStudentFormValid()) {
      this.emitMessage('error', 'Por favor complete todos los campos obligatorios');
      return;
    }

    this.isSaving = true;

    if (this.editingStudent) {
      // Update existing student
      const updateRequest: UpdateStudentRequest = { ...this.studentForm };
      
      console.log('=== UPDATE STUDENT FRONTEND DEBUG ===');
      console.log('Editing student ID:', this.editingStudent.id);
      console.log('Update request:', updateRequest);
      console.log('API URL will be:', `/api/students/${this.editingStudent.id}`);
      
      this.studentService.updateStudent(this.editingStudent.id, updateRequest)
        .pipe(
          catchError(error => {
            console.error('Error updating student:', error);
            this.emitMessage('error', error.error?.error || 'Error actualizando estudiante');
            this.isSaving = false;
            return of(null);
          })
        )
        .subscribe(updatedStudent => {
          if (updatedStudent) {
            const index = this.allStudents.findIndex(s => s.id === updatedStudent.id);
            if (index !== -1) {
              this.allStudents[index] = updatedStudent;
              this.filteredStudents = [...this.allStudents];
            }
            this.emitMessage('success', 'Estudiante actualizado exitosamente');
            this.cancelStudentForm();
          }
          this.isSaving = false;
        });
    } else {
      // Create new student
      this.studentService.createStudent(this.studentForm)
        .pipe(
          catchError(error => {
            console.error('Error creating student:', error);
            this.emitMessage('error', error.error?.error || 'Error creando estudiante');
            this.isSaving = false;
            return of(null);
          })
        )
        .subscribe(newStudent => {
          if (newStudent) {
            this.allStudents.push(newStudent);
            this.filteredStudents = [...this.allStudents];
            this.loadStatistics(); // Refresh statistics
            this.emitMessage('success', 'Estudiante creado exitosamente');
            this.cancelStudentForm();
          }
          this.isSaving = false;
        });
    }
  }

  toggleStudentStatus(student: Student) {
    this.studentService.toggleStudentStatus(student.id)
      .pipe(
        catchError(error => {
          console.error('Error toggling student status:', error);
          this.emitMessage('error', 'Error cambiando estado del estudiante');
          return of(null);
        })
      )
      .subscribe(updatedStudent => {
        if (updatedStudent) {
          const index = this.allStudents.findIndex(s => s.id === updatedStudent.id);
          if (index !== -1) {
            this.allStudents[index] = updatedStudent;
            this.filteredStudents = [...this.allStudents];
          }
          this.loadStatistics(); // Refresh statistics
          const statusText = updatedStudent.active ? 'activado' : 'desactivado';
          this.emitMessage('success', `Estudiante ${statusText} exitosamente`);
        }
      });
  }

  cancelStudentForm() {
    this.closeModal('studentModal');
    this.editingStudent = null;
    this.resetStudentForm();
  }

  resetStudentForm() {
    this.studentForm = {
      firstName: '',
      lastName: '',
      documentNumber: '',
      documentType: 'DNI',
      email: '',
      phoneNumber: '',
      gender: '',
      birthDate: '',
      username: '',
      password: ''
    };
  }

  isStudentFormValid(): boolean {
    return !!(
      this.studentForm.firstName?.trim() &&
      this.studentForm.lastName?.trim() &&
      this.studentForm.documentNumber?.trim() &&
      this.studentForm.email?.trim() &&
      this.studentForm.phoneNumber?.trim()
    );
  }

  // === HELPER METHODS ===

  getFullName(student: Student): string {
    return `${student.person.firstName} ${student.person.lastName}`;
  }

  getStudentEnrollments(studentId: number): Enrollment[] {
    return this.allEnrollments.filter(e => e.userId === studentId);
  }

  getCompletedEnrollments(studentId: number): Enrollment[] {
    return this.getStudentEnrollments(studentId).filter(e => e.status === 'completed');
  }

  getStatusText(status: boolean | string): string {
    if (typeof status === 'boolean') {
      return status ? 'Activo' : 'Inactivo';
    }
    // Handle enrollment status strings
    switch (status) {
      case 'enrolled':
        return 'Inscrito';
      case 'in_progress':
        return 'En Progreso';
      case 'completed':
        return 'Completado';
      case 'suspended':
        return 'Suspendido';
      case 'cancelled':
        return 'Cancelado';
      default:
        return 'Desconocido';
    }
  }

  getStatusClass(active: boolean): string {
    return active ? 'badge bg-success' : 'badge bg-secondary';
  }

  getStatusBadgeClass(status: any): string {
    if (typeof status === 'boolean') {
      return status ? 'badge bg-success' : 'badge bg-secondary';
    }
    // Handle enrollment status
    switch (status) {
      case 'enrolled':
      case 'in_progress':
        return 'badge bg-primary';
      case 'completed':
        return 'badge bg-success';
      case 'cancelled':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  // Helper methods for student display
  getStudentName(enrollment: Enrollment): string {
    const student = this.allStudents.find(s => s.id === enrollment.userId);
    return student ? this.getFullName(student) : 'Estudiante no encontrado';
  }

  getProgramName(enrollment: Enrollment): string {
    // For now, return the program ID as we don't have program data loaded
    return `Programa ${enrollment.programId}`;
  }

  formatDate(dateString: string | Date | null | undefined): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES');
    } catch {
      return 'Fecha inválida';
    }
  }

  // View methods for student actions
  viewStudentDetails(student: Student): void {
    console.log('=== VIEW STUDENT DETAILS ===');
    console.log('Student:', student);
    this.selectedStudent = student;
    console.log('Selected student set to:', this.selectedStudent);
    console.log('About to show modal: studentDetailsModal');
    
    this.showModal('studentDetailsModal');
  }

  viewStudentCertificates(student: Student): void {
    // TODO: Implement student certificates view
    console.log('View student certificates:', student);
    // Note: Using success type for info messages since only success/error are supported
  }

  viewStudentCourses(student: Student) {
    this.selectedStudent = student;
    this.isLoadingStudentCourses = true;
    this.studentCourses = [];
    this.modalMessage = null; // Clear any previous modal messages
    
    console.log('Loading courses for student:', student.id);
    this.showModal('studentCoursesModal');
    
    // Load real enrollments from API
    this.enrollmentService.getEnrollmentsByUser(student.id)
      .pipe(
        catchError(error => {
          console.error('Error loading student enrollments:', error);
          this.showModalMessage('error', 'Error cargando cursos del estudiante');
          this.isLoadingStudentCourses = false;
          return of([]);
        })
      )
      .subscribe(enrollments => {
        console.log('Loaded enrollments for student:', enrollments);
        this.studentCourses = enrollments;
        this.isLoadingStudentCourses = false;
      });
  }



  getCourseStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'bg-success';
      case 'enrolled':
      case 'in_progress':
        return 'bg-primary';
      case 'suspended':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }

  getCourseStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      case 'enrolled':
        return 'Inscrito';
      case 'in_progress':
        return 'En Progreso';
      case 'suspended':
        return 'Suspendido';
      default:
        return 'Desconocido';
    }
  }

  trackByCourseId(index: number, course: any): number {
    return course.id || index;
  }

  private showModalMessage(type: 'success' | 'error', text: string) {
    this.modalMessage = { type, text };
    // Auto-hide message after 4 seconds
    setTimeout(() => {
      this.modalMessage = null;
    }, 4000);
  }



  markCourseAsCompleted(course: any) {
    console.log('Marking course as completed:', course);
    
    // Call API to update enrollment status to completed
    this.enrollmentService.updateEnrollmentStatus(course.id, 'completed')
      .pipe(
        catchError(error => {
          console.error('Error updating enrollment status:', error);
          this.showModalMessage('error', 'Error marcando curso como completado');
          return of(null);
        })
      )
      .subscribe(updatedEnrollment => {
        if (updatedEnrollment) {
          // Update local data
          const courseIndex = this.studentCourses.findIndex(c => c.id === course.id);
          if (courseIndex !== -1) {
            this.studentCourses[courseIndex] = updatedEnrollment;
          }
          this.showModalMessage('success', `✅ Curso "${course.program?.title || updatedEnrollment.programId}" marcado como completado`);
        }
      });
  }

  markCourseAsInProgress(course: any) {
    console.log('Marking course as in progress:', course);
    
    // Call API to update enrollment status to in_progress
    this.enrollmentService.updateEnrollmentStatus(course.id, 'in_progress')
      .pipe(
        catchError(error => {
          console.error('Error updating enrollment status:', error);
          this.showModalMessage('error', 'Error marcando curso como en progreso');
          return of(null);
        })
      )
      .subscribe(updatedEnrollment => {
        if (updatedEnrollment) {
          // Update local data
          const courseIndex = this.studentCourses.findIndex(c => c.id === course.id);
          if (courseIndex !== -1) {
            this.studentCourses[courseIndex] = updatedEnrollment;
          }
          this.showModalMessage('success', `🔄 Curso "${course.program?.title || updatedEnrollment.programId}" marcado como en progreso`);
        }
      });
  }

  deleteEnrollment(course: any) {
    if (confirm(`¿Estás seguro de que deseas eliminar la matrícula del estudiante en el curso "${course.program?.title || 'Sin título'}"?`)) {
      console.log('Deleting enrollment from course:', course);
      
      // Call API to delete enrollment
      this.enrollmentService.deleteEnrollment(course.id)
        .pipe(
          catchError(error => {
            console.error('Error deleting enrollment:', error);
            this.showModalMessage('error', 'Error eliminando matrícula');
            return of(null);
          })
        )
        .subscribe(() => {
          // Remove from local data
          this.studentCourses = this.studentCourses.filter(c => c.id !== course.id);
          this.showModalMessage('success', `🗑️ Matrícula eliminada del curso "${course.program?.title || 'Sin título'}"`);
        });
    }
  }

  // Computed properties for student display
  getStudentDisplayName(student: Student): string {
    return this.getFullName(student);
  }

  getStudentDocument(student: Student): string {
    return student.person.documentNumber;
  }

  getStudentEmail(student: Student): string {
    return student.person.email;
  }

  getStudentPhone(student: Student): string {
    return student.person.phoneNumber;
  }

  getStudentEnrollmentCount(student: Student): number {
    // Return count from real enrollments if available
    return this.allEnrollments.filter(e => e.userId === student.id).length;
  }

  getStudentCompletedCount(student: Student): number {
    return this.allEnrollments.filter(e => e.userId === student.id && e.status === 'completed').length;
  }

  getStudentActiveCount(student: Student): number {
    return this.allEnrollments.filter(e => e.userId === student.id && (e.status === 'enrolled' || e.status === 'in_progress')).length;
  }

  getStudentCompletionRate(student: Student): number {
    const totalEnrollments = this.getStudentEnrollmentCount(student);
    if (totalEnrollments === 0) return 0;
    
    const completedCount = this.getStudentCompletedCount(student);
    return Math.round((completedCount / totalEnrollments) * 100);
  }

  getGenderLabel(gender: string): string {
    const genderObj = this.genders.find(g => g.value === gender);
    return genderObj ? genderObj.label : gender || 'No especificado';
  }

  isStudentPremium(student: Student): boolean {
    return !!(student.isPremium || student.is_premium);
  }

  // Methods to handle modal transitions
  editStudentFromDetails() {
    if (!this.selectedStudent) return;
    
    console.log('Transitioning from details modal to edit modal');
    
    // Close current modal and immediately open edit modal
    this.closeModal('studentDetailsModal');
    this.editStudent(this.selectedStudent!);
  }

  viewStudentCoursesFromDetails() {
    if (!this.selectedStudent) return;
    
    console.log('Transitioning from details modal to courses modal');
    
    // Close current modal and immediately open courses modal
    this.closeModal('studentDetailsModal');
    this.viewStudentCourses(this.selectedStudent!);
  }

  trackByStudentId(index: number, student: Student): number {
    return student.id;
  }

  trackByEnrollmentId(index: number, enrollment: Enrollment): number {
    return enrollment.id;
  }

  // === COMPUTED PROPERTIES ===

  get totalStudents(): number {
    return this.statistics.totalStudents;
  }

  get activeEnrollments(): number {
    return this.allEnrollments.filter(e => e.status === 'enrolled' || e.status === 'in_progress').length;
  }

  get completedEnrollments(): number {
    return this.allEnrollments.filter(e => e.status === 'completed').length;
  }

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === MODAL MANAGEMENT ===

  showModal(modalId: string) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error('Modal element not found:', modalId);
      return;
    }

    try {
      // Use data-bs-toggle approach - let Bootstrap handle everything
      modalElement.classList.add('show');
      modalElement.style.display = 'block';
      modalElement.setAttribute('aria-modal', 'true');
      modalElement.setAttribute('aria-hidden', 'false');
      
      // Add backdrop
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

  closeModal(modalId: string) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;

    try {
      // Simple CSS approach - no Bootstrap API calls
      modalElement.classList.remove('show');
      modalElement.style.display = 'none';
      modalElement.setAttribute('aria-modal', 'false');
      modalElement.setAttribute('aria-hidden', 'true');
      
      // Remove backdrop
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
}
