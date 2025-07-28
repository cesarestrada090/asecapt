import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
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

  onLogin() {
    if (this.isValidForm()) {
      this.isLoading = true;
      this.loginError = '';
      
      // Simular proceso de login
      setTimeout(() => {
        // Simular validación de credenciales
        if (this.loginForm.username === 'estudiante@asecapt.com' && this.loginForm.password === 'asecapt2024') {
          // Login exitoso (aquí iría la lógica real)
          console.log('Login exitoso');
          this.isLoading = false;
          // Redirigir al aula virtual real
        } else {
          this.loginError = 'Usuario o contraseña incorrectos. Credenciales de prueba: estudiante@asecapt.com / asecapt2024';
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