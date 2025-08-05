import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { ProgramService, Program, CreateProgramRequest, UpdateProgramRequest, Content, CreateContentRequest, UpdateContentRequest, ProgramContent, AddContentToProgramRequest } from '../../services/program.service';
import { ContentService } from '../../services/content.service';
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
  allPrograms: Program[] = [];
  filteredPrograms: Program[] = [];
  allContents: Content[] = [];
  availableContents: Content[] = [];
  
  // Program content counts cache
  programContentCounts: Map<number, number> = new Map();

  // Loading states
  isLoadingPrograms: boolean = false;
  isLoadingContents: boolean = false;
  isSearching: boolean = false;
  isSaving: boolean = false;

  // Search form
  searchForm = {
    query: '',
    status: '',
    type: ''
  };

  // === PROGRAM FORM STATE ===
  programFormModel: CreateProgramRequest = {
    title: '',
    description: '',
    type: 'course',
    category: '',
    status: 'active', // Changed from 'draft' to 'active'
    duration: '',
    credits: 0,
    price: '',
    startDate: '',
    endDate: '',
    instructor: '',
    maxStudents: 0,
    prerequisites: '',
    objectives: ''
  };
  editingProgram: Program | null = null;

  // === CONTENT FORM STATE ===
  contentFormModel: CreateContentRequest = {
    title: '',
    description: '',
    type: 'module',
    duration: '',
    content: '',
    isRequired: true
  };
  editingContent: Content | null = null;

  // === PROGRAM CONTENT MANAGEMENT ===
  selectedProgram: Program | null = null;
  programContents: ProgramContent[] = [];
  isLoadingProgramContents: boolean = false;

  // === VIEW STATES ===
  currentView: string = 'list';

  constructor(
    private enrollmentService: EnrollmentService,
    private programService: ProgramService,
    private contentService: ContentService
  ) {}

  ngOnInit() {
    this.loadPrograms();
    this.loadContents();
  }

  // === DATA LOADING ===

  loadPrograms() {
    this.isLoadingPrograms = true;
    this.programService.getAllPrograms()
      .pipe(
        catchError(error => {
          console.error('Error loading programs:', error);
          this.emitMessage('error', 'Error cargando programas');
          return of([]);
        })
      )
      .subscribe(programs => {
        this.allPrograms = programs;
        this.filteredPrograms = [...programs];
        this.isLoadingPrograms = false;
        
        // Load content counts for each program
        this.loadProgramContentCounts();
      });
  }

  loadContents() {
    this.isLoadingContents = true;
    this.contentService.getAllContents()
      .pipe(
        catchError(error => {
          console.error('Error loading contents:', error);
          this.emitMessage('error', 'Error cargando contenidos');
          return of([]);
        })
      )
      .subscribe(contents => {
        this.allContents = contents;
        this.availableContents = [...contents];
        this.isLoadingContents = false;
      });
  }

    // === SEARCH ===

  searchPrograms() {
    if (!this.searchForm.query.trim()) {
      this.filteredPrograms = this.allPrograms.filter(program => {
        return (!this.searchForm.status || program.status === this.searchForm.status) &&
               (!this.searchForm.type || program.type === this.searchForm.type);
      });
      return;
    }

    this.isSearching = true;
    this.programService.searchPrograms(this.searchForm.query, this.searchForm.type, this.searchForm.status)
      .pipe(
        catchError(error => {
          console.error('Error searching programs:', error);
          this.emitMessage('error', 'Error en la búsqueda de programas');
          return of([]);
        })
      )
      .subscribe(programs => {
        this.filteredPrograms = programs;
        this.isSearching = false;
      });
  }

  clearSearch() {
    this.searchForm.query = '';
    this.searchForm.status = '';
    this.searchForm.type = '';
    this.filteredPrograms = [...this.allPrograms];
  }

    // === PROGRAM CONTENT MANAGEMENT ===

  loadProgramContents(programId: number) {
    this.isLoadingProgramContents = true;
    this.programService.getProgramContents(programId)
      .pipe(
        catchError(error => {
          console.error('Error loading program contents:', error);
          this.emitMessage('error', 'Error cargando contenidos del programa');
          return of([]);
        })
      )
      .subscribe(contents => {
        this.programContents = contents;
        this.isLoadingProgramContents = false;
      });
  }

  addContentToProgram(contentId: number) {
    if (!this.selectedProgram) return;

    const request: AddContentToProgramRequest = {
      programId: this.selectedProgram.id,
      contentId: contentId,
      orderIndex: this.programContents.length + 1,
      isRequired: true
    };

    this.programService.addContentToProgram(request)
      .pipe(
        catchError(error => {
          console.error('Error adding content to program:', error);
          this.emitMessage('error', 'Error agregando contenido al programa');
          return of(null);
        })
      )
      .subscribe(programContent => {
        if (programContent) {
          this.programContents.push(programContent);
          this.emitMessage('success', 'Contenido agregado exitosamente');
        }
      });
  }

  removeContentFromProgram(contentId: number) {
    if (!this.selectedProgram) return;

    this.programService.removeProgramContent(this.selectedProgram.id, contentId)
      .pipe(
        catchError(error => {
          console.error('Error removing content from program:', error);
          this.emitMessage('error', 'Error removiendo contenido del programa');
          return of(null);
        })
      )
      .subscribe(() => {
        this.programContents = this.programContents.filter(pc => pc.contentId !== contentId);
        this.emitMessage('success', 'Contenido removido exitosamente');
      });
  }

  // === PROGRAM ACTIONS ===

  viewProgramDetails(program: Program) {
    console.log('🔍 viewProgramDetails called with:', program);
    console.log('🔍 Current view before:', this.currentView);
    
    this.selectedProgram = program;
    this.currentView = 'details';
    
    console.log('🔍 Selected program set to:', this.selectedProgram);
    console.log('🔍 Current view after:', this.currentView);
    console.log('🔍 isDetailsView():', this.isDetailsView());
    console.log('🔍 selectedProgram exists:', !!this.selectedProgram);
    
    this.loadProgramContents(program.id);
    this.emitMessage('success', `Mostrando detalles de ${program.title}`);
  }

  createNewProgram() {
    this.editingProgram = null;
    this.programFormModel = {
      title: '',
      description: '',
      type: 'course',
      category: '',
      status: 'active', // Changed from 'draft' to 'active'
      duration: '',
      credits: 0,
      price: '',
      startDate: '',
      endDate: '',
      instructor: '',
      maxStudents: 0,
      prerequisites: '',
      objectives: ''
    };
    setTimeout(() => {
      // @ts-ignore
      const modal = new window.bootstrap.Modal(document.getElementById('programModal'));
      modal.show();
    }, 0);
  }

  editProgram(program: Program) {
    this.editingProgram = program;
    this.programFormModel = { ...program };
    setTimeout(() => {
      // @ts-ignore
      const modal = new window.bootstrap.Modal(document.getElementById('programModal'));
      modal.show();
    }, 0);
  }

  saveProgram() {
    this.isSaving = true;
    
    if (this.editingProgram) {
      // Update existing program
      const updateRequest: UpdateProgramRequest = { ...this.programFormModel, id: this.editingProgram.id };
      this.programService.updateProgram(this.editingProgram.id, updateRequest)
        .pipe(
          catchError(error => {
            console.error('Error updating program:', error);
            this.emitMessage('error', 'Error actualizando programa');
            return of(null);
          })
        )
        .subscribe(program => {
          this.isSaving = false;
          if (program) {
            const index = this.allPrograms.findIndex(p => p.id === program.id);
            if (index > -1) {
              this.allPrograms[index] = program;
              this.filteredPrograms = [...this.allPrograms];
            }
            this.emitMessage('success', 'Programa actualizado correctamente');
            this.closeModal('programModal');
          }
        });
    } else {
      // Create new program
      this.programService.createProgram(this.programFormModel)
        .pipe(
          catchError(error => {
            console.error('Error creating program:', error);
            this.emitMessage('error', 'Error creando programa');
            return of(null);
          })
        )
        .subscribe(program => {
          this.isSaving = false;
          if (program) {
            this.allPrograms.unshift(program);
            this.filteredPrograms = [...this.allPrograms];
            this.emitMessage('success', 'Programa creado correctamente');
            this.closeModal('programModal');
          }
        });
    }
  }

  toggleCourseStatus(course: any) {
    // Update course status to active/inactive
    course.status = course.status === 'active' ? 'inactive' : 'active';
    const action = course.status === 'active' ? 'activado' : 'inactivado';
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

  duplicateProgram(program: Program) {
    const newTitle = program.title + ' (Copia)';
    this.programService.duplicateProgram(program.id, newTitle)
      .pipe(
        catchError(error => {
          console.error('Error duplicating program:', error);
          this.emitMessage('error', 'Error duplicando programa');
          return of(null);
        })
      )
      .subscribe(duplicated => {
        if (duplicated) {
          this.allPrograms.unshift(duplicated);
          this.filteredPrograms = [...this.allPrograms];
          this.emitMessage('success', `Programa duplicado: ${duplicated.title}`);
        }
      });
  }

  // === CONTENT ACTIONS ===

  createNewContent() {
    this.editingContent = null;
    this.contentFormModel = {
      title: '',
      description: '',
      type: 'module',
      duration: '',
      content: '',
      isRequired: true
    };
    setTimeout(() => {
      // @ts-ignore
      const modal = new window.bootstrap.Modal(document.getElementById('contentModal'));
      modal.show();
    }, 0);
  }

  editContent(content: Content) {
    this.editingContent = content;
    this.contentFormModel = { ...content };
    setTimeout(() => {
      // @ts-ignore
      const modal = new window.bootstrap.Modal(document.getElementById('contentModal'));
      modal.show();
    }, 0);
  }

  saveContent() {
    this.isSaving = true;
    
    if (this.editingContent) {
      // Update existing content
      const updateRequest: UpdateContentRequest = { ...this.contentFormModel, id: this.editingContent.id };
      this.contentService.updateContent(this.editingContent.id, updateRequest)
        .pipe(
          catchError(error => {
            console.error('Error updating content:', error);
            this.emitMessage('error', 'Error actualizando contenido');
            return of(null);
          })
        )
        .subscribe(content => {
          this.isSaving = false;
          if (content) {
            const index = this.allContents.findIndex(c => c.id === content.id);
            if (index > -1) {
              this.allContents[index] = content;
              this.availableContents = [...this.allContents];
            }
            this.emitMessage('success', 'Contenido actualizado correctamente');
            this.closeModal('contentModal');
          }
        });
    } else {
      // Create new content
      this.contentService.createContent(this.contentFormModel)
        .pipe(
          catchError(error => {
            console.error('Error creating content:', error);
            this.emitMessage('error', 'Error creando contenido');
            return of(null);
          })
        )
        .subscribe(content => {
          this.isSaving = false;
          if (content) {
            this.allContents.unshift(content);
            this.availableContents = [...this.allContents];
            this.emitMessage('success', 'Contenido creado correctamente');
            this.closeModal('contentModal');
          }
        });
    }
  }

  // === UTILITY METHODS ===

  closeModal(modalId: string) {
    setTimeout(() => {
      // @ts-ignore
      const modal = window.bootstrap.Modal.getInstance(document.getElementById(modalId));
      if (modal) modal.hide();
    }, 0);
  }

  setCurrentView(view: string) {
    this.currentView = view;
    if (view === 'list') {
      this.selectedProgram = null;
    }
  }

  isContentAlreadyAssigned(contentId: number): boolean {
    return this.programContents.some(pc => pc.contentId === contentId);
  }

  // === VIEW HELPER METHODS ===
  
  isListView(): boolean {
    return this.currentView === 'list';
  }

  isDetailsView(): boolean {
    return this.currentView === 'details';
  }

  isContentLibraryView(): boolean {
    return this.currentView === 'content-library';
  }

  // === UI HELPERS ===

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'active': return 'badge bg-success';
      case 'inactive': return 'badge bg-secondary';
      default: return 'badge bg-info';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'active': return 'Activo';
      case 'inactive': return 'Inactivo';
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
      { value: 'inactive', label: 'Inactivos' }
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
    const categories = [...new Set(this.allPrograms.map(p => p.category))];
    return [
      { value: '', label: 'Todas las categorías' },
      ...categories.map(cat => ({ value: cat, label: cat }))
    ];
  }

  get availableContentTypes() {
    return [
      { value: '', label: 'Todos los tipos' },
      { value: 'module', label: 'Módulos' },
      { value: 'lesson', label: 'Lecciones' },
      { value: 'assignment', label: 'Asignaciones' },
      { value: 'exam', label: 'Exámenes' },
      { value: 'resource', label: 'Recursos' }
    ];
  }

  // === STATISTICS ===

  get totalPrograms(): number {
    return this.filteredPrograms.length;
  }

  get activePrograms(): number {
    return this.filteredPrograms.filter(p => p.status === 'active').length;
  }

  get inactivePrograms(): number {
    return this.filteredPrograms.filter(p => p.status === 'inactive').length;
  }

  get totalContents(): number {
    return this.allContents.length;
  }

  get moduleContents(): number {
    return this.allContents.filter(c => c.type === 'module').length;
  }

  get totalProgramContents(): number {
    // Sum all content counts from all programs
    let total = 0;
    this.programContentCounts.forEach(count => {
      total += count;
    });
    return total;
  }

  // === PROGRAM CONTENT COUNTS ===

  getProgramContentCount(programId: number): number {
    return this.programContentCounts.get(programId) || 0;
  }

  loadProgramContentCounts() {
    // Load content counts for all programs
    this.allPrograms.forEach(program => {
      this.programService.getProgramContents(program.id).subscribe({
        next: (contents) => {
          this.programContentCounts.set(program.id, contents.length);
        },
        error: (error) => {
          console.error('Error loading content count for program:', program.id, error);
          this.programContentCounts.set(program.id, 0);
        }
      });
    });
  }

  // === ADDITIONAL PROGRAM ACTIONS ===

  viewProgramStudents(program: Program) {
    // TODO: Navigate to students view filtered by program
    this.emitMessage('success', `Mostrando estudiantes de ${program.title}`);
  }

  viewProgramCertificates(program: Program) {
    // TODO: Navigate to certificates view filtered by program
    this.emitMessage('success', `Mostrando certificados de ${program.title}`);
  }

  toggleProgramStatus(program: Program) {
    this.programService.toggleProgramStatus(program.id)
      .pipe(
        catchError(error => {
          console.error('Error toggling program status:', error);
          this.emitMessage('error', 'Error cambiando estado del programa');
          return of(null);
        })
      )
      .subscribe(updatedProgram => {
        if (updatedProgram) {
          const index = this.allPrograms.findIndex(p => p.id === program.id);
          if (index > -1) {
            this.allPrograms[index] = updatedProgram;
            this.filteredPrograms = [...this.allPrograms];
          }
          const statusText = updatedProgram.status === 'active' ? 'activado' : 'inactivado';
          this.emitMessage('success', `Programa ${statusText}: ${updatedProgram.title}`);
        }
      });
  }

  deleteProgram(program: Program) {
    if (confirm(`¿Estás seguro de que quieres eliminar el programa "${program.title}"?`)) {
      this.programService.deleteProgram(program.id)
        .pipe(
          catchError(error => {
            console.error('Error deleting program:', error);
            this.emitMessage('error', 'Error eliminando programa');
            return of(null);
          })
        )
        .subscribe(() => {
          this.allPrograms = this.allPrograms.filter(p => p.id !== program.id);
          this.filteredPrograms = [...this.allPrograms];
          this.emitMessage('success', `Programa eliminado: ${program.title}`);
        });
    }
  }

  deleteContent(content: Content) {
    if (confirm(`¿Estás seguro de que quieres eliminar el contenido "${content.title}"?`)) {
      this.contentService.deleteContent(content.id)
        .pipe(
          catchError(error => {
            console.error('Error deleting content:', error);
            this.emitMessage('error', 'Error eliminando contenido');
            return of(null);
          })
        )
        .subscribe(() => {
          this.allContents = this.allContents.filter(c => c.id !== content.id);
          this.availableContents = [...this.allContents];
          this.emitMessage('success', `Contenido eliminado: ${content.title}`);
        });
    }
  }

  // === UTILITY ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  // === TRACKING ===

  trackByProgramId(index: number, item: Program): number {
    return item.id;
  }

  trackByContentId(index: number, item: Content): number {
    return item.id;
  }

  trackByProgramContentId(index: number, item: ProgramContent): number {
    return item.id;
  }
}
