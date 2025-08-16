import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplaintService, Complaint, CreateComplaintRequest } from '../../services/complaint.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-complaints-book',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './complaints-book.component.html',
  styleUrl: './complaints-book.component.css'
})
export class ComplaintsBookComponent {
  activeTab: 'new' | 'track' = 'new';

  // Formulario de nuevo reclamo
  complaintForm = {
    type: 'reclamo',
    name: '',
    email: '',
    phone: '',
    document: '',
    description: ''
  };

  // Búsqueda de reclamo
  searchId: string = '';
  searchResult: Complaint | null = null;
  isSearching: boolean = false;
  hasSearched: boolean = false; // Nueva variable para rastrear si se hizo una búsqueda
  isSubmitting: boolean = false;
  submitSuccess: boolean = false;
  submittedId: string = '';
  errorMessage: string = '';
  searchError: string = '';

  constructor(private complaintService: ComplaintService) {}

  setActiveTab(tab: 'new' | 'track') {
    this.activeTab = tab;
    this.resetForms();
  }

  onSubmitComplaint() {
    if (this.isValidForm()) {
      this.isSubmitting = true;
      this.errorMessage = '';

      const complaintRequest: CreateComplaintRequest = {
        type: this.complaintForm.type,
        name: this.complaintForm.name,
        email: this.complaintForm.email,
        phone: this.complaintForm.phone,
        document: this.complaintForm.document,
        description: this.complaintForm.description
      };

      this.complaintService.createComplaint(complaintRequest)
        .pipe(
          catchError(error => {
            console.error('Error creating complaint:', error);
            this.errorMessage = 'Error al enviar el reclamo. Por favor, inténtelo nuevamente.';
            this.isSubmitting = false;
            return of(null);
          })
        )
        .subscribe(response => {
          if (response) {
            this.submittedId = response.complaintNumber;
            this.submitSuccess = true;
            this.resetComplaintForm();
          }
          this.isSubmitting = false;
        });
    }
  }

  onSearchComplaint() {
    if (this.searchId.trim()) {
      this.isSearching = true;
      this.hasSearched = true; // Marcar que se realizó una búsqueda
      this.searchError = '';
      this.searchResult = null;

      this.complaintService.getComplaintByNumber(this.searchId.trim())
        .pipe(
          catchError(error => {
            console.error('Error searching complaint:', error);
            if (error.status === 404) {
              this.searchError = 'No se encontró ningún reclamo con ese número.';
            } else {
              this.searchError = 'Error al buscar el reclamo. Por favor, inténtelo nuevamente.';
            }
            this.isSearching = false;
            return of(null);
          })
        )
        .subscribe(response => {
          if (response) {
            this.searchResult = response;
          }
          this.isSearching = false;
        });
    }
  }

  resetForms() {
    this.resetComplaintForm();
    this.searchId = '';
    this.searchResult = null;
    this.hasSearched = false; // Resetear también hasSearched
    this.submitSuccess = false;
    this.errorMessage = '';
    this.searchError = '';
  }

  resetComplaintForm() {
    this.complaintForm = {
      type: 'reclamo',
      name: '',
      email: '',
      phone: '',
      document: '',
      description: ''
    };
  }

  isValidForm(): boolean {
    return !!(
      this.complaintForm.name.trim() &&
      this.complaintForm.email.trim() &&
      this.complaintForm.description.trim()
    );
  }

  generateComplaintId(): string {
    return 'REC-' + Date.now().toString();
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'pendiente': return 'Pendiente';
      case 'en_proceso': return 'En Proceso';
      case 'resuelto': return 'Resuelto';
      default: return status;
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'pendiente': return 'badge bg-warning';
      case 'en_proceso': return 'badge bg-info';
      case 'resuelto': return 'badge bg-success';
      default: return 'badge bg-secondary';
    }
  }

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return 'Fecha no disponible';
    }
  }

  resetSearch() {
    this.searchId = '';
    this.searchResult = null;
    this.hasSearched = false; // Resetear también hasSearched
    this.searchError = '';
  }
}
