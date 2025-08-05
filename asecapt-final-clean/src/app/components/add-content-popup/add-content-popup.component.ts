import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Content, CreateContentRequest, Program } from '../../services/program.service';

@Component({
  selector: 'app-add-content-popup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-content-popup.component.html',
  styleUrl: './add-content-popup.component.css'
})
export class AddContentPopupComponent implements OnInit, OnChanges {
  @Input() isVisible: boolean = false;
  @Input() availableContents: Content[] = [];
  @Input() selectedProgram: Program | null = null;
  @Input() isLoading: boolean = false;
  
  @Output() close = new EventEmitter<void>();
  @Output() addExistingContent = new EventEmitter<{contentId: number, isRequired: boolean}>();
  @Output() createNewContent = new EventEmitter<{content: CreateContentRequest, isRequired: boolean}>();

  // Form state
  contentOption: 'existing' | 'new' = 'existing';
  selectedContentId: number | null = null;
  isRequired: boolean = false;
  
  // New content form
  newContentForm: CreateContentRequest = {
    title: '',
    description: '',
    type: 'module',
    duration: '',
    content: ''
  };

  // Content type options
  contentTypes = [
    { value: 'module', label: 'Módulo' },
    { value: 'lesson', label: 'Lección' },
    { value: 'quiz', label: 'Quiz' },
    { value: 'video', label: 'Video' },
    { value: 'document', label: 'Documento' },
    { value: 'external-link', label: 'Enlace Externo' }
  ];

  ngOnInit(): void {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isVisible']) {
      console.log('🟢 AddContentPopup isVisible changed from', changes['isVisible'].previousValue, 'to', changes['isVisible'].currentValue);
      console.log('🟢 Current availableContents length:', this.availableContents?.length);
      console.log('🟢 Current selectedProgram:', this.selectedProgram?.title);
    }
  }

  onClose(): void {
    this.resetForm();
    this.close.emit();
  }

  onSubmit(): void {
    if (this.contentOption === 'existing') {
      if (this.selectedContentId) {
        this.addExistingContent.emit({
          contentId: this.selectedContentId,
          isRequired: this.isRequired
        });
      }
    } else {
      if (this.isNewContentFormValid()) {
        this.createNewContent.emit({
          content: { ...this.newContentForm },
          isRequired: this.isRequired
        });
      }
    }
  }

  onContentOptionChange(): void {
    this.selectedContentId = null;
    this.resetNewContentForm();
  }

  private resetForm(): void {
    this.contentOption = 'existing';
    this.selectedContentId = null;
    this.isRequired = false;
    this.resetNewContentForm();
  }

  private resetNewContentForm(): void {
    this.newContentForm = {
      title: '',
      description: '',
      type: 'module',
      duration: '',
      content: ''
    };
  }

  isFormValid(): boolean {
    if (this.contentOption === 'existing') {
      return this.selectedContentId !== null;
    } else {
      return this.isNewContentFormValid();
    }
  }

  private isNewContentFormValid(): boolean {
    return !!(
      this.newContentForm.title?.trim() &&
      this.newContentForm.description?.trim() &&
      this.newContentForm.type &&
      this.newContentForm.duration?.trim()
    );
  }

  // Helper to check if modal should be shown
  get showModal(): boolean {
    return this.isVisible;
  }
} 