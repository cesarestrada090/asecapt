import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService, Student, CreateStudentRequest, UpdateStudentRequest, StudentStatistics } from '../../services/student.service';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { ProgramService, Program } from '../../services/program.service';
import { catchError, switchMap } from 'rxjs/operators';
import { of, forkJoin } from 'rxjs';

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

  // Course editing properties (for student courses modal)
  editingStudentCourse: { [key: number]: boolean } = {};
  studentCourseEditForm: { [key: number]: { finalGrade: number | null, attendancePercentage: number | null, status: string, enrollmentDate: string } } = {};
  isUpdatingStudentCourse: { [key: number]: boolean } = {};

  // Course enrollment properties
  availableCourses: Program[] = [];
  courseSearchQuery: string = '';
  isLoadingAvailableCourses: boolean = false;
  isEnrollingStudent: boolean = false;

  // New properties for manual enrollment dates
  selectedCourseForEnrollment: Program | null = null;
  enrollmentForm = {
    enrollmentDate: '',
    issueDate: ''
  };
  today: string = new Date().toISOString().split('T')[0];

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
    private enrollmentService: EnrollmentService,
    private programService: ProgramService
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
        console.log('Received enrollments from backend:', enrollments);
        console.log('Number of enrollments received:', enrollments.length);

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
      this.showModalMessage('error', 'Por favor complete todos los campos obligatorios');
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
            this.showModalMessage('error', error.error?.error || 'Error actualizando estudiante');
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
            this.showModalMessage('success', 'Estudiante actualizado exitosamente');
            // Close modal after successful update
            setTimeout(() => {
              this.cancelStudentForm();
            }, 1500);
          }
          this.isSaving = false;
        });
    } else {
      // Create new student
      this.studentService.createStudent(this.studentForm)
        .pipe(
          catchError(error => {
            console.error('Error creating student:', error);
            this.showModalMessage('error', error.error?.error || 'Error creando estudiante');
            this.isSaving = false;
            return of(null);
          })
        )
        .subscribe(newStudent => {
          if (newStudent) {
            this.allStudents.push(newStudent);
            this.filteredStudents = [...this.allStudents];
            this.loadStatistics(); // Refresh statistics
            this.showModalMessage('success', 'Estudiante creado exitosamente');
            // Close modal after successful creation
            setTimeout(() => {
              this.cancelStudentForm();
            }, 1500);
          }
          this.isSaving = false;
        });
    }
  }

  toggleStudentStatus(student: Student) {
    // Si el estudiante está activo y se va a desactivar, mostrar confirmación
    if (student.active) {
      const studentName = this.getFullName(student);
      const confirmMessage = `¿Estás seguro de que deseas desactivar al estudiante ${studentName}?\n\n⚠️ ADVERTENCIA: Un estudiante desactivado no podrá iniciar sesión en el sistema hasta que sea reactivado.`;

      if (!confirm(confirmMessage)) {
        return; // Si cancela, no hacer nada
      }
    }

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

  // View methods for student actions
  viewStudentDetails(student: Student): void {
    console.log('=== VIEW STUDENT DETAILS ===');
    console.log('Student:', student);
    this.selectedStudent = student;
    console.log('Selected student set to:', this.selectedStudent);
    console.log('About to show modal: studentDetailsModal');

    this.showModal('studentDetailsModal');
  }



  viewStudentCourses(student: Student) {
    this.selectedStudent = student;
    this.isLoadingStudentCourses = true;
    this.studentCourses = [];
    this.modalMessage = null; // Clear any previous modal messages

    console.log('Loading courses for student:', student.id);
    this.showModal('studentCoursesModal');

    // Load enrollments and then get program details for each
    this.enrollmentService.getEnrollmentsByUser(student.id)
      .pipe(
        switchMap(enrollments => {
          console.log('Loaded enrollments for student:', enrollments);

          if (enrollments.length === 0) {
            return of([]);
          }

          // Get unique program IDs
          const programIds = [...new Set(enrollments.map(e => e.programId))];
          console.log('Loading program details for IDs:', programIds);

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
              const enrichedEnrollments = enrollments.map(enrollment => ({
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
          console.error('Error loading student courses:', error);
                      this.showModalMessage('error', 'Error cargando programas del estudiante');
          this.isLoadingStudentCourses = false;
          return of([]);
        })
      )
      .subscribe(enrichedCourses => {
        console.log('Final enriched courses:', enrichedCourses);
        this.studentCourses = enrichedCourses;
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

  private showModalMessage(type: 'success' | 'error', text: string) {
    this.modalMessage = { type, text };

    // Scroll to top of modal to ensure message is visible
    setTimeout(() => {
      const modalBody = document.querySelector('#studentModal .modal-body');
      if (modalBody) {
        modalBody.scrollTop = 0;
      }
    }, 100);

    // Auto-hide message after different durations based on type
    const duration = type === 'error' ? 8000 : 4000; // 8 seconds for errors, 4 for success
    setTimeout(() => {
      this.modalMessage = null;
    }, duration);
  }



  markCourseAsCompleted(course: any) {
    console.log('Marking course as completed:', course);

    // Validate required fields before completing
    const missingFields = [];

    if (course.finalGrade === null || course.finalGrade === undefined || course.finalGrade === '') {
      missingFields.push('Nota Final');
    }

    if (course.attendancePercentage === null || course.attendancePercentage === undefined || course.attendancePercentage === '') {
      missingFields.push('Porcentaje de Asistencia');
    }

    if (!course.enrollmentDate) {
      missingFields.push('Fecha de Matriculación');
    }

    if (missingFields.length > 0) {
      const missingFieldsText = missingFields.join(', ');
      this.showModalMessage('error', `Para completar el programa, los siguientes campos son obligatorios: ${missingFieldsText}. Por favor, edite la matrícula y complete estos campos antes de marcar como completado.`);
      return;
    }

    // Additional validation for grade and attendance ranges
    if (course.finalGrade < 0 || course.finalGrade > 100) {
      this.showModalMessage('error', 'La nota final debe estar entre 0 y 100 para poder completar el programa.');
      return;
    }

    if (course.attendancePercentage < 0 || course.attendancePercentage > 100) {
      this.showModalMessage('error', 'El porcentaje de asistencia debe estar entre 0% y 100% para poder completar el programa.');
      return;
    }

    // Show confirmation dialog
    const confirmMessage = `¿Está seguro de que desea marcar el programa "${course.program?.title || 'Sin título'}" como completado?\n\nNota Final: ${course.finalGrade}\nAsistencia: ${course.attendancePercentage}%\nFecha de Matriculación: ${this.formatDate(course.enrollmentDate)}`;

    if (!confirm(confirmMessage)) {
      return;
    }

    // Call API to update enrollment status to completed
    this.enrollmentService.updateEnrollmentStatus(course.id, 'completed')
      .pipe(
        catchError(error => {
          console.error('Error updating enrollment status:', error);
          this.showModalMessage('error', 'Error marcando programa como completado');
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
          this.showModalMessage('success', `✅ Programa "${course.program?.title || updatedEnrollment.programId}" marcado como completado exitosamente`);
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
                      this.showModalMessage('error', 'Error marcando programa como en progreso');
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
          this.showModalMessage('success', `🔄 Programa "${course.program?.title || updatedEnrollment.programId}" marcado como en progreso`);
        }
      });
  }

  deleteEnrollment(course: any) {
    if (confirm(`¿Estás seguro de que deseas eliminar la matrícula del estudiante en el programa "${course.program?.title || 'Sin título'}"?`)) {
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

          // Reload all enrollments to update counts
          this.loadEnrollments();

          this.showModalMessage('success', `🗑️ Matrícula eliminada del programa "${course.program?.title || 'Sin título'}"`);
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
    // Filter enrollments for this student
    const studentEnrollments = this.allEnrollments.filter(e => e.userId === student.id);

    // Debug log to see what's happening
    console.log(`Student ${student.id} enrollments:`, studentEnrollments);
    console.log(`Total enrollments in component:`, this.allEnrollments.length);

    return studentEnrollments.length;
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

  getCourseProgress(course: any): number {
    // If course is completed, return 100%
    if (course.status === 'completed') {
      return 100;
    }

    // If attendance percentage is available, use it as progress
    if (course.attendancePercentage) {
      return Math.round(course.attendancePercentage);
    }

    // For enrolled/in_progress courses, estimate progress based on time
    if (course.startDate && course.program?.duration) {
      try {
        const startDate = new Date(course.startDate);
        const currentDate = new Date();
        const daysPassed = Math.floor((currentDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));

        // Extract duration in days (rough estimation)
        const duration = course.program.duration.toLowerCase();
        let totalDays = 30; // Default to 30 days

        if (duration.includes('semana')) {
          const weeks = parseInt(duration.match(/\d+/)?.[0] || '4');
          totalDays = weeks * 7;
        } else if (duration.includes('mes')) {
          const months = parseInt(duration.match(/\d+/)?.[0] || '1');
          totalDays = months * 30;
        } else if (duration.includes('día')) {
          totalDays = parseInt(duration.match(/\d+/)?.[0] || '30');
        }

        const progress = Math.min(Math.max((daysPassed / totalDays) * 100, 0), 95);
        return Math.round(progress);
      } catch (error) {
        console.error('Error calculating course progress:', error);
      }
    }

    // Default progress for enrolled courses
    return course.status === 'enrolled' ? 10 : 0;
  }

  getGradeBadgeClass(grade: number | null | undefined): string {
    if (grade === null || grade === undefined) {
      return 'badge bg-secondary';
    }

    if (grade >= 90) {
      return 'badge bg-success';
    } else if (grade >= 80) {
      return 'badge bg-primary';
    } else if (grade >= 70) {
      return 'badge bg-warning';
    } else {
      return 'badge bg-danger';
    }
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

  // === STUDENT COURSE EDITING METHODS ===

  startEditingStudentCourse(course: any) {
    console.log('Starting to edit student course:', course);

    // Initialize edit form with current values
    this.studentCourseEditForm[course.id] = {
      finalGrade: course.finalGrade,
      attendancePercentage: course.attendancePercentage,
      status: course.status,
      enrollmentDate: course.enrollmentDate || ''
    };

    // Mark as editing
    this.editingStudentCourse[course.id] = true;

    console.log('Student course edit form initialized:', this.studentCourseEditForm[course.id]);
  }

  cancelEditingStudentCourse(courseId: number) {
    console.log('Canceling edit for student course:', courseId);

    // Remove from editing state
    delete this.editingStudentCourse[courseId];
    delete this.studentCourseEditForm[courseId];
    delete this.isUpdatingStudentCourse[courseId];
  }

  saveStudentCourseChanges(course: any) {
    const courseId = course.id;
    const formData = this.studentCourseEditForm[courseId];

    if (!formData) {
      console.error('No form data found for student course:', courseId);
      return;
    }

    console.log('Saving student course changes:', { courseId, formData });

    // Validate data
    if (formData.finalGrade !== null && (formData.finalGrade < 0 || formData.finalGrade > 100)) {
      this.showModalMessage('error', 'La nota debe estar entre 0 y 100');
      return;
    }

    if (formData.attendancePercentage !== null && (formData.attendancePercentage < 0 || formData.attendancePercentage > 100)) {
      this.showModalMessage('error', 'La asistencia debe estar entre 0% y 100%');
      return;
    }

    if (!formData.enrollmentDate) {
      this.showModalMessage('error', 'La fecha de matriculación es obligatoria');
      return;
    }

    // Mark as updating
    this.isUpdatingStudentCourse[courseId] = true;

    // Prepare update request
    const updateRequest: any = {
      finalGrade: formData.finalGrade,
      attendancePercentage: formData.attendancePercentage,
      status: formData.status as 'enrolled' | 'in_progress' | 'completed' | 'suspended',
      enrollmentDate: formData.enrollmentDate
    };

    // If marking as completed, set completion date
    if (formData.status === 'completed' && course.status !== 'completed') {
      updateRequest.completionDate = new Date().toISOString().split('T')[0];
    }

    console.log('Sending student course update request:', updateRequest);

    // Call the API to update enrollment
    this.enrollmentService.updateEnrollment(courseId, updateRequest)
      .pipe(
        catchError(error => {
          console.error('Error updating student course:', error);
          this.showModalMessage('error', 'Error actualizando el programa');
          this.isUpdatingStudentCourse[courseId] = false;
          return of(null);
        })
      )
      .subscribe(updatedCourse => {
        if (updatedCourse) {
          console.log('Student course updated successfully:', updatedCourse);

          // Update the local data
          const courseIndex = this.studentCourses.findIndex(c => c.id === courseId);
          if (courseIndex !== -1) {
            this.studentCourses[courseIndex] = {
              ...this.studentCourses[courseIndex],
              ...updatedCourse
            };
          }

          // Clean up editing state
          this.cancelEditingStudentCourse(courseId);

          // Show success message
          this.showModalMessage('success', 'Programa actualizado exitosamente');

          // Update statistics
          this.loadStatistics();
        }

        this.isUpdatingStudentCourse[courseId] = false;
      });
  }

  trackByCourseId(index: number, course: Program): number {
    return course.id;
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
    // Clear modal messages when opening studentModal
    if (modalId === 'studentModal') {
      this.modalMessage = null;
    }

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

  // === COURSE ENROLLMENT METHODS ===

  showEnrollmentSearch() {
    if (!this.selectedStudent) return;

    console.log('Opening course enrollment modal for student:', this.selectedStudent);

    // Reset search state
    this.courseSearchQuery = '';
    this.availableCourses = [];
    this.isLoadingAvailableCourses = false;
    this.isEnrollingStudent = false;
    this.selectedCourseForEnrollment = null;
    this.resetEnrollmentForm();

    this.showModal('courseEnrollmentModal');
  }

  searchAvailableCourses() {
    if (!this.courseSearchQuery.trim()) {
      this.availableCourses = [];
      return;
    }

    this.isLoadingAvailableCourses = true;
    console.log('Searching for courses with query:', this.courseSearchQuery);

    this.programService.searchPrograms(this.courseSearchQuery, 'course', 'active')
      .pipe(
        catchError(error => {
          console.error('Error searching courses:', error);
          this.showModalMessage('error', 'Error buscando programas disponibles');
          this.isLoadingAvailableCourses = false;
          return of([]);
        })
      )
      .subscribe(courses => {
        console.log('Found courses:', courses);

        // Filter out courses the student is already enrolled in
        if (this.selectedStudent) {
          const enrolledProgramIds = this.studentCourses.map(c => c.programId);
          this.availableCourses = courses.filter(course =>
            !enrolledProgramIds.includes(course.id)
          );
        } else {
          this.availableCourses = courses;
        }

        this.isLoadingAvailableCourses = false;
        console.log('Available courses after filtering:', this.availableCourses);
      });
  }

  clearCourseSearch() {
    this.courseSearchQuery = '';
    this.availableCourses = [];
  }

  selectCourseForEnrollment(course: Program) {
    console.log('Selected course for enrollment:', course);
    this.selectedCourseForEnrollment = course;
    this.resetEnrollmentForm();
  }

  resetEnrollmentForm() {
    const today = new Date().toISOString().split('T')[0];
    this.enrollmentForm = {
      enrollmentDate: today, // Default to today
      issueDate: '' // Leave empty - optional
    };
  }

  confirmEnrollment() {
    if (!this.selectedStudent || !this.selectedCourseForEnrollment) {
      console.error('No student or course selected for enrollment');
      return;
    }

    if (!this.enrollmentForm.enrollmentDate) {
      this.showModalMessage('error', 'La fecha de matriculación es obligatoria');
      return;
    }

    this.isEnrollingStudent = true;
    console.log('Confirming enrollment with dates:', this.enrollmentForm);

    // Create enrollment request with manual dates
    const enrollmentRequest = {
      userId: this.selectedStudent.id,
      programId: this.selectedCourseForEnrollment.id,
      status: 'enrolled' as const,
      enrollmentDate: this.enrollmentForm.enrollmentDate,
      issueDate: this.enrollmentForm.issueDate || null
    };

    this.enrollmentService.createEnrollment(enrollmentRequest)
      .pipe(
        catchError(error => {
          console.error('Error enrolling student:', error);
          this.showModalMessage('error', `Error matriculando en "${this.selectedCourseForEnrollment?.title || 'curso seleccionado'}"`);
          this.isEnrollingStudent = false;
          return of(null);
        })
      )
      .subscribe(newEnrollment => {
        if (newEnrollment && this.selectedCourseForEnrollment) {
          console.log('Student enrolled successfully with manual dates:', newEnrollment);

          // Add to student courses list with program information included
          const enrollmentWithProgram = {
            ...newEnrollment,
            program: this.selectedCourseForEnrollment // Include the full program information
          };
          this.studentCourses.push(enrollmentWithProgram);

          // Remove from available courses
          this.availableCourses = this.availableCourses.filter(c => c.id !== this.selectedCourseForEnrollment!.id);

          // Show success message
          this.showModalMessage('success', `✅ Estudiante matriculado en "${this.selectedCourseForEnrollment.title}" exitosamente`);

          // Reload enrollments to update counts
          this.loadEnrollments();

          // Update statistics
          this.loadStatistics();

          // Reset enrollment state
          this.selectedCourseForEnrollment = null;
          this.resetEnrollmentForm();

          // Close enrollment modal after a delay
          setTimeout(() => {
            this.closeModal('courseEnrollmentModal');
          }, 1500);
        }
        this.isEnrollingStudent = false;
      });
  }

  cancelEnrollment() {
    this.selectedCourseForEnrollment = null;
    this.resetEnrollmentForm();
  }

  enrollStudentInCourse(course: Program) {
    // This method is now replaced by selectCourseForEnrollment
    this.selectCourseForEnrollment(course);
  }
}
