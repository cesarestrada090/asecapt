import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, User } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { StudentCoursesComponent } from '../student-courses/student-courses.component';

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface PersonInfo {
  id: number;
  firstName: string;
  lastName: string;
  documentNumber: string;
  documentType: string;
  phoneNumber: string;
  email: string;
  gender?: string;
  birthDate?: string;
  bio?: string;
}

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, StudentCoursesComponent],
  templateUrl: './student-dashboard.component.html',
  styleUrl: './student-dashboard.component.css'
})
export class StudentDashboardComponent implements OnInit {
  // User data
  currentUser: User | null = null;
  personInfo: PersonInfo | null = null;

  // Navigation state
  activeSection: string = 'profile';
  sidebarOpen: boolean = false;

  // Password change
  passwordData: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  // Loading states
  loading: boolean = false;
  loadingProfile: boolean = true;

  // Messages
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit() {
    // Verify authentication and user type
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/virtual-classroom']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();

    // Check if user is student (type 3)
    if (!this.currentUser || this.currentUser.type !== 3) {
      this.router.navigate(['/virtual-classroom']);
      return;
    }

    this.loadPersonInfo();
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

  // === PROFILE MANAGEMENT ===

  loadPersonInfo() {
    if (!this.currentUser) return;

    this.loadingProfile = true;
    this.userService.getProfile(this.currentUser.id).subscribe({
      next: (response: any) => {
        console.log('🔍 Profile response:', response);

        // El backend retorna un UserResponseDto que contiene un objeto person
        if (response.person) {
          this.personInfo = {
            id: response.person.id,
            firstName: response.person.firstName,
            lastName: response.person.lastName,
            documentNumber: response.person.documentNumber,
            documentType: response.person.documentType || 'DNI',
            phoneNumber: response.person.phoneNumber,
            email: response.person.email,
            gender: response.person.gender,
            birthDate: response.person.birthDate,
            bio: response.person.bio
          };
        } else {
          console.warn('No person data in response:', response);
        }

        this.loadingProfile = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Error al cargar la información del perfil';
        this.loadingProfile = false;
      }
    });
  }

  // === PASSWORD CHANGE ===

  changePassword() {
    if (!this.validatePasswordForm()) {
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.userService.changePassword(this.passwordData).subscribe({
      next: (response) => {
        this.successMessage = 'Contraseña cambiada exitosamente';
        this.resetPasswordForm();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error changing password:', error);
        this.errorMessage = error.error?.message || 'Error al cambiar la contraseña';
        this.loading = false;
      }
    });
  }

  validatePasswordForm(): boolean {
    if (!this.passwordData.currentPassword) {
      this.errorMessage = 'Ingrese su contraseña actual';
      return false;
    }

    if (!this.passwordData.newPassword) {
      this.errorMessage = 'Ingrese una nueva contraseña';
      return false;
    }

    if (this.passwordData.newPassword.length < 6) {
      this.errorMessage = 'La nueva contraseña debe tener al menos 6 caracteres';
      return false;
    }

    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return false;
    }

    return true;
  }

  resetPasswordForm() {
    this.passwordData = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  }

  // === UTILITIES ===

  clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/virtual-classroom']);
    });
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'No especificado';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
  }

  getGenderText(gender?: string): string {
    if (!gender) return 'No especificado';
    return gender === 'M' ? 'Masculino' : gender === 'F' ? 'Femenino' : gender;
  }
}
