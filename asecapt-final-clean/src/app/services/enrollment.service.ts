import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, buildApiUrl } from '../constants';

export interface Student {
  fullName: string;
  documentNumber: string;
  email: string;
}

export interface Program {
  name: string;
  credits: number;
  hours: number;
  duration: string;
}

export interface User {
  id: number;
  person: {
    firstName: string;
    lastName: string;
    documentNumber: string;
    email: string;
  };
}

export interface Enrollment {
  id: number;
  userId: number;
  programId: number;
  enrollmentDate: string;
  startDate: string;
  completionDate?: string;
  status: 'enrolled' | 'in_progress' | 'completed' | 'suspended';
  finalGrade?: number;
  attendancePercentage?: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  // TODO: Add user and program details when implementing complex joins
  // user?: User;
  // program?: Program;
}

export interface EnrollmentSummary {
  userId: number;
  totalEnrollments: number;
  completedEnrollments: number;
}

export interface CreateEnrollmentRequest {
  userId: number;
  programId: number;
  startDate?: string;
}

export interface CompleteEnrollmentRequest {
  finalGrade: number;
  attendancePercentage: number;
}

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private apiUrl = buildApiUrl(APP_CONFIG.endpoints.enrollments);

  constructor(private http: HttpClient) { }

  // Get all enrollments
  getAllEnrollments(): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(this.apiUrl);
  }

  // Get enrollments by user
  getEnrollmentsByUser(userId: number): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/user/${userId}`);
  }

  // Get enrollments by program
  getEnrollmentsByProgram(programId: number): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/program/${programId}`);
  }

  // Get completed enrollments (for certificate generation)
  getCompletedEnrollments(): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/completed`);
  }

  // Search completed enrollments
  searchCompletedEnrollments(query: string): Observable<Enrollment[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Enrollment[]>(`${this.apiUrl}/completed/search`, { params });
  }

  // Get enrollment summary for all students (optimized endpoint)
  getEnrollmentSummary(): Observable<{ [userId: number]: EnrollmentSummary }> {
    return this.http.get<{ [userId: number]: EnrollmentSummary }>(`${this.apiUrl}/summary`);
  }

  // Get enrollment by ID
  getEnrollmentById(id: number): Observable<Enrollment> {
    return this.http.get<Enrollment>(`${this.apiUrl}/${id}`);
  }

  // Create new enrollment
  createEnrollment(request: CreateEnrollmentRequest): Observable<Enrollment> {
    return this.http.post<Enrollment>(this.apiUrl, request);
  }

  // Update enrollment status
  updateEnrollmentStatus(id: number, status: string): Observable<Enrollment> {
    return this.http.put<Enrollment>(`${this.apiUrl}/${id}/status`, { status });
  }

  // Update enrollment (full update)
  updateEnrollment(id: number, updateData: Partial<Enrollment>): Observable<Enrollment> {
    return this.http.put<Enrollment>(`${this.apiUrl}/${id}`, updateData);
  }

  // Complete enrollment with grades
  completeEnrollment(id: number, request: CompleteEnrollmentRequest): Observable<Enrollment> {
    return this.http.put<Enrollment>(`${this.apiUrl}/${id}/complete`, request);
  }

  // Delete enrollment
  deleteEnrollment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
