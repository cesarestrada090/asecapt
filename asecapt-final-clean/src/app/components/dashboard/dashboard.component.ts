import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StudentsComponent } from '../students/students.component';
import { CoursesComponent } from '../courses/courses.component';
import { CertificatesComponent } from '../certificates/certificates.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    StudentsComponent,
    CoursesComponent,
    CertificatesComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  // Navigation state
  activeSection: string = 'courses'; // Changed default to courses
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
      case 'students': return 'Gestión de Alumnos';
      case 'courses': return 'Gestión de Cursos';
      case 'certificates': return 'Gestión de Certificados';
      default: return 'Dashboard Administrativo';
    }
  }

  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'students': return 'Visualiza y gestiona información de estudiantes que han completado programas';
      case 'courses': return 'Administra cursos, especializaciones y programas disponibles';
      case 'certificates': return 'Gestiona certificados para estudiantes con cursos completados - subir, descargar y generar QR';
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