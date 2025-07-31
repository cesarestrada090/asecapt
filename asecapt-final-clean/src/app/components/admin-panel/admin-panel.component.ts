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

interface Certificate {
  id: string;
  studentName: string;
  studentDNI: string;
  studentEmail: string;
  courseName: string;
  courseType: 'curso' | 'diplomado' | 'especializacion';
  completionDate: Date;
  issueDate: Date;
  certificateNumber: string;
  verificationURL: string;
  pdfFile?: File;
  pdfDataURL?: string;
  qrCode?: QRCode;
  createdAt: Date;
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

  // Datos del formulario para certificados
  certificateForm = {
    studentName: '',
    studentDNI: '',
    studentEmail: '',
    courseName: '',
    courseType: 'curso' as 'curso' | 'diplomado' | 'especializacion',
    completionDate: '',
    issueDate: new Date().toISOString().split('T')[0], // Fecha actual por defecto
    pdfFile: null as File | null
  };

  // Lista de certificados generados
  generatedCertificates: Certificate[] = [];
  filteredCertificates: Certificate[] = [];
  
  // Estados
  isGenerating: boolean = false;
  isUploadingPDF: boolean = false;
  pdfPreviewURL: string | null = null;

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
    // Cargar certificados guardados del localStorage
    this.loadSavedCertificates();
    // Inicializar listas filtradas
    this.filteredCertificates = [...this.generatedCertificates];
    this.filteredStudents = [...this.mockStudents];
    this.filteredCourses = [...this.mockCourses];
  }

  // Generar nuevo certificado con QR
  generateCertificate() {
    if (!this.isValidCertificateForm()) {
      return;
    }

    this.isGenerating = true;

    // Simular generación de certificado
    setTimeout(() => {
      const certificateNumber = this.generateCertificateNumber();
      const verificationURL = `https://asecapt.com/verify/${certificateNumber}`;
      
      // Crear QR para la URL de verificación
      const qrCode: QRCode = {
        id: this.generateId(),
        title: `Certificado - ${this.certificateForm.studentName}`,
        content: verificationURL,
        createdAt: new Date(),
        qrDataURL: this.generateQRDataURL(),
        type: 'url'
      };

      const newCertificate: Certificate = {
        id: this.generateId(),
        studentName: this.certificateForm.studentName,
        studentDNI: this.certificateForm.studentDNI,
        studentEmail: this.certificateForm.studentEmail,
        courseName: this.certificateForm.courseName,
        courseType: this.certificateForm.courseType,
        completionDate: new Date(this.certificateForm.completionDate),
        issueDate: new Date(this.certificateForm.issueDate),
        certificateNumber: certificateNumber,
        verificationURL: verificationURL,
        pdfDataURL: this.pdfPreviewURL || undefined,
        qrCode: qrCode,
        createdAt: new Date()
      };

      this.generatedCertificates.unshift(newCertificate);
      this.filteredCertificates = [...this.generatedCertificates];
      this.saveCertificatesToStorage();
      this.resetCertificateForm();
      this.isGenerating = false;
    }, 1500);
  }

  // Generar número de certificado único
  generateCertificateNumber(): string {
    const year = new Date().getFullYear();
    const month = String(new Date().getMonth() + 1).padStart(2, '0');
    const randomNum = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    return `ASECAPT-${year}${month}-${randomNum}`;
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

  // Resetear formulario de certificado
  resetCertificateForm() {
    this.certificateForm = {
      studentName: '',
      studentDNI: '',
      studentEmail: '',
      courseName: '',
      courseType: 'curso',
      completionDate: '',
      issueDate: new Date().toISOString().split('T')[0],
      pdfFile: null
    };
    this.pdfPreviewURL = null;
  }

  // Validar formulario de certificado
  isValidCertificateForm(): boolean {
    return !!(
      this.certificateForm.studentName.trim() &&
      this.certificateForm.studentDNI.trim() &&
      this.certificateForm.studentEmail.trim() &&
      this.certificateForm.courseName.trim() &&
      this.certificateForm.completionDate &&
      this.certificateForm.issueDate &&
      this.pdfPreviewURL
    );
  }

  // Manejar selección de archivo PDF
  onPDFFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.certificateForm.pdfFile = file;
      this.isUploadingPDF = true;
      
      // Crear preview URL para el PDF
      const reader = new FileReader();
      reader.onload = (e) => {
        this.pdfPreviewURL = e.target?.result as string;
        this.isUploadingPDF = false;
      };
      reader.readAsDataURL(file);
    }
  }

  // Descargar QR del certificado
  downloadCertificateQR(certificate: Certificate) {
    if (certificate.qrCode) {
      const link = document.createElement('a');
      link.download = `QR_${certificate.certificateNumber}.svg`;
      link.href = certificate.qrCode.qrDataURL;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }

  // Eliminar certificado
  deleteCertificate(certificateId: string) {
    this.generatedCertificates = this.generatedCertificates.filter(cert => cert.id !== certificateId);
    this.filteredCertificates = this.filteredCertificates.filter(cert => cert.id !== certificateId);
    this.saveCertificatesToStorage();
  }



  // Cerrar sesión
  logout() {
    // Limpiar cualquier estado de autenticación si fuera necesario
    this.router.navigate(['/virtual-classroom']);
  }

  // Guardar certificados en localStorage
  private saveCertificatesToStorage() {
    localStorage.setItem('asecapt_certificates', JSON.stringify(this.generatedCertificates));
  }

  // Cargar certificados del localStorage
  private loadSavedCertificates() {
    const saved = localStorage.getItem('asecapt_certificates');
    if (saved) {
      this.generatedCertificates = JSON.parse(saved).map((cert: any) => ({
        ...cert,
        createdAt: new Date(cert.createdAt),
        completionDate: new Date(cert.completionDate),
        issueDate: new Date(cert.issueDate)
      }));
      this.filteredCertificates = [...this.generatedCertificates];
    }
  }

  // Obtener texto para el tipo de curso
  getCourseTypeText(type: string): string {
    switch (type) {
      case 'curso':
        return 'Curso';
      case 'diplomado':
        return 'Diplomado';
      case 'especializacion':
        return 'Especialización';
      default:
        return 'Curso';
    }
  }

  // TrackBy para optimizar ngFor
  trackByQRId(index: number, item: QRCode | Certificate): string {
    return item.id;
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
      case 'qr': return 'Generador de Certificados';
      case 'qr-search': return 'Buscar Certificados';
      case 'students': return 'Alumnos Matriculados';
      case 'courses': return 'Gestión de Cursos';
      default: return 'Dashboard';
    }
  }

  // Obtener descripción de la sección actual
  getSectionDescription(): string {
    switch (this.activeSection) {
      case 'qr': return 'Genera certificados con QR de verificación';
      case 'qr-search': return 'Busca certificados por estudiante, curso o número';
      case 'students': return 'Administra estudiantes matriculados';
      case 'courses': return 'Gestiona cursos y programas';
      default: return 'Panel de administración ASECAPT';
    }
  }

  // === BÚSQUEDAS ===

  // Buscar certificados
  searchCertificates() {
    const query = this.searchForm.qrQuery.toLowerCase();
    const type = this.searchForm.qrType;

    this.filteredCertificates = this.generatedCertificates.filter(cert => {
      const matchesQuery = !query || 
        cert.studentName.toLowerCase().includes(query) || 
        cert.certificateNumber.toLowerCase().includes(query) ||
        cert.courseName.toLowerCase().includes(query);
      
      const matchesType = !type || cert.courseType === type;

      return matchesQuery && matchesType;
    });
  }

  // Limpiar búsqueda de certificados
  clearCertificateSearch() {
    this.searchForm.qrQuery = '';
    this.searchForm.qrType = '';
    this.filteredCertificates = [...this.generatedCertificates];
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