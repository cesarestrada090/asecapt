import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface QRCode {
  id: string;
  title: string;
  content: string;
  createdAt: Date;
  qrDataURL: string;
  type?: string;
}

interface Student {
  id: string;
  name: string;
  email: string;
  dni: string;
  course: string;
  status: 'activo' | 'completado' | 'suspendido';
  enrolledAt: Date;
}

interface Course {
  id: string;
  name: string;
  description: string;
  status: 'activo' | 'inactivo';
  enrolledStudents: number;
  duration: string;
}

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent {
  // Navegación del dashboard
  activeSection: string = 'qr';
  sidebarOpen: boolean = false;

  // Datos del formulario para generar QR
  qrForm = {
    title: '',
    content: '',
    type: 'text' // text, url, email, phone
  };

  // Lista de QRs generados
  generatedQRs: QRCode[] = [];
  filteredQRs: QRCode[] = [];
  
  // Estados
  isGenerating: boolean = false;

  // Formularios de búsqueda
  searchForm = {
    qrQuery: '',
    qrType: '',
    studentQuery: '',
    studentCourse: '',
    courseQuery: '',
    courseStatus: ''
  };

  // Datos mock
  mockStudents: Student[] = [
    {
      id: '1',
      name: 'Juan Carlos Pérez',
      email: 'juan.perez@email.com',
      dni: '12345678',
      course: 'Seguridad Industrial',
      status: 'activo',
      enrolledAt: new Date('2024-01-15')
    },
    {
      id: '2',
      name: 'María Elena González',
      email: 'maria.gonzalez@email.com',
      dni: '87654321',
      course: 'Soldadura',
      status: 'completado',
      enrolledAt: new Date('2024-02-01')
    },
    {
      id: '3',
      name: 'Carlos Alberto Ruiz',
      email: 'carlos.ruiz@email.com',
      dni: '11223344',
      course: 'Electricidad',
      status: 'activo',
      enrolledAt: new Date('2024-01-20')
    },
    {
      id: '4',
      name: 'Ana Sofia Martínez',
      email: 'ana.martinez@email.com',
      dni: '44332211',
      course: 'Mecánica',
      status: 'suspendido',
      enrolledAt: new Date('2023-12-10')
    },
    {
      id: '5',
      name: 'Roberto Silva',
      email: 'roberto.silva@email.com',
      dni: '55667788',
      course: 'Seguridad Industrial',
      status: 'activo',
      enrolledAt: new Date('2024-02-15')
    }
  ];

  mockCourses: Course[] = [
    {
      id: '1',
      name: 'Seguridad Industrial',
      description: 'Curso completo de seguridad y salud ocupacional',
      status: 'activo',
      enrolledStudents: 25,
      duration: '120 horas'
    },
    {
      id: '2',
      name: 'Soldadura Básica',
      description: 'Fundamentos de soldadura eléctrica y oxiacetilénica',
      status: 'activo',
      enrolledStudents: 18,
      duration: '80 horas'
    },
    {
      id: '3',
      name: 'Electricidad Residencial',
      description: 'Instalaciones eléctricas domiciliarias',
      status: 'activo',
      enrolledStudents: 22,
      duration: '100 horas'
    },
    {
      id: '4',
      name: 'Mecánica Automotriz',
      description: 'Diagnóstico y reparación de motores',
      status: 'inactivo',
      enrolledStudents: 0,
      duration: '150 horas'
    }
  ];

  filteredStudents: Student[] = [];
  filteredCourses: Course[] = [];

  constructor(private router: Router) {
    // Cargar QRs guardados del localStorage
    this.loadSavedQRs();
    // Inicializar listas filtradas
    this.filteredQRs = [...this.generatedQRs];
    this.filteredStudents = [...this.mockStudents];
    this.filteredCourses = [...this.mockCourses];
  }

  // Generar nuevo QR
  generateQR() {
    if (!this.qrForm.title.trim() || !this.qrForm.content.trim()) {
      return;
    }

    this.isGenerating = true;

    // Simular generación de QR
    setTimeout(() => {
      const newQR: QRCode = {
        id: this.generateId(),
        title: this.qrForm.title,
        content: this.formatContent(),
        createdAt: new Date(),
        qrDataURL: this.generateQRDataURL(),
        type: this.qrForm.type
      };

      this.generatedQRs.unshift(newQR);
      this.filteredQRs = [...this.generatedQRs];
      this.saveQRsToStorage();
      this.resetForm();
      this.isGenerating = false;
    }, 1500);
  }

  // Formatear contenido según el tipo
  formatContent(): string {
    const content = this.qrForm.content.trim();
    
    switch (this.qrForm.type) {
      case 'url':
        return content.startsWith('http') ? content : `https://${content}`;
      case 'email':
        return `mailto:${content}`;
      case 'phone':
        return `tel:${content}`;
      default:
        return content;
    }
  }

  // Generar QR simulado (en producción usarías una librería como qrcode.js)
  generateQRDataURL(): string {
    // SVG simple simulando un QR
    const size = 200;
    const squares = Math.floor(Math.random() * 50) + 100;
    let qrPattern = '<svg width="200" height="200" xmlns="http://www.w3.org/2000/svg">';
    qrPattern += '<rect width="200" height="200" fill="white"/>';
    
    for (let i = 0; i < squares; i++) {
      const x = Math.floor(Math.random() * 20) * 10;
      const y = Math.floor(Math.random() * 20) * 10;
      qrPattern += `<rect x="${x}" y="${y}" width="10" height="10" fill="black"/>`;
    }
    
    qrPattern += '</svg>';
    
    return 'data:image/svg+xml;base64,' + btoa(qrPattern);
  }

  // Generar ID único
  generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  // Resetear formulario
  resetForm() {
    this.qrForm = {
      title: '',
      content: '',
      type: 'text'
    };
  }

  // Descargar QR
  downloadQR(qr: QRCode) {
    const link = document.createElement('a');
    link.download = `QR_${qr.title.replace(/\s+/g, '_')}.svg`;
    link.href = qr.qrDataURL;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // Eliminar QR
  deleteQR(qrId: string) {
    this.generatedQRs = this.generatedQRs.filter(qr => qr.id !== qrId);
    this.filteredQRs = this.filteredQRs.filter(qr => qr.id !== qrId);
    this.saveQRsToStorage();
  }



  // Cerrar sesión
  logout() {
    // Limpiar cualquier estado de autenticación si fuera necesario
    this.router.navigate(['/virtual-classroom']);
  }

  // Guardar QRs en localStorage
  private saveQRsToStorage() {
    localStorage.setItem('asecapt_qrs', JSON.stringify(this.generatedQRs));
  }

  // Cargar QRs del localStorage
  private loadSavedQRs() {
    const saved = localStorage.getItem('asecapt_qrs');
    if (saved) {
      this.generatedQRs = JSON.parse(saved).map((qr: any) => ({
        ...qr,
        createdAt: new Date(qr.createdAt)
      }));
      this.filteredQRs = [...this.generatedQRs];
    }
  }

  // Validar formulario
  isValidForm(): boolean {
    return this.qrForm.title.trim().length > 0 && this.qrForm.content.trim().length > 0;
  }

  // Obtener texto de placeholder dinámico
  getPlaceholderText(): string {
    switch (this.qrForm.type) {
      case 'url':
        return 'Ej: https://asecapt.com/certificado/123';
      case 'email':
        return 'Ej: estudiante@asecapt.com';
      case 'phone':
        return 'Ej: +51 948 090 763';
      default:
        return 'Ej: Certificado de Especialización válido hasta 2025';
    }
  }

  // TrackBy para optimizar ngFor
  trackByQRId(index: number, qr: QRCode): string {
    return qr.id;
  }

  // === NAVEGACIÓN DEL DASHBOARD ===

  // Alternar sidebar en móviles
  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  // Cambiar sección activa
  setActiveSection(section: string) {
    this.activeSection = section;
    this.sidebarOpen = false; // Cerrar sidebar en móviles
  }

  // Obtener título de la sección actual
  getSectionTitle(): string {
    switch (this.activeSection) {
      case 'qr': return 'Generador de QR';
      case 'qr-search': return 'Buscar QRs';
      case 'students': return 'Alumnos Matriculados';
      case 'courses': return 'Gestión de Cursos';
      default: return 'Dashboard';
    }
  }

  // Obtener descripción de la sección actual
  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'qr': return 'Crea y gestiona códigos QR';
      case 'qr-search': return 'Encuentra QRs por título o contenido';
      case 'students': return 'Administra estudiantes matriculados';
      case 'courses': return 'Gestiona cursos y programas';
      default: return 'Panel de administración ASECAPT';
    }
  }

  // === BÚSQUEDAS ===

  // Buscar QRs
  searchQRs() {
    const query = this.searchForm.qrQuery.toLowerCase();
    const type = this.searchForm.qrType;

    this.filteredQRs = this.generatedQRs.filter(qr => {
      const matchesQuery = !query || 
        qr.title.toLowerCase().includes(query) || 
        qr.content.toLowerCase().includes(query);
      
      const matchesType = !type || qr.type === type;

      return matchesQuery && matchesType;
    });
  }

  // Limpiar búsqueda de QRs
  clearQRSearch() {
    this.searchForm.qrQuery = '';
    this.searchForm.qrType = '';
    this.filteredQRs = [...this.generatedQRs];
  }

  // Buscar estudiantes
  searchStudents() {
    const query = this.searchForm.studentQuery.toLowerCase();
    const course = this.searchForm.studentCourse;

    this.filteredStudents = this.mockStudents.filter(student => {
      const matchesQuery = !query || 
        student.name.toLowerCase().includes(query) || 
        student.email.toLowerCase().includes(query) ||
        student.course.toLowerCase().includes(query) ||
        student.dni.includes(query);
      
      const matchesCourse = !course || student.course === course;

      return matchesQuery && matchesCourse;
    });
  }

  // Limpiar búsqueda de estudiantes
  clearStudentSearch() {
    this.searchForm.studentQuery = '';
    this.searchForm.studentCourse = '';
    this.filteredStudents = [...this.mockStudents];
  }

  // Buscar cursos
  searchCourses() {
    const query = this.searchForm.courseQuery.toLowerCase();
    const status = this.searchForm.courseStatus;

    this.filteredCourses = this.mockCourses.filter(course => {
      const matchesQuery = !query || 
        course.name.toLowerCase().includes(query) || 
        course.description.toLowerCase().includes(query);
      
      const matchesStatus = !status || course.status === status;

      return matchesQuery && matchesStatus;
    });
  }

  // Limpiar búsqueda de cursos
  clearCourseSearch() {
    this.searchForm.courseQuery = '';
    this.searchForm.courseStatus = '';
    this.filteredCourses = [...this.mockCourses];
  }

  // === MÉTODOS AUXILIARES ===

  // Obtener clase de badge para estado de estudiante
  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'activo': return 'bg-success';
      case 'completado': return 'bg-primary';
      case 'suspendido': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
} 