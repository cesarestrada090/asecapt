import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CoursesService, Course } from '../../services/courses.service';

@Component({
  selector: 'app-courses-grid',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './courses-grid.component.html',
  styleUrl: './courses-grid.component.css'
})
export class CoursesGridComponent implements OnInit {
  courses: Course[] = [];

  constructor(private coursesService: CoursesService) {}

  ngOnInit() {
    this.loadCourses();
  }

  loadCourses() {
    this.coursesService.getCourses().subscribe(data => {
      // Mostrar todos los 9 cursos en courses-grid
      this.courses = data.courses;
    });
  }
}
