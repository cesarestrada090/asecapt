import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  constructor(private router: Router) {}

  onLogin() {
    if (this.isValidForm()) {
      this.isLoading = true;
      this.loginError = '';
      
      // Simular proceso de login
      setTimeout(() => {
        // Simular validación de credenciales de administrador
        if (this.loginForm.username === 'admin@asecapt.com' && this.loginForm.password === 'admin2024') {
          // Login exitoso - redirigir al dashboard administrativo
          console.log('Login exitoso - redirigiendo al dashboard administrativo');
          this.isLoading = false;
          this.router.navigate(['/dashboard']);
        } else {
          this.loginError = 'Usuario o contraseña incorrectos. Credenciales de administrador: admin@asecapt.com / admin2024';
          this.isLoading = false;
        }
      }, 2000);
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