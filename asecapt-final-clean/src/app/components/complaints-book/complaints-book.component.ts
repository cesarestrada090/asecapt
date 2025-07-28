import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Complaint {
  id: string;
  type: 'reclamo' | 'queja' | 'sugerencia';
  name: string;
  email: string;
  phone: string;
  document: string;
  description: string;
  date: Date;
  status: 'pendiente' | 'en_proceso' | 'resuelto';
  response?: string;
}

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
  isSubmitting: boolean = false;
  submitSuccess: boolean = false;
  submittedId: string = '';

  setActiveTab(tab: 'new' | 'track') {
    this.activeTab = tab;
    this.resetForms();
  }

  onSubmitComplaint() {
    if (this.isValidForm()) {
      this.isSubmitting = true;
      
      // Simular envío (aquí iría la lógica real)
      setTimeout(() => {
        this.submittedId = this.generateComplaintId();
        this.submitSuccess = true;
        this.isSubmitting = false;
        this.resetComplaintForm();
      }, 2000);
    }
  }

  onSearchComplaint() {
    if (this.searchId.trim()) {
      this.isSearching = true;
      
      // Simular búsqueda (aquí iría la lógica real)
      setTimeout(() => {
        // Simular resultado encontrado o no encontrado
        const found = Math.random() > 0.3;
        
        if (found) {
          this.searchResult = {
            id: this.searchId,
            type: 'reclamo',
            name: 'María Elena González Vargas',
            email: 'maria.gonzalez@email.com',
            phone: '+51 987 654 321',
            document: '12345678',
            description: 'Problema con el acceso a la plataforma virtual del curso de SSOMA',
            date: new Date('2024-01-15'),
            status: 'en_proceso',
            response: 'Su reclamo está siendo revisado por nuestro equipo técnico. Le responderemos en un plazo máximo de 48 horas.'
          };
        } else {
          this.searchResult = null;
        }
        
        this.isSearching = false;
      }, 1500);
    }
  }

  resetForms() {
    this.resetComplaintForm();
    this.searchId = '';
    this.searchResult = null;
    this.submitSuccess = false;
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

  resetSearch() {
    this.searchId = '';
    this.searchResult = null;
  }

  isValidForm(): boolean {
    return !!(
      this.complaintForm.name.trim() &&
      this.complaintForm.email.trim() &&
      this.complaintForm.document.trim() &&
      this.complaintForm.description.trim()
    );
  }

  private generateComplaintId(): string {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    return `LR-${year}${month}${day}-${random}`;
  }

  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'pendiente': 'Pendiente',
      'en_proceso': 'En Proceso',
      'resuelto': 'Resuelto'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const classMap: { [key: string]: string } = {
      'pendiente': 'badge-warning',
      'en_proceso': 'badge-info',
      'resuelto': 'badge-success'
    };
    return classMap[status] || 'badge-secondary';
  }
}
