import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProgramService, Program, ProgramWithContents } from '../../services/program.service';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit {
  course: Program | null = null;
  courseContents: ProgramWithContents | null = null;
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
    if (!this.courseId) {
      this.error = 'ID del curso no válido';
      return;
    }

    this.loading = true;
    this.error = null;

    const courseIdNumber = parseInt(this.courseId);

    // Obtener datos del programa con sus contenidos
    this.programService.getProgramWithContents(courseIdNumber).subscribe({
      next: (data) => {
        this.courseContents = data;
        this.course = data.program;
        this.loading = false;
        console.log('Curso cargado:', this.course);
        console.log('Contenidos del curso:', data.contents);
      },
      error: (error) => {
        console.error('Error cargando curso:', error);
        this.error = 'Error al cargar los detalles del curso';
        this.loading = false;
      }
    });
  }

  // Helper methods for template
  getModuleIcon(type: string): string {
    switch(type) {
      case 'video': return 'fas fa-play-circle';
      case 'document': return 'fas fa-file';
      case 'exam': return 'fas fa-clipboard-check';
      case 'assignment': return 'fas fa-tasks';
      case 'module': return 'fas fa-book';
      case 'lesson': return 'fas fa-play-circle';
      case 'resource': return 'fas fa-download';
      default: return 'fas fa-book';
    }
  }

  // Helper method to split prerequisites and objectives
  getPrerequisitesList(): string[] {
    if (!this.course?.prerequisites) return [];
    return this.course.prerequisites.split(',').map(req => req.trim()).filter(req => req.length > 0);
  }

  getObjectivesList(): string[] {
    if (!this.course?.objectives) return [];
    return this.course.objectives.split(',').map(obj => obj.trim()).filter(obj => obj.length > 0);
  }

  // Helper method to get organized contents by topic
  getOrganizedContents(): any[] {
    if (!this.courseContents?.contents) return [];

    // Agrupar contenidos por topic o crear grupos genéricos
    const grouped: { [key: string]: any[] } = {};

    this.courseContents.contents.forEach(content => {
      if (!content) return;

      const topicKey = content.topic || `Módulo ${content.topicNumber || 1}`;

      if (!grouped[topicKey]) {
        grouped[topicKey] = [];
      }

      grouped[topicKey].push(content);
    });

    // Convertir a array y ordenar
    return Object.keys(grouped).map(topic => ({
      title: topic,
      contents: grouped[topic].sort((a, b) => (a.topicNumber || 0) - (b.topicNumber || 0))
    }));
  }

  // Helper method to get total duration
  getTotalDuration(): string {
    if (!this.courseContents?.contents) return this.course?.duration || '0 horas';

    // Si tenemos contenidos con duración específica, calcular total
    const totalHours = this.courseContents.contents.reduce((total, content) => {
      if (content?.duration) {
        const hours = parseFloat(content.duration.replace(/[^\d.]/g, '')) || 0;
        return total + hours;
      }
      return total;
    }, 0);

    return totalHours > 0 ? `${totalHours} horas` : this.course?.duration || '0 horas';
  }

  // Helper method to get total lessons count
  getTotalLessons(): number {
    return this.courseContents?.contents?.length || 0;
  }

  // WhatsApp enrollment method
  enrollViaWhatsApp(): void {
    if (!this.course) return;

    const phoneNumber = '+51967634608'; // Número de WhatsApp sin espacios
    const message = `¡Hola! 👋

Estoy interesado/a en matricularme en el siguiente curso:

📚 *${this.course.title}*
📂 Categoría: ${this.course.category}
⏱️ Duración: ${this.course.duration}
💰 Precio: ${this.course.price}

Por favor, me podrían brindar más información sobre el proceso de matrícula y los requisitos.

¡Gracias!`;

    // Codificar el mensaje para URL
    const encodedMessage = encodeURIComponent(message);

    // Construir la URL de WhatsApp
    const whatsappUrl = `https://wa.me/${phoneNumber.replace(/[^\d]/g, '')}?text=${encodedMessage}`;

    // Abrir WhatsApp en una nueva ventana
    window.open(whatsappUrl, '_blank');
  }
}
