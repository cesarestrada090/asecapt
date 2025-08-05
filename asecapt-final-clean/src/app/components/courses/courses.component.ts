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
    topic: '',
    topicNumber: null
  };
  editingContent: Content | null = null;

  // === PROGRAM CONTENT MANAGEMENT ===
  selectedProgram: Program | null = null;
  programContents: ProgramContent[] = [];
  isLoadingProgramContents: boolean = false;

  // === ADD CONTENT WITH OPTIONS ===
  selectedContentToAdd: number | null = null;
  newContentIsRequired: boolean = false;
  showAddContentForm: boolean = false;
  
  // === ADD CONTENT POPUP ===
  showAddContentPopup: boolean = false;

  // === SIMPLE ADD CONTENT MODAL ===
  showSimpleAddModal: boolean = false;
  simpleAddForm = {
    isRequired: false,
    newContent: {
      title: '',
      description: '',
      type: 'module' as string,
      duration: '',
      content: '',
      topic: '',
      topicNumber: null as number | null
    }
  };

  // === INLINE CONTENT EDITING ===
  editingProgramContent: { programContentId: number, content: Content } | null = null;
  editingContentForm: Content | null = null;

  // === VIEW STATES ===
  currentView: string = 'list';

  constructor(
    private enrollmentService: EnrollmentService,
    private programService: ProgramService,
    private contentService: ContentService
  ) {}

  ngOnInit(): void {
    this.loadPrograms();
    this.loadContents();
    // Removed duplicate loadProgramContentCounts() - it's called in loadPrograms()
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

    const request = {
      programId: this.selectedProgram.id,
      contentId: contentId,
      orderIndex: this.programContents.length + 1,
      isRequired: false // Changed from true to false - content is optional by default
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

  addContentToProgramWithOptions() {
    if (!this.selectedProgram || !this.selectedContentToAdd) return;

    const request = {
      programId: this.selectedProgram.id,
      contentId: this.selectedContentToAdd,
      orderIndex: this.programContents.length + 1,
      isRequired: this.newContentIsRequired
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
          // Reset form and collapse
          this.selectedContentToAdd = null;
          this.newContentIsRequired = false;
          this.showAddContentForm = false;
        }
      });
  }

  toggleAddContentForm() {
    this.showAddContentForm = !this.showAddContentForm;
    // Reset form when closing
    if (!this.showAddContentForm) {
      this.selectedContentToAdd = null;
      this.newContentIsRequired = false;
    }
  }

  // === ADD CONTENT POPUP METHODS ===
  
  openAddContentPopup() {
    console.log('🔵 Opening add content popup');
    console.log('🔵 selectedProgram:', this.selectedProgram);
    console.log('🔵 unassignedContents:', this.unassignedContents.length);
    
    if (!this.selectedProgram) {
      console.error('❌ No program selected');
      this.emitMessage('error', 'Debe seleccionar un programa primero');
      return;
    }
    
    this.showAddContentPopup = true;
    console.log('🔵 showAddContentPopup set to:', this.showAddContentPopup);
  }

  closeAddContentPopup() {
    this.showAddContentPopup = false;
  }

  onAddExistingContent(event: {contentId: number, isRequired: boolean}) {
    if (!this.selectedProgram) return;

    const request = {
      programId: this.selectedProgram.id,
      contentId: event.contentId,
      orderIndex: this.programContents.length + 1,
      isRequired: event.isRequired
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
          this.closeAddContentPopup();
          // Reload available contents to update the list
          this.loadContents();
        }
      });
  }

  onCreateAndAddContent(event: {content: CreateContentRequest, isRequired: boolean}) {
    if (!this.selectedProgram) return;

    // First create the content
    this.contentService.createContent(event.content)
      .pipe(
        catchError(error => {
          console.error('Error creating content:', error);
          this.emitMessage('error', 'Error creando contenido');
          return of(null);
        })
      )
      .subscribe(newContent => {
        if (newContent) {
          // Add to local contents arrays
          this.allContents.unshift(newContent);
          this.availableContents = [...this.allContents];
          
          // Now add it to the program
          const request = {
            programId: this.selectedProgram!.id,
            contentId: newContent.id,
            orderIndex: this.programContents.length + 1,
            isRequired: event.isRequired
          };

          this.programService.addContentToProgram(request)
            .pipe(
              catchError(error => {
                console.error('Error adding new content to program:', error);
                this.emitMessage('error', 'Error agregando el nuevo contenido al programa');
                return of(null);
              })
            )
            .subscribe(programContent => {
              if (programContent) {
                this.programContents.push(programContent);
                this.emitMessage('success', 'Contenido creado y agregado exitosamente');
                this.closeAddContentPopup();
              }
            });
        }
      });
  }

  // === SIMPLE ADD CONTENT MODAL METHODS ===

  showSimpleAddContentModal() {
    if (!this.selectedProgram) {
      this.emitMessage('error', 'Debe seleccionar un programa primero');
      return;
    }
    
    // Reset form
    this.resetSimpleAddForm();
    
    // Show modal using Angular state
    this.showSimpleAddModal = true;
  }

  resetSimpleAddForm() {
    this.simpleAddForm = {
      isRequired: false,
      newContent: {
        title: '',
        description: '',
        type: 'module',
        duration: '',
        content: '',
        topic: '',
        topicNumber: null
      }
    };
  }

  isSimpleAddFormValid(): boolean {
    return !!(
      this.simpleAddForm.newContent.title?.trim() &&
      this.simpleAddForm.newContent.type &&
      this.simpleAddForm.newContent.duration?.trim()
    );
  }

  submitSimpleAddForm() {
    if (!this.selectedProgram || !this.isSimpleAddFormValid()) {
      return;
    }

    this.isSaving = true;
    // Always create new content and add to program
    this.createAndAddNewContent();
  }



  private createAndAddNewContent() {
    if (!this.selectedProgram) return;

    // Prepare content data with defaults
    const contentData = {
      ...this.simpleAddForm.newContent,
      topic: this.simpleAddForm.newContent.topic?.trim() || this.simpleAddForm.newContent.title || 'Contenido'
    };

    // First create the new content
    this.contentService.createContent(contentData)
      .pipe(
        catchError(error => {
          console.error('Error creating content:', error);
          this.emitMessage('error', 'Error creando contenido');
          this.isSaving = false;
          return of(null);
        })
      )
      .subscribe(newContent => {
        if (newContent && this.selectedProgram) {
          // Add the newly created content to available contents
          this.allContents.push(newContent);
          this.availableContents.push(newContent);

          // Now add it to the program
          const request = {
            programId: this.selectedProgram.id,
            contentId: newContent.id,
            orderIndex: this.programContents.length + 1,
            isRequired: this.simpleAddForm.isRequired
          };

          this.programService.addContentToProgram(request)
            .pipe(
              catchError(error => {
                console.error('Error adding new content to program:', error);
                this.emitMessage('error', 'Error agregando contenido al programa');
                this.isSaving = false;
                return of(null);
              })
            )
            .subscribe(programContent => {
              if (programContent) {
                this.programContents.push(programContent);
                this.emitMessage('success', 'Contenido creado y agregado exitosamente');
                this.closeSimpleAddModal();
              }
              this.isSaving = false;
            });
        } else {
          this.isSaving = false;
        }
      });
  }

  closeSimpleAddModal() {
    this.showSimpleAddModal = false;
    this.resetSimpleAddForm();
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

  // === INLINE CONTENT EDITING ===

  editProgramContent(contentId: number) {
    const content = this.getContentById(contentId);
    if (!content) {
      this.emitMessage('error', 'Contenido no encontrado');
      return;
    }

    this.editingProgramContent = { programContentId: contentId, content: content };
    // Create a copy for editing
    this.editingContentForm = { ...content };
  }

  saveProgramContentEdit() {
    if (!this.editingContentForm || !this.editingProgramContent) return;

    this.isSaving = true;
    const updateRequest: UpdateContentRequest = { 
      ...this.editingContentForm, 
      id: this.editingContentForm.id 
    };

    this.contentService.updateContent(this.editingContentForm.id, updateRequest)
      .pipe(
        catchError(error => {
          console.error('Error updating content:', error);
          this.emitMessage('error', 'Error actualizando contenido');
          return of(null);
        })
      )
      .subscribe(updatedContent => {
        this.isSaving = false;
        if (updatedContent) {
          // Update in allContents array
          const index = this.allContents.findIndex(c => c.id === updatedContent.id);
          if (index > -1) {
            this.allContents[index] = updatedContent;
            this.availableContents = [...this.allContents];
          }
          
          this.emitMessage('success', 'Contenido actualizado exitosamente');
          this.cancelProgramContentEdit();
        }
      });
  }

  cancelProgramContentEdit() {
    this.editingProgramContent = null;
    this.editingContentForm = null;
  }

  isEditingProgramContent(contentId: number): boolean {
    return this.editingProgramContent?.programContentId === contentId;
  }

  // === PROGRAM ACTIONS ===

  viewProgramDetails(program: Program) {
    console.log('🔍 Viewing program details for:', program.title);
    this.selectedProgram = program;
    this.currentView = 'details';
    this.loadProgramContents(program.id);

  }

  // === VIEW NAVIGATION ===

  setCurrentView(view: string) {
    this.currentView = view;
    if (view === 'list') {
      this.selectedProgram = null;
    }
  }

  isListView(): boolean {
    return this.currentView === 'list';
  }

  isDetailsView(): boolean {
    return this.currentView === 'details';
  }

  isContentAlreadyAssigned(contentId: number): boolean {
    return this.programContents.some(pc => pc.contentId === contentId);
  }

  getContentById(contentId: number): Content | undefined {
    return this.allContents.find(content => content.id === contentId);
  }

  get unassignedContents(): Content[] {
    return this.availableContents.filter(content => !this.isContentAlreadyAssigned(content.id));
  }

  isContentLibraryView(): boolean {
    return this.currentView === 'content-library';
  }

  // === PROGRAM MANAGEMENT ACTIONS ===

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

  viewProgramStudents(program: Program) {
    // TODO: Navigate to students view filtered by program
    this.emitMessage('success', `Mostrando estudiantes de ${program.title}`);
  }

  viewProgramCertificates(program: Program) {
    // TODO: Navigate to certificates view filtered by program
    this.emitMessage('success', `Mostrando certificados de ${program.title}`);
  }

  // Toggle individual content isRequired status
  toggleContentRequiredStatus(contentId: number) {
    if (!this.selectedProgram) return;
    
    this.programService.toggleContentRequiredStatus(this.selectedProgram.id, contentId)
      .pipe(
        catchError(error => {
          console.error('Error toggling content required status:', error);
          this.emitMessage('error', 'Error cambiando estado del contenido');
          return of(null);
        })
      )
      .subscribe(updatedProgramContent => {
        if (updatedProgramContent) {
          // Update the local programContents array
          const index = this.programContents.findIndex(pc => pc.contentId === contentId);
          if (index > -1) {
            this.programContents[index] = updatedProgramContent;
          }
          
          const statusText = updatedProgramContent.isRequired ? 'obligatorio' : 'opcional';
          this.emitMessage('success', `Contenido marcado como ${statusText}`);
        }
      });
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
      content: ''
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

  // === MISSING UTILITY METHODS ===

  private emitMessage(type: 'success' | 'error', message: string) {
    this.message.emit({ type, message });
  }

  loadProgramContentCounts() {
    // OPTIMIZED: Load content counts for all programs in single request
    this.programService.getAllProgramContentCounts()
      .pipe(
        catchError(error => {
          console.error('Error loading program content counts:', error);
          // Initialize empty counts on error
          this.allPrograms.forEach(program => {
            this.programContentCounts.set(program.id, 0);
          });
          return of({});
        })
      )
      .subscribe(counts => {
        // Clear existing counts
        this.programContentCounts.clear();
        
        // Set counts from server response
        Object.entries(counts).forEach(([programId, count]) => {
          this.programContentCounts.set(Number(programId), Number(count));
        });
        
        console.log('✅ Loaded all content counts in single request:', this.programContentCounts);
      });
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

  getProgramContentCount(programId: number): number {
    return this.programContentCounts.get(programId) || 0;
  }

  // Get content summary for display in program cards
  getContentSummary(programId: number): string {
    const totalCount = this.getProgramContentCount(programId);
    if (totalCount === 0) return 'Sin contenidos';
    return `${totalCount} contenido${totalCount !== 1 ? 's' : ''}`;
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

  get availableContentTypes() {
    return [
      { value: '', label: 'Todos los tipos' },
      { value: 'module', label: 'Módulo' },
      { value: 'lesson', label: 'Lección' },
      { value: 'quiz', label: 'Quiz' },
      { value: 'video', label: 'Video' },
      { value: 'document', label: 'Documento' },
      { value: 'external-link', label: 'Enlace Externo' }
    ];
  }
}
