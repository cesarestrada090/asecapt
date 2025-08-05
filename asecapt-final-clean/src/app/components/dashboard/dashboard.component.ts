import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { GenerateCertificatesComponent } from '../generate-certificates/generate-certificates.component';
import { SearchCertificatesComponent } from '../search-certificates/search-certificates.component';
import { StudentsComponent } from '../students/students.component';
import { CoursesComponent } from '../courses/courses.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    GenerateCertificatesComponent,
    SearchCertificatesComponent,
    StudentsComponent,
    CoursesComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  // Navigation state
  activeSection: string = 'generate-certificates';
  sidebarOpen: boolean = false;

  // Messages
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private router: Router) {}

  ngOnInit() {
    // Initialize dashboard
  }

  // === NAVIGATION ===

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  setActiveSection(section: string) {
    this.activeSection = section;
    this.sidebarOpen = false;
    this.clearMessages();
  }

  getSectionTitle(): string {
    switch (this.activeSection) {
      case 'generate-certificates': return 'Generar Certificados';
      case 'search-certificates': return 'Buscar Certificados';
      case 'students': return 'Gestión de Alumnos';
      case 'courses': return 'Gestión de Cursos';
      default: return 'Dashboard Administrativo';
    }
  }

  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'generate-certificates': return 'Genera certificados con QR de verificación para estudiantes que han completado programas';
      case 'search-certificates': return 'Busca y gestiona certificados existentes por estudiante, curso o número de certificado';
      case 'students': return 'Visualiza y gestiona información de estudiantes que han completado programas';
      case 'courses': return 'Administra cursos, especializaciones y programas disponibles';
      default: return 'Panel de administración ASECAPT - Gestión completa de certificados y usuarios';
    }
  }

  // === UTILITY ===

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  logout() {
    // Clear any stored auth data
    localStorage.removeItem('admin_token');
    sessionStorage.clear();
    
    // Navigate to login
    this.router.navigate(['/virtual-classroom']);
  }

  // === MESSAGE HANDLING ===

  onMessage(event: { type: 'success' | 'error', message: string }) {
    this.clearMessages();
    if (event.type === 'success') {
      this.successMessage = event.message;
    } else {
      this.errorMessage = event.message;
    }

    // Auto-clear messages after 5 seconds
    setTimeout(() => {
      this.clearMessages();
    }, 5000);
  }
} 