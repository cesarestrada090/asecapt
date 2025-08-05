import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { APP_CONFIG, buildApiUrl } from '../constants';

// Program interfaces
export interface Program {
  id: number;
  title: string;
  description: string;
  type: 'course' | 'specialization' | 'certification';
  category: string;
  status: 'active' | 'inactive'; // Updated from 'archived' to 'inactive'
  duration: string;
  credits: number;
  price: string;
  startDate: string;
  endDate: string;
  instructor: string;
  maxStudents: number;
  prerequisites: string;
  objectives: string;
  createdAt: string;
  updatedAt: string;
}

export interface Content {
  id: number;
  title: string;
  description: string;
  type: 'module' | 'lesson' | 'assignment' | 'exam' | 'resource';
  duration: string;
  content: string;
  orderIndex?: number;
  createdAt?: Date;
  updatedAt?: Date;
  // Legacy fields for compatibility
  topicNumber?: number;
  topic?: string;
}

export interface ProgramContent {
  id: number;
  programId: number;
  contentId: number;
  orderIndex: number;
  isRequired: boolean;
  program?: Program;
  content?: Content;
  createdAt?: Date;
}

// Request/Response DTOs
export interface CreateProgramRequest {
  title: string;
  description: string;
  type: string;
  category: string;
  status: string;
  duration: string;
  credits: number;
  price: string;
  startDate: string;
  endDate: string;
  instructor: string;
  maxStudents?: number;
  prerequisites?: string;
  objectives?: string;
}

export interface UpdateProgramRequest extends CreateProgramRequest {
  id: number;
}

export interface CreateContentRequest {
  title: string;
  description: string;
  type: string;
  duration: string;
  content: string;
}

export interface UpdateContentRequest extends CreateContentRequest {
  id: number;
}

export interface AddContentToProgramRequest {
  programId: number;
  contentId: number;
  orderIndex: number;
  isRequired: boolean;
}

export interface ProgramWithContents {
  program: Program;
  contents: Content[];
  totalContents: number;
  totalDuration: string;
}

export interface ProgramStatisticsResponse {
  totalPrograms: number;
  activePrograms: number;
  inactivePrograms: number; // Changed from archivedPrograms
  totalEnrollments: number;
  averageRating: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private apiUrl = buildApiUrl(APP_CONFIG.endpoints.programs);

  constructor(private http: HttpClient) {}

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return new Observable(subscriber => {
      subscriber.error(error);
    });
  }

  // === PROGRAM CRUD ===

  getAllPrograms(): Observable<Program[]> {
    return this.http.get<Program[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getActivePrograms(): Observable<Program[]> {
    return this.http.get<Program[]>(`${this.apiUrl}/search?status=active`).pipe(
      catchError(this.handleError)
    );
  }

  getProgramById(id: number): Observable<Program> {
    return this.http.get<Program>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getProgramWithContents(id: number): Observable<ProgramWithContents> {
    return this.http.get<ProgramWithContents>(`${this.apiUrl}/${id}/with-contents`).pipe(
      catchError(this.handleError)
    );
  }

  createProgram(request: CreateProgramRequest): Observable<Program> {
    return this.http.post<Program>(this.apiUrl, request).pipe(
      catchError(this.handleError)
    );
  }

  updateProgram(id: number, request: UpdateProgramRequest): Observable<Program> {
    return this.http.put<Program>(`${this.apiUrl}/${id}`, request).pipe(
      catchError(this.handleError)
    );
  }

  deleteProgram(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  searchPrograms(query?: string, type?: string, status?: string, category?: string): Observable<Program[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (type) params = params.set('type', type);
    if (status) params = params.set('status', status);
    if (category) params = params.set('category', category);
    
    return this.http.get<Program[]>(`${this.apiUrl}/search`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  // === PROGRAM STATISTICS ===

  getProgramStatistics(): Observable<ProgramStatisticsResponse> {
    return this.http.get<ProgramStatisticsResponse>(`${this.apiUrl}/statistics`).pipe(
      catchError(this.handleError)
    );
  }

  // === PROGRAM CONTENT MANAGEMENT ===

  // Get program contents (modules/lessons assigned to a program)
  getProgramContents(programId: number): Observable<ProgramContent[]> {
    return this.http.get<ProgramContent[]>(`${this.apiUrl}/${programId}/program-contents`);
  }

  // === OPTIMIZED: Get content counts for all programs in single request ===
  getAllProgramContentCounts(): Observable<{[key: number]: number}> {
    return this.http.get<{[key: number]: number}>(`${this.apiUrl}/content-counts`);
  }

  // Add content to program
  addContentToProgram(request: AddContentToProgramRequest): Observable<ProgramContent> {
    return this.http.post<ProgramContent>(`${this.apiUrl}/${request.programId}/contents`, request);
  }

  updateProgramContent(programId: number, contentId: number, request: Partial<AddContentToProgramRequest>): Observable<ProgramContent> {
    return this.http.put<ProgramContent>(`${this.apiUrl}/${programId}/contents/${contentId}`, request);
  }

  // Remove content from program
  removeProgramContent(programId: number, contentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${programId}/contents/${contentId}`);
  }

  // === INDIVIDUAL CONTENT OPERATIONS ===
  
  // Toggle isRequired status for a specific content in a program
  toggleContentRequiredStatus(programId: number, contentId: number): Observable<ProgramContent> {
    return this.http.put<ProgramContent>(
      `${this.apiUrl}/${programId}/contents/${contentId}/toggle-required`, {}
    );
  }

  // === PROGRAM CRUD ===

  reorderProgramContents(programId: number, contentOrders: { contentId: number, orderIndex: number }[]): Observable<ProgramContent[]> {
    return this.http.put<ProgramContent[]>(`${this.apiUrl}/${programId}/contents/reorder`, { contentOrders });
  }

  // === PROGRAM ACTIONS - SIMPLIFIED ===

  toggleProgramStatus(id: number): Observable<Program> {
    return this.http.post<Program>(`${this.apiUrl}/${id}/toggle-status`, {}).pipe(
      catchError(this.handleError)
    );
  }

  duplicateProgram(id: number, title: string): Observable<Program> {
    return this.http.post<Program>(`${this.apiUrl}/${id}/duplicate`, { title }).pipe(
      catchError(this.handleError)
    );
  }

  // === PROGRAM VALIDATION ===

  validateProgramForPublication(id: number): Observable<{
    isValid: boolean;
    errors: string[];
    warnings: string[];
  }> {
    return this.http.get<any>(`${this.apiUrl}/${id}/validate`);
  }
} 