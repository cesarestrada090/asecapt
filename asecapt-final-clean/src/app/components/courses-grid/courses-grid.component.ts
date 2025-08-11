import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProgramService, Program } from '../../services/program.service';

@Component({
  selector: 'app-courses-grid',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './courses-grid.component.html',
  styleUrl: './courses-grid.component.css'
})
export class CoursesGridComponent implements OnInit {
  courses: Program[] = [];
  loading: boolean = false;
  error: string | null = null;

  constructor(private programService: ProgramService) {}

  ngOnInit() {
    this.loadCourses();
  }

  loadCourses() {
    this.loading = true;
    this.error = null;

    this.programService.getActivePrograms().subscribe({
      next: (programs) => {
        this.courses = programs;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.error = 'Error cargando cursos. Por favor intenta nuevamente.';
        this.loading = false;
      }
    });
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=400&h=220&q=80';
  }
}
