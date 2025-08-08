import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StudentsComponent } from '../students/students.component';
import { CoursesComponent } from '../courses/courses.component';
import { CertificatesComponent } from '../certificates/certificates.component';
import { AuthService } from '../../services/auth.service';

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

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Initialize dashboard and verify authentication
    if (!this.authService.isAuthenticated() || !this.authService.isAdmin()) {
      this.router.navigate(['/virtual-classroom']);
    }
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
      case 'courses': return 'Gestión de Programas';
      case 'certificates': return 'Gestión de Certificados';
      default: return 'Dashboard Administrativo';
    }
  }

  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'students': return 'Visualiza y gestiona información de estudiantes que han completado programas';
      case 'courses': return 'Administra programas, especializaciones y cursos disponibles';
      case 'certificates': return 'Gestiona certificados para estudiantes con programas completados - subir, descargar y generar QR';
      default: return 'Panel de administración ASECAPT - Gestión completa de certificados y usuarios';
    }
  }

  // === UTILITY ===

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
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

  // === AUTHENTICATION ===

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logout successful');
        this.router.navigate(['/virtual-classroom']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        // Navigate anyway
        this.router.navigate(['/virtual-classroom']);
      }
    });
  }

  getCurrentUser() {
    return this.authService.getCurrentUser();
  }

  getUserFullName(): string {
    return this.authService.getUserFullName();
  }

  getUserTypeText(): string {
    return this.authService.getUserTypeText();
  }
} 