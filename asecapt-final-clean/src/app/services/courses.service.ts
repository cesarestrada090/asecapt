import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Course {
  id: number;
  title: string;
  category: string;
  categoryClass: string;
  image: string;
  instructor: {
    name: string;
    avatar: string;
  };
  modules: number;
  students: number;
  rating: number;
  reviews: number;
  price: string;
  type: string;
}

export interface CoursesResponse {
  courses: Course[];
}

@Injectable({
  providedIn: 'root'
})
export class CoursesService {

  constructor(private http: HttpClient) { }

  getCourses(): Observable<CoursesResponse> {
    return this.http.get<CoursesResponse>('assets/data/courses.json');
  }

  getCourseById(id: number): Observable<Course | undefined> {
    return new Observable(observer => {
      this.getCourses().subscribe(data => {
        const course = data.courses.find(c => c.id === id);
        observer.next(course);
        observer.complete();
      });
    });
  }
} 