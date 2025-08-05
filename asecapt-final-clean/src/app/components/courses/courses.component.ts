import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.css'
})
export class CoursesComponent implements OnInit {
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
    type: ''
  };

  // Mock data for display (until we have proper program service)
  mockCourses: any[] = [];
  filteredCourses: any[] = [];

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
          this.emitMessage('error', 'Error cargando información de cursos');
          return of([]);
        })
      )
      .subscribe(enrollments => {
        this.allEnrollments = enrollments;
        this.filteredEnrollments = [...enrollments];
        this.isLoadingEnrollments = false;
        this.updateCourseList();
      });
  }

  // === SEARCH ===

  searchCourses() {
    if (!this.searchForm.query.trim()) {
      this.filteredCourses = this.mockCourses.filter(course => {
        return (!this.searchForm.status || course.status === this.searchForm.status) &&
               (!this.searchForm.type || course.type === this.searchForm.type);
      });
      return;
    }

    this.isSearching = true;
    
    // Filter courses based on search criteria
    this.filteredCourses = this.mockCourses.filter(course => {
      const matchesStatus = !this.searchForm.status || course.status === this.searchForm.status;
      const matchesType = !this.searchForm.type || course.type === this.searchForm.type;
      const matchesQuery = course.name.toLowerCase().includes(this.searchForm.query.toLowerCase()) ||
                          course.description.toLowerCase().includes(this.searchForm.query.toLowerCase());
      
      return matchesStatus && matchesType && matchesQuery;
    });

    this.isSearching = false;
  }

  clearCourseSearch() {
    this.searchForm.query = '';
    this.searchForm.status = '';
    this.searchForm.type = '';
    this.filteredCourses = [...this.mockCourses];
  }

  // === MOCK DATA ===

  initializeMockData() {
    // Generate mock course data for display purposes
    this.mockCourses = [
      {
        id: 1,
        name: 'Fundamentos de Programación',
        description: 'Curso básico de programación con Python',
        type: 'course',
        category: 'Programación',
        duration: '40 horas',
        credits: 3,
        price: 'S/. 299',
        status: 'active',
        enrollments: 15,
        completions: 12,
        instructor: 'Prof. García',
        startDate: '2024-01-15',
        endDate: '2024-03-15'
      },
      {
        id: 2,
        name: 'Especialización en Data Science',
        description: 'Programa especializado en ciencia de datos',
        type: 'specialization',
        category: 'Data Science',
        duration: '120 horas',
        credits: 8,
        price: 'S/. 899',
        status: 'active',
        enrollments: 8,
        completions: 5,
        instructor: 'Prof. López',
        startDate: '2024-02-01',
        endDate: '2024-06-01'
      },
      {
        id: 3,
        name: 'Certificación en Ciberseguridad',
        description: 'Programa de certificación profesional',
        type: 'certification',
        category: 'Seguridad',
        duration: '80 horas',
        credits: 5,
        price: 'S/. 599',
        status: 'active',
        enrollments: 12,
        completions: 8,
        instructor: 'Prof. Martín',
        startDate: '2024-01-20',
        endDate: '2024-04-20'
      },
      {
        id: 4,
        name: 'Diseño Web Avanzado',
        description: 'Curso avanzado de diseño y desarrollo web',
        type: 'course',
        category: 'Diseño',
        duration: '60 horas',
        credits: 4,
        price: 'S/. 449',
        status: 'draft',
        enrollments: 0,
        completions: 0,
        instructor: 'Prof. Rivera',
        startDate: '2024-04-01',
        endDate: '2024-06-01'
      }
    ];
    this.filteredCourses = [...this.mockCourses];
  }

  updateCourseList() {
    // Update course statistics based on enrollments
    const courseStats = new Map();
    
    this.allEnrollments.forEach(enrollment => {
      if (!courseStats.has(enrollment.programId)) {
        courseStats.set(enrollment.programId, {
          enrollments: 0,
          completions: 0
        });
      }
      
      const stats = courseStats.get(enrollment.programId);
      stats.enrollments++;
      if (enrollment.status === 'completed') {
        stats.completions++;
      }
    });
    
    // Update mock courses with real statistics
    this.mockCourses.forEach(course => {
      const stats = courseStats.get(course.id) || { enrollments: 0, completions: 0 };
      course.enrollments = stats.enrollments;
      course.completions = stats.completions;
    });
    
    this.filteredCourses = [...this.mockCourses];
  }

  // === COURSE ACTIONS ===

  viewCourseDetails(course: any) {
    // Show course enrollments and details
    const courseEnrollments = this.allEnrollments.filter(e => e.programId === course.id);
    console.log('Course enrollments:', courseEnrollments);
    this.emitMessage('success', `Mostrando detalles de ${course.name}`);
  }

  editCourse(course: any) {
    // TODO: Implement course editing
    this.emitMessage('success', `Editando ${course.name}`);
  }

  createNewCourse() {
    // TODO: Implement course creation
    this.emitMessage('success', 'Creando nuevo curso');
  }

  duplicateCourse(course: any) {
    // TODO: Implement course duplication
    this.emitMessage('success', `Duplicando ${course.name}`);
  }

  archiveCourse(course: any) {
    // Update course status to archived
    course.status = course.status === 'archived' ? 'active' : 'archived';
    const action = course.status === 'archived' ? 'archivado' : 'restaurado';
    this.emitMessage('success', `Curso ${action}: ${course.name}`);
  }

  viewCourseStudents(course: any) {
    // TODO: Navigate to students view filtered by course
    this.emitMessage('success', `Mostrando estudiantes de ${course.name}`);
  }

  viewCourseCertificates(course: any) {
    // TODO: Navigate to certificates view filtered by course
    this.emitMessage('success', `Mostrando certificados de ${course.name}`);
  }

  // === UI HELPERS ===

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'active': return 'badge bg-success';
      case 'draft': return 'badge bg-warning';
      case 'archived': return 'badge bg-secondary';
      case 'suspended': return 'badge bg-danger';
      default: return 'badge bg-info';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'active': return 'Activo';
      case 'draft': return 'Borrador';
      case 'archived': return 'Archivado';
      case 'suspended': return 'Suspendido';
      default: return status;
    }
  }

  getTypeText(type: string): string {
    switch (type) {
      case 'course': return 'Curso';
      case 'specialization': return 'Especialización';
      case 'certification': return 'Certificación';
      default: return type;
    }
  }

  getTypeBadgeClass(type: string): string {
    switch (type) {
      case 'course': return 'badge bg-primary';
      case 'specialization': return 'badge bg-info';
      case 'certification': return 'badge bg-warning';
      default: return 'badge bg-secondary';
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('es-ES');
  }

  getCompletionRate(course: any): number {
    if (course.enrollments === 0) return 0;
    return Math.round((course.completions / course.enrollments) * 100);
  }

  getCompletionRateClass(rate: number): string {
    if (rate >= 80) return 'text-success';
    if (rate >= 60) return 'text-warning';
    return 'text-danger';
  }

  // === FILTERS ===

  get availableStatuses() {
    return [
      { value: '', label: 'Todos los estados' },
      { value: 'active', label: 'Activos' },
      { value: 'draft', label: 'Borradores' },
      { value: 'archived', label: 'Archivados' },
      { value: 'suspended', label: 'Suspendidos' }
    ];
  }

  get availableTypes() {
    return [
      { value: '', label: 'Todos los tipos' },
      { value: 'course', label: 'Cursos' },
      { value: 'specialization', label: 'Especializaciones' },
      { value: 'certification', label: 'Certificaciones' }
    ];
  }

  get availableCategories() {
    const categories = [...new Set(this.mockCourses.map(c => c.category))];
    return [
      { value: '', label: 'Todas las categorías' },
      ...categories.map(cat => ({ value: cat, label: cat }))
    ];
  }

  // === STATISTICS ===

  get totalCourses(): number {
    return this.filteredCourses.length;
  }

  get activeCourses(): number {
    return this.filteredCourses.filter(c => c.status === 'active').length;
  }

  get draftCourses(): number {
    return this.filteredCourses.filter(c => c.status === 'draft').length;
  }

  get totalEnrollments(): number {
    return this.filteredCourses.reduce((sum, course) => sum + course.enrollments, 0);
  }

  get totalCompletions(): number {
    return this.filteredCourses.reduce((sum, course) => sum + course.completions, 0);
  }

  get averageCompletionRate(): number {
    if (this.totalEnrollments === 0) return 0;
    return Math.round((this.totalCompletions / this.totalEnrollments) * 100);
  }

  // === UTILITY ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === TRACKING ===

  trackByCourseId(index: number, item: any): number {
    return item.id;
  }
} 