import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, buildApiUrl } from '../constants';

export interface Student {
  id: number;
  username: string;
  type: number;
  active: boolean;
  isEmailVerified: boolean;
  isPremium?: boolean;
  is_premium?: boolean;
  createdAt: string;
  updatedAt: string;
  person: {
    id: number;
    firstName: string;
    lastName: string;
    documentNumber: string;
    documentType: string;
    email: string;
    phoneNumber: string;
    gender?: string;
    birthDate?: string;
  };
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  documentNumber: string;
  documentType?: string;
  email: string;
  phoneNumber: string;
  gender?: string;
  birthDate?: string;
  username?: string;
  password?: string;
}

export interface UpdateStudentRequest {
  firstName?: string;
  lastName?: string;
  documentNumber?: string;
  documentType?: string;
  email?: string;
  phoneNumber?: string;
  gender?: string;
  birthDate?: string;
  username?: string;
  password?: string;
}

export interface StudentStatistics {
  totalStudents: number;
  activeStudents: number;
  inactiveStudents: number;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = buildApiUrl('students');

  constructor(private http: HttpClient) { }

  // Get all students
  getAllStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl);
  }

  // Search students
  searchStudents(query?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (query) {
      params = params.set('query', query);
    }
    return this.http.get<Student[]>(`${this.apiUrl}/search`, { params });
  }

  // Get student by ID
  getStudentById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/${id}`);
  }

  // Create new student
  createStudent(request: CreateStudentRequest): Observable<Student> {
    return this.http.post<Student>(this.apiUrl, request);
  }

  // Update student
  updateStudent(id: number, request: UpdateStudentRequest): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/${id}`, request);
  }

  // Toggle student status
  toggleStudentStatus(id: number): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/${id}/toggle-status`, {});
  }

  // Get student statistics
  getStudentStatistics(): Observable<StudentStatistics> {
    return this.http.get<StudentStatistics>(`${this.apiUrl}/statistics`);
  }
} 