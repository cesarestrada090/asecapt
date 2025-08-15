import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProgramService, Program } from '../../services/program.service';

// Interfaz extendida para el componente con propiedades adicionales
interface CourseDetails extends Program {
  reviews?: {
    author: string;
    rating: number;
    comment: string;
    date: string;
  }[];
  modules?: {
    title: string;
    lessons: {
      title: string;
      duration: string;
      type: string;
    }[];
  }[];
}

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit {
  course: CourseDetails | null = null;
  loading: boolean = false;
  error: string | null = null;
  courseId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService
  ) {}

  ngOnInit() {
    this.courseId = this.route.snapshot.paramMap.get('id');
    this.loadCourseDetails();
  }

  loadCourseDetails() {
    this.loading = true;
    this.error = null;

    // For now, use mock data based on courseId
    setTimeout(() => {
      this.course = this.getMockCourse(this.courseId);
      this.loading = false;
    }, 500);
  }

  private getMockCourse(courseId: string | null): CourseDetails {
    // Base mock course data - you can expand this with more courses
    const baseCourse: CourseDetails = {
      id: parseInt(courseId || '1'),
      title: "Especialidad en Hemoterapia y Banco de Sangre",
      description: "Programa especializado en técnicas avanzadas de hemoterapia y gestión de bancos de sangre, dirigido a profesionales de la salud que buscan especializarse en esta área crítica.",
      category: "Salud",
      type: "specialization",
      duration: "320 horas",
      credits: 20,
      price: "Consultar",
      instructor: "Dr. Elena Vargas",
      maxStudents: 45,
      status: "active",
      imageUrl: "https://images.unsplash.com/photo-1615461066841-6116e61058f4?auto=format&fit=crop&w=800&h=400&q=80",
      prerequisites: "Título profesional en ciencias de la salud, experiencia mínima de 2 años en área clínica",
      objectives: "Desarrollar competencias en técnicas de hemoterapia, gestionar eficientemente bancos de sangre",
      startDate: "2024-01-15",
      endDate: "2024-06-15",
      createdAt: "2024-01-01T00:00:00Z",
      updatedAt: "2024-01-01T00:00:00Z",
      showInLanding: true,
      modules: [
        {
          title: "Fundamentos de Hemoterapia",
          lessons: [
            { title: "Introducción a la hemoterapia", duration: "2 horas", type: "video" },
            { title: "Componentes sanguíneos", duration: "3 horas", type: "document" },
            { title: "Evaluación Módulo 1", duration: "1 hora", type: "exam" }
          ]
        },
        {
          title: "Gestión de Bancos de Sangre",
          lessons: [
            { title: "Organización y estructura", duration: "2.5 horas", type: "video" },
            { title: "Protocolos de donación", duration: "3 horas", type: "document" },
            { title: "Práctica supervisada", duration: "4 horas", type: "assignment" }
          ]
        },
        {
          title: "Seguridad Transfusional",
          lessons: [
            { title: "Protocolos de seguridad", duration: "2 horas", type: "video" },
            { title: "Manejo de reacciones adversas", duration: "2.5 horas", type: "document" },
            { title: "Casos clínicos", duration: "3 horas", type: "assignment" }
          ]
        }
      ],
      reviews: [
        {
          author: "Dr. Carlos Mendoza",
          rating: 5,
          comment: "Excelente programa, muy completo y actualizado. Los instructores son de primer nivel.",
          date: "Marzo 2024"
        },
        {
          author: "Lic. María Santos",
          rating: 5,
          comment: "La metodología es muy práctica y aplicable al trabajo diario. Totalmente recomendado.",
          date: "Febrero 2024"
        }
      ]
    };

    return baseCourse;
  }

  // Helper methods for template
  getModuleIcon(type: string): string {
    switch(type) {
      case 'video': return 'fas fa-play-circle';
      case 'document': return 'fas fa-file';
      case 'exam': return 'fas fa-clipboard-check';
      case 'assignment': return 'fas fa-tasks';
      default: return 'fas fa-book';
    }
  }

  getStarArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < rating ? 1 : 0);
  }

  // Helper method to split prerequisites and objectives
  getPrerequisitesList(): string[] {
    if (!this.course?.prerequisites) return [];
    return this.course.prerequisites.split(',').map(req => req.trim());
  }

  getObjectivesList(): string[] {
    if (!this.course?.objectives) return [];
    return this.course.objectives.split(',').map(obj => obj.trim());
  }
}
