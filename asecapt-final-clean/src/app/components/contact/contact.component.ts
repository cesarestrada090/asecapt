import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface ContactForm {
  name: string;
  email: string;
  subject: string;
  message: string;
}

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {

  // Contact form data
  contactForm: ContactForm = {
    name: '',
    email: '',
    subject: '',
    message: ''
  };

  // Loading and message states
  loading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  // Company information
  companyInfo = {
    name: 'ASECAPT',
    fullName: 'Asociación Especializada en Capacitación Técnica',
    description: 'Somos una institución especializada en capacitación técnica profesional, comprometida con la excelencia educativa y el desarrollo de competencias laborales.',
    address: 'Trujillo, La Libertad, Perú',
    email: 'asociacion.esp.asecapt@gmail.com',
    phone: '+51 XXX XXX XXX',
    website: 'https://asecapt.com',
    socialMedia: {
      facebook: '#',
      linkedin: '#',
      instagram: '#'
    }
  };

  // FAQ items
  faqItems = [
    {
      question: '¿Cómo puedo matricularme en un curso?',
      answer: 'Puedes matricularte contactando directamente con nuestro equipo de admisiones a través del formulario de contacto o enviando un email a nuestro correo institucional.'
    },
    {
      question: '¿Los certificados tienen validez oficial?',
      answer: 'Sí, todos nuestros certificados cuentan con validez oficial y están respaldados por nuestra institución. Puedes verificar la autenticidad a través de nuestro sistema de validación.'
    },
    {
      question: '¿Ofrecen modalidad virtual?',
      answer: 'Sí, ofrecemos cursos en modalidad presencial, virtual y mixta, adaptándonos a las necesidades de nuestros estudiantes.'
    },
    {
      question: '¿Cuál es la duración de los cursos?',
      answer: 'La duración varía según el programa. Nuestros cursos van desde talleres de 20 horas hasta especializaciones de varios meses.'
    }
  ];

  constructor() {}

  // Send contact message
  sendMessage() {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.clearMessages();

    // Simulate API call - replace with actual service call
    setTimeout(() => {
      // For now, just show success message
      this.successMessage = 'Tu mensaje ha sido enviado exitosamente. Nos pondremos en contacto contigo pronto.';
      this.resetForm();
      this.loading = false;
    }, 2000);
  }

  // Form validation
  validateForm(): boolean {
    if (!this.contactForm.name.trim()) {
      this.errorMessage = 'El nombre es requerido';
      return false;
    }

    if (!this.contactForm.email.trim()) {
      this.errorMessage = 'El email es requerido';
      return false;
    }

    if (!this.isValidEmail(this.contactForm.email)) {
      this.errorMessage = 'Ingresa un email válido';
      return false;
    }

    if (!this.contactForm.subject.trim()) {
      this.errorMessage = 'El asunto es requerido';
      return false;
    }

    if (!this.contactForm.message.trim()) {
      this.errorMessage = 'El mensaje es requerido';
      return false;
    }

    if (this.contactForm.message.trim().length < 10) {
      this.errorMessage = 'El mensaje debe tener al menos 10 caracteres';
      return false;
    }

    return true;
  }

  // Email validation
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // Reset form
  resetForm() {
    this.contactForm = {
      name: '',
      email: '',
      subject: '',
      message: ''
    };
  }

  // Clear messages
  clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  // Copy email to clipboard
  copyEmail() {
    navigator.clipboard.writeText(this.companyInfo.email).then(() => {
      this.successMessage = 'Email copiado al portapapeles';
      setTimeout(() => this.clearMessages(), 3000);
    }).catch(() => {
      this.errorMessage = 'No se pudo copiar el email';
    });
  }
}
