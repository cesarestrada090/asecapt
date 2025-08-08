import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnrollmentService, Enrollment } from '../../services/enrollment.service';
import { ProgramService, Program, CreateProgramRequest, UpdateProgramRequest, Content, CreateContentRequest, UpdateContentRequest, ProgramContent, AddContentToProgramRequest } from '../../services/program.service';
import { ContentService } from '../../services/content.service';
import { StudentService, Student } from '../../services/student.service';

import { catchError, switchMap } from 'rxjs/operators';
import { of, forkJoin } from 'rxjs';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.css'
})
export class CoursesComponent implements OnInit {
  @Output() message = new EventEmitter<{ type: 'success' | 'error', message: string }>();
  @Output() navigate = new EventEmitter<string>();

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

  // === INLINE PROGRAM EDITING ===
  isEditingProgram: boolean = false;
  editingProgramForm: Program | null = null;

  // === VIEW STATES ===
  currentView: string = 'list';

  // === PROGRAM STUDENTS MODAL ===
  selectedProgramForStudents: Program | null = null;
  programStudents: (Enrollment & { student?: Student })[] = [];
  isLoadingProgramStudents: boolean = false;
  editingEnrollment: { [key: number]: boolean } = {};
  enrollmentEditForm: { [key: number]: { finalGrade: number | null, attendancePercentage: number | null, status: string } } = {};
  isUpdatingEnrollment: { [key: number]: boolean } = {};
  programStudentsModalMessage: {type: 'success' | 'error', text: string} | null = null;

