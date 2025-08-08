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
          this.isLoading = false;
          
          if (response.success) {
            // Login exitoso - redirigir al dashboard administrativo
            console.log('Login exitoso - redirigiendo al dashboard administrativo');
            this.router.navigate(['/dashboard']);
          } else {
            // Mostrar error específico
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
