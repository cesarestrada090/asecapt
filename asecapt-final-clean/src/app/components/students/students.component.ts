import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  allEnrollments: Enrollment[] = [];
  filteredEnrollments: Enrollment[] = [];

  // Loading states
  isLoadingEnrollments: boolean = false;
  isSearching: boolean = false;

  // Search form
  searchForm = {
    query: '',
    status: '',
    programId: ''
  };

  // Mock data for display (until we have proper user service)
  mockStudents: any[] = [];
  filteredStudents: any[] = [];

  constructor(private enrollmentService: EnrollmentService) {}

  ngOnInit() {
    this.loadEnrollments();
    this.initializeMockData();
  }

  // === DATA LOADING ===

  loadEnrollments() {
    this.isLoadingEnrollments = true;
    this.enrollmentService.getAllEnrollments()
      .pipe(
        catchError(error => {
          console.error('Error loading enrollments:', error);
          this.emitMessage('error', 'Error cargando información de estudiantes');
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.allEnrollments = enrollments;
        this.filteredEnrollments = [...enrollments];
        this.isLoadingEnrollments = false;
        this.updateStudentList();
      });
  }

  // === SEARCH ===

  searchStudents() {
    if (!this.searchForm.query.trim()) {
      this.filteredStudents = [...this.mockStudents];
      this.filteredEnrollments = this.allEnrollments.filter(enrollment => {
        return (!this.searchForm.status || enrollment.status === this.searchForm.status) &&
               (!this.searchForm.programId || enrollment.programId.toString() === this.searchForm.programId);
      });
      return;
    }

    this.isSearching = true;

    // Filter enrollments based on search criteria
    this.filteredEnrollments = this.allEnrollments.filter(enrollment => {
      const matchesStatus = !this.searchForm.status || enrollment.status === this.searchForm.status;
      const matchesProgram = !this.searchForm.programId || enrollment.programId.toString() === this.searchForm.programId;

      // For now, we'll search by user ID since we don't have proper user names
      const matchesQuery = enrollment.userId.toString().includes(this.searchForm.query);

      return matchesStatus && matchesProgram && matchesQuery;
    });

    this.updateStudentList();
    this.isSearching = false;
  }

  clearStudentSearch() {
    this.searchForm.query = '';
    this.searchForm.status = '';
    this.searchForm.programId = '';
    this.filteredStudents = [...this.mockStudents];
    this.filteredEnrollments = [...this.allEnrollments];
  }

  // === MOCK DATA ===

  initializeMockData() {
    // Generate mock student data for display purposes
    this.mockStudents = [
      {
        id: 1,
        name: 'Juan Pérez',
        email: 'juan.perez@email.com',
        document: '12345678',
        phone: '+51 999 123 456',
        enrollments: 3,
        completedCourses: 2,
        status: 'active'
      },
      {
        id: 2,
        name: 'María García',
        email: 'maria.garcia@email.com',
        document: '87654321',
        phone: '+51 999 654 321',
        enrollments: 2,
        completedCourses: 2,
        status: 'active'
      },
      {
        id: 3,
        name: 'Carlos López',
        email: 'carlos.lopez@email.com',
        document: '11223344',
        phone: '+51 999 111 222',
        enrollments: 4,
        completedCourses: 1,
        status: 'active'
      }
    ];
    this.filteredStudents = [...this.mockStudents];
  }

  updateStudentList() {
    // Update student list based on enrollments
    // This is a simplified version - in a real app, you'd fetch user details from a user service
    const studentMap = new Map();

    this.filteredEnrollments.forEach(enrollment => {
      if (!studentMap.has(enrollment.userId)) {
        studentMap.set(enrollment.userId, {
          id: enrollment.userId,
          name: `Estudiante #${enrollment.userId}`,
          email: `estudiante${enrollment.userId}@example.com`,
          document: `DOC${enrollment.userId.toString().padStart(6, '0')}`,
          phone: `+51 999 ${enrollment.userId.toString().padStart(3, '0')} 000`,
          enrollments: 0,
          completedCourses: 0,
          status: 'active'
        });
      }

      const student = studentMap.get(enrollment.userId);
      student.enrollments++;
      if (enrollment.status === 'completed') {
        student.completedCourses++;
      }
    });

    // For display, we'll use a mix of mock data and real enrollment data
    this.filteredStudents = Array.from(studentMap.values());
  }

  // === STUDENT ACTIONS ===

  viewStudentDetails(student: any) {
    // Show student enrollments
    const studentEnrollments = this.allEnrollments.filter(e => e.userId === student.id);
    console.log('Student enrollments:', studentEnrollments);

  }

  editStudent(student: any) {
    // TODO: Implement student editing
    this.emitMessage('success', `Editando información de ${student.name}`);
  }

  viewStudentCertificates(student: any) {
    // TODO: Navigate to certificates view filtered by student
    this.emitMessage('success', `Mostrando certificados de ${student.name}`);
  }

  // === UI HELPERS ===

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'completed': return 'badge bg-success';
      case 'in_progress': return 'badge bg-primary';
      case 'enrolled': return 'badge bg-info';
      case 'active': return 'badge bg-success';
      default: return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'completed': return 'Completado';
      case 'in_progress': return 'En Progreso';
      case 'enrolled': return 'Inscrito';
      case 'active': return 'Activo';
      default: return status;
    }
  }

  formatDate(date: string | Date | null): string {
    if (!date) return 'N/A';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('es-ES');
  }

  getStudentName(enrollment: Enrollment): string {
    // TODO: Implement proper student name retrieval
    return `Estudiante #${enrollment.userId}`;
  }

  getProgramName(enrollment: Enrollment): string {
    // TODO: Implement proper program name retrieval
    return `Programa #${enrollment.programId}`;
  }

  // === FILTERS ===

  get availableStatuses() {
    return [
      { value: '', label: 'Todos los estados' },
      { value: 'enrolled', label: 'Inscritos' },
      { value: 'in_progress', label: 'En Progreso' },
      { value: 'completed', label: 'Completados' }
    ];
  }

  get availablePrograms() {
    const programs = [...new Set(this.allEnrollments.map(e => e.programId))];
    return [
      { value: '', label: 'Todos los programas' },
      ...programs.map(id => ({ value: id.toString(), label: `Programa #${id}` }))
    ];
  }

  // === STATISTICS ===

  get totalStudents(): number {
    return this.filteredStudents.length;
  }

  get activeEnrollments(): number {
    return this.filteredEnrollments.filter(e => e.status !== 'completed').length;
  }

  get completedEnrollments(): number {
    return this.filteredEnrollments.filter(e => e.status === 'completed').length;
  }

  // === UTILITY ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === TRACKING ===

  trackByStudentId(index: number, item: any): number {
    return item.id;
  }

  trackByEnrollmentId(index: number, item: Enrollment): number {
    return item.id;
  }
}