  constructor(
    private enrollmentService: EnrollmentService,
    private programService: ProgramService,
    private contentService: ContentService,
    private studentService: StudentService
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
      // When going back to list view, ensure we're in the courses section of dashboard
      this.navigate.emit('courses');
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
    this.isSaving = true; // Show loading state
    
    this.programService.toggleProgramStatus(program.id)
      .pipe(
        catchError(error => {
          console.error('Error toggling program status:', error);
          this.emitMessage('error', 'Error cambiando estado del programa');
          this.isSaving = false;
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
          
          // Update selected program if it's the same one being toggled
          if (this.selectedProgram && this.selectedProgram.id === program.id) {
            this.selectedProgram = updatedProgram;
          }
          
          const statusText = updatedProgram.status === 'active' ? 'activado' : 'inactivado';
          this.emitMessage('success', `Programa ${statusText}: ${updatedProgram.title}`);
        }
        this.isSaving = false; // Hide loading state
        
        // Close dropdown after action completes
        setTimeout(() => this.closeAllDropdowns(), 100);
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
          
          // Close dropdown after action completes
          setTimeout(() => this.closeAllDropdowns(), 100);
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
    console.log('Loading students for program:', program.title);
    
    this.selectedProgramForStudents = program;
    this.programStudents = [];
    this.isLoadingProgramStudents = true;
    this.programStudentsModalMessage = null; // Clear any previous messages
    
    // Show the modal
    this.showModal('programStudentsModal');
    
    // Load enrollments for this program and then get student details
    this.enrollmentService.getEnrollmentsByProgram(program.id)
      .pipe(
        switchMap(enrollments => {
          console.log('Found enrollments for program:', enrollments);
          
          if (enrollments.length === 0) {
            return of([]);
          }
          
          // Get unique student IDs
          const studentIds = [...new Set(enrollments.map(e => e.userId))];
          console.log('Loading student details for IDs:', studentIds);
          
          // Create requests for each student
          const studentRequests = studentIds.map(id => 
            this.studentService.getStudentById(id).pipe(
              catchError(error => {
                console.error(`Error loading student ${id}:`, error);
                return of(null);
              })
            )
          );
          
          // Execute all student requests in parallel
          return forkJoin(studentRequests).pipe(
            switchMap(students => {
              // Filter out null results and create student map
              const studentMap = new Map();
              students.filter(s => s !== null).forEach(student => {
                studentMap.set(student.id, student);
              });
              
              console.log('Loaded students:', studentMap);
              
              // Enrich enrollments with student data
              const enrichedEnrollments = enrollments.map(enrollment => ({
                ...enrollment,
                student: studentMap.get(enrollment.userId) || {
                  id: enrollment.userId,
                  person: {
                    firstName: 'Estudiante',
                    lastName: `${enrollment.userId}`,
                    email: 'No disponible'
                  }
                }
              }));
              
              return of(enrichedEnrollments);
            })
          );
        }),
        catchError(error => {
          console.error('Error loading program students:', error);
          this.emitMessage('error', 'Error cargando estudiantes del programa');
          this.isLoadingProgramStudents = false;
          return of([]);
        })
      )
      .subscribe(enrichedStudents => {
        console.log('Final enriched students:', enrichedStudents);
        this.programStudents = enrichedStudents;
        this.isLoadingProgramStudents = false;
      });
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
    console.log('createNewProgram() called');
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
    
    console.log('Form model reset:', this.programFormModel);
    console.log('About to show modal...');
    
    // Try immediate and with delay
    this.showModal('programModal');
    setTimeout(() => {
      this.showModal('programModal');
    }, 200);
  }

  editProgram(program: Program) {
    if (!program) {
      this.emitMessage('error', 'Programa no encontrado');
      return;
    }

    this.isEditingProgram = true;
    // Create a copy for editing
    this.editingProgramForm = { ...program };
  }

  saveProgramEdit() {
    if (!this.editingProgramForm || !this.selectedProgram) return;

    this.isSaving = true;

    const updateRequest: UpdateProgramRequest = { 
      ...this.editingProgramForm, 
      id: this.selectedProgram.id 
    };

    this.programService.updateProgram(this.selectedProgram.id, updateRequest)
      .pipe(
        catchError(error => {
          console.error('Error updating program:', error);
          this.emitMessage('error', 'Error actualizando programa');
          this.isSaving = false;
          return of(null);
        })
      )
      .subscribe(updatedProgram => {
        if (updatedProgram) {
          // Update the selected program
          this.selectedProgram = updatedProgram;
          
          // Update in the programs list
          const index = this.allPrograms.findIndex(p => p.id === updatedProgram.id);
          if (index !== -1) {
            this.allPrograms[index] = updatedProgram;
            this.filteredPrograms = [...this.allPrograms];
          }

          this.emitMessage('success', 'Programa actualizado exitosamente');
          this.cancelProgramEdit();
        }
        this.isSaving = false;
      });
  }

  cancelProgramEdit() {
    this.isEditingProgram = false;
    this.editingProgramForm = null;
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
            this.emitMessage('success', `Programa ${action}: ${course.name}`);
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

  showModal(modalId: string) {
    console.log('Attempting to show modal:', modalId);
    setTimeout(() => {
      const modalElement = document.getElementById(modalId);
      console.log('Modal element found:', modalElement);
      
      if (modalElement) {
        try {
          // Check if Bootstrap is available
          const bootstrapAvailable = typeof (window as any).bootstrap !== 'undefined';
          console.log('Bootstrap available:', bootstrapAvailable);
          
          if (bootstrapAvailable) {
            console.log('Using Bootstrap modal');
            const modal = new (window as any).bootstrap.Modal(modalElement, {
              backdrop: true,
              keyboard: true,
              focus: true
            });
            modal.show();
          } else {
            console.log('Using manual modal fallback');
            // Fallback: manually show modal using CSS classes
            modalElement.classList.add('show', 'd-block');
            modalElement.style.display = 'block';
            modalElement.style.opacity = '1';
            modalElement.style.visibility = 'visible';
            modalElement.setAttribute('aria-hidden', 'false');
            modalElement.setAttribute('aria-modal', 'true');
            
            // Add backdrop if it doesn't exist
            let backdrop = document.getElementById(modalId + '-backdrop');
            if (!backdrop) {
              backdrop = document.createElement('div');
              backdrop.className = 'modal-backdrop fade show';
              backdrop.id = modalId + '-backdrop';
              backdrop.style.zIndex = '1040';
              document.body.appendChild(backdrop);
            }
            
            document.body.classList.add('modal-open');
            document.body.style.overflow = 'hidden';
            
            // Focus on modal
            modalElement.focus();
          }
          console.log('Modal should now be visible');
        } catch (error) {
          console.error('Error showing modal:', error);
          // Emergency fallback method
          console.log('Using emergency fallback');
          modalElement.classList.add('show', 'd-block');
          modalElement.style.display = 'block !important';
          modalElement.style.opacity = '1';
          modalElement.style.zIndex = '1050';
          modalElement.style.position = 'fixed';
          modalElement.style.top = '50%';
          modalElement.style.left = '50%';
          modalElement.style.transform = 'translate(-50%, -50%)';
          modalElement.style.width = '90%';
          modalElement.style.maxWidth = '800px';
        }
      } else {
        console.error('Modal element not found:', modalId);
      }
    }, 100); // Increased timeout to ensure DOM is ready
  }

  closeModal(modalId: string) {
    console.log('Attempting to close modal:', modalId);
    setTimeout(() => {
      const modalElement = document.getElementById(modalId);
      if (modalElement) {
        try {
          // Try to use Bootstrap if available
          const bootstrapAvailable = typeof (window as any).bootstrap !== 'undefined';
          
          if (bootstrapAvailable) {
            const modal = (window as any).bootstrap.Modal.getInstance(modalElement);
            if (modal) {
              modal.hide();
            } else {
              // Manually hide if no instance
              modalElement.classList.remove('show');
              modalElement.style.display = 'none';
            }
          } else {
            // Fallback: manually hide modal
            modalElement.classList.remove('show', 'd-block');
            modalElement.style.display = 'none';
            modalElement.style.opacity = '';
            modalElement.style.visibility = '';
            modalElement.style.zIndex = '';
            modalElement.style.position = '';
            modalElement.style.top = '';
            modalElement.style.left = '';
            modalElement.style.transform = '';
            modalElement.style.width = '';
            modalElement.style.maxWidth = '';
            modalElement.setAttribute('aria-hidden', 'true');
            modalElement.removeAttribute('aria-modal');
            
            // Remove backdrop
            const backdrop = document.getElementById(modalId + '-backdrop');
            if (backdrop) {
              backdrop.remove();
            }
            
            document.body.classList.remove('modal-open');
            document.body.style.overflow = '';
          }
        } catch (error) {
          console.error('Error closing modal:', error);
          // Emergency fallback method
          modalElement.classList.remove('show', 'd-block');
          modalElement.style.display = 'none';
          modalElement.style.opacity = '';
          modalElement.style.visibility = '';
        }
      }
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
      case 'course': return 'Programa';
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
      { value: 'course', label: 'Programas' },
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

  // === UTILITY METHODS ===

  closeAllDropdowns() {
    // Close all Bootstrap dropdowns
    const dropdowns = document.querySelectorAll('.dropdown-menu.show');
    dropdowns.forEach(dropdown => {
      const dropdownInstance = (window as any).bootstrap?.Dropdown?.getInstance(dropdown.previousElementSibling);
      if (dropdownInstance) {
        dropdownInstance.hide();
      } else {
        // Fallback: remove show class manually
        dropdown.classList.remove('show');
        if (dropdown.previousElementSibling) {
          dropdown.previousElementSibling.setAttribute('aria-expanded', 'false');
        }
      }
    });
  }

  // === PROGRAM STUDENTS MODAL HELPER METHODS ===

  trackByEnrollmentId(index: number, enrollment: any): number {
    return enrollment.id || index;
  }

  getStudentFullName(student: Student | undefined): string {
    if (!student || !student.person) {
      return 'Estudiante no encontrado';
    }
    return `${student.person.firstName} ${student.person.lastName}`;
  }

  getEnrollmentStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'badge bg-success';
      case 'enrolled':
        return 'badge bg-primary';
      case 'in_progress':
        return 'badge bg-warning';
      case 'suspended':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  getEnrollmentStatusText(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'Completado';
      case 'enrolled':
        return 'Matriculado';
      case 'in_progress':
        return 'En Progreso';
      case 'suspended':
        return 'Suspendido';
      default:
        return 'Desconocido';
    }
  }

  getEnrollmentProgress(enrollment: any): number {
    if (enrollment.status === 'completed') {
      return 100;
    }
    
    if (enrollment.attendancePercentage) {
      return Math.round(enrollment.attendancePercentage);
    }
    
    // Estimate progress based on time if start date is available
    if (enrollment.startDate) {
      try {
        const startDate = new Date(enrollment.startDate);
        const currentDate = new Date();
        const daysPassed = Math.floor((currentDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        
        // Rough estimation: assume 30 days for most programs
        const estimatedDays = 30;
        const progress = Math.min(Math.max((daysPassed / estimatedDays) * 100, 0), 95);
        return Math.round(progress);
      } catch (error) {
        console.error('Error calculating progress:', error);
      }
    }
    
    return enrollment.status === 'enrolled' ? 15 : 0;
  }

  getProgressBarClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
        return 'bg-success';
      case 'enrolled':
        return 'bg-primary';
      case 'in_progress':
        return 'bg-warning';
      case 'suspended':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
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

  formatDate(dateString: string | Date | null | undefined): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES');
    } catch {
      return 'Fecha inválida';
    }
  }

  // === ENROLLMENT EDITING METHODS ===

  startEditingEnrollment(enrollment: any) {
    console.log('Starting to edit enrollment:', enrollment);
    
    // Initialize edit form with current values
    this.enrollmentEditForm[enrollment.id] = {
      finalGrade: enrollment.finalGrade,
      attendancePercentage: enrollment.attendancePercentage,
      status: enrollment.status
    };
    
    // Mark as editing
    this.editingEnrollment[enrollment.id] = true;
    
    console.log('Edit form initialized:', this.enrollmentEditForm[enrollment.id]);
  }

  cancelEditingEnrollment(enrollmentId: number) {
    console.log('Canceling edit for enrollment:', enrollmentId);
    
    // Remove from editing state
    delete this.editingEnrollment[enrollmentId];
    delete this.enrollmentEditForm[enrollmentId];
    delete this.isUpdatingEnrollment[enrollmentId];
  }

  saveEnrollmentChanges(enrollment: any) {
    const enrollmentId = enrollment.id;
    const formData = this.enrollmentEditForm[enrollmentId];
    
    if (!formData) {
      console.error('No form data found for enrollment:', enrollmentId);
      return;
    }

    console.log('Saving enrollment changes:', { enrollmentId, formData });
    
    // Validate data
    if (formData.finalGrade !== null && (formData.finalGrade < 0 || formData.finalGrade > 100)) {
      this.showProgramStudentsModalMessage('error', 'La nota debe estar entre 0 y 100');
      return;
    }
    
    if (formData.attendancePercentage !== null && (formData.attendancePercentage < 0 || formData.attendancePercentage > 100)) {
      this.showProgramStudentsModalMessage('error', 'La asistencia debe estar entre 0% y 100%');
      return;
    }

    // Mark as updating
    this.isUpdatingEnrollment[enrollmentId] = true;

    // Prepare update request
    const updateRequest: any = {
      finalGrade: formData.finalGrade,
      attendancePercentage: formData.attendancePercentage,
      status: formData.status as 'enrolled' | 'in_progress' | 'completed' | 'suspended'
    };

    // If marking as completed, set completion date
    if (formData.status === 'completed' && enrollment.status !== 'completed') {
      updateRequest.completionDate = new Date().toISOString().split('T')[0];
    }

    console.log('Sending update request:', updateRequest);

    // Call the API to update enrollment
    this.enrollmentService.updateEnrollment(enrollmentId, updateRequest)
      .pipe(
        catchError(error => {
          console.error('Error updating enrollment:', error);
          this.showProgramStudentsModalMessage('error', 'Error actualizando la matrícula');
          this.isUpdatingEnrollment[enrollmentId] = false;
          return of(null);
        })
      )
      .subscribe(updatedEnrollment => {
        if (updatedEnrollment) {
          console.log('Enrollment updated successfully:', updatedEnrollment);
          
          // Update the local data
          const enrollmentIndex = this.programStudents.findIndex(e => e.id === enrollmentId);
          if (enrollmentIndex !== -1) {
            this.programStudents[enrollmentIndex] = {
              ...this.programStudents[enrollmentIndex],
              ...updatedEnrollment
            };
          }
          
          // Clean up editing state
          this.cancelEditingEnrollment(enrollmentId);
          
          // Show success message in the modal
          this.showProgramStudentsModalMessage('success', 'Matrícula actualizada exitosamente');
        }
        
        this.isUpdatingEnrollment[enrollmentId] = false;
      });
  }

  private showProgramStudentsModalMessage(type: 'success' | 'error', text: string) {
    this.programStudentsModalMessage = { type, text };
    // Auto-hide message after 4 seconds
    setTimeout(() => {
      this.programStudentsModalMessage = null;
    }, 4000);
  }
}
