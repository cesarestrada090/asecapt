import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-virtual-classroom',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './virtual-classroom.component.html',
  styleUrl: './virtual-classroom.component.css'
})
export class VirtualClassroomComponent {
  loginForm = {
    username: '',
    password: ''
  };

  isLoading: boolean = false;
  loginError: string = '';
  showPassword: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  onLogin() {
    if (this.isValidForm()) {
      this.isLoading = true;
      this.loginError = '';

      const credentials: LoginRequest = {
        username: this.loginForm.username.trim(),
        password: this.loginForm.password
      };

      this.authService.login(credentials).subscribe({
        next: (response) => {
          console.log('🔍 Login response received:', response);
          this.isLoading = false;

          if (response.success) {
            // Login exitoso - redirigir según el tipo de usuario
            console.log('✅ Login exitoso - redirigiendo según tipo de usuario');
            const user = this.authService.getCurrentUser();
            console.log('👤 Current user after login:', user);

            if (user?.type === 1) {
              // Admin - Dashboard administrativo
              console.log('🔄 Redirecting admin to /dashboard');
              this.router.navigate(['/dashboard']);
            } else if (user?.type === 3) {
              // Estudiante - Dashboard de estudiante
              console.log('🔄 Redirecting student to /student-dashboard');
              this.router.navigate(['/student-dashboard']).then(
                (success: boolean) => {
                  console.log('✅ Navigation success:', success);
                  if (!success) {
                    console.log('❌ Navigation failed, trying alternative route');
                    window.location.href = '/student-dashboard';
                  }
                }
              ).catch(error => {
                console.error('❌ Navigation error:', error);
                console.log('🔄 Trying direct navigation');
                window.location.href = '/student-dashboard';
              });
            } else if (user?.type === 2) {
              // Instructor - Por ahora al dashboard de estudiante (pueden usar el mismo)
              console.log('🔄 Redirecting instructor to /student-dashboard');
              this.router.navigate(['/student-dashboard']);
            } else {
              // Tipo no reconocido - redirigir a inicio
              console.log('⚠️ Unknown user type, redirecting to home');
              this.router.navigate(['/']);
            }
          } else {
            // Mostrar error específico
            console.log('❌ Login failed:', response);
            this.loginError = this.getErrorMessage(response.errorCode, response.message);
          }
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Login error:', error);
          this.loginError = 'Error de conexión. Por favor, intente nuevamente.';
        }
      });
    }
  }

  private getErrorMessage(errorCode?: string, message?: string): string {
    switch (errorCode) {
      case 'INVALID_CREDENTIALS':
        return 'Usuario o contraseña incorrectos';
      case 'USER_INACTIVE':
        return 'La cuenta de usuario está inactiva. Contacte al administrador';
      case 'INSUFFICIENT_PRIVILEGES':
        return 'Acceso denegado. Se requieren privilegios de administrador';
      case 'INVALID_INPUT':
        return 'Por favor ingrese usuario y contraseña';
      default:
        return message || 'Error durante el inicio de sesión. Intente nuevamente';
    }
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  resetForm() {
    this.loginForm = {
      username: '',
      password: ''
    };
    this.loginError = '';
  }

  isValidForm(): boolean {
    return !!(this.loginForm.username.trim() && this.loginForm.password.trim());
  }
}
