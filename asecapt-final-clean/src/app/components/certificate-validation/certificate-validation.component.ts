import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-certificate-validation',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './certificate-validation.component.html',
  styleUrl: './certificate-validation.component.css'
})
export class CertificateValidationComponent {
  documentNumber: string = '';
  searchResult: any = null;
  isSearching: boolean = false;

  onSearch() {
    if (this.documentNumber.trim()) {
      this.isSearching = true;
      // Simular búsqueda (aquí iría la lógica real de búsqueda)
      setTimeout(() => {
        this.searchResult = {
          found: Math.random() > 0.5, // Simular resultado aleatorio
          certificate: {
            studentName: 'Juan Carlos Pérez Rodríguez',
            courseTitle: 'Especialización en SSOMA',
            completionDate: '15 de Noviembre, 2023',
            certificateNumber: 'ASECAPT-2023-1156',
            instructor: 'Dr. Carlos Mendoza'
          }
        };
        this.isSearching = false;
      }, 1500);
    }
  }

  resetSearch() {
    this.searchResult = null;
    this.documentNumber = '';
  }
}
