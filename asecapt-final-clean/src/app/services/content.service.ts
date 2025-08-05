import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, buildApiUrl } from '../constants';
import { Content, CreateContentRequest, UpdateContentRequest } from './program.service';

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private apiUrl = buildApiUrl('/contents');

  constructor(private http: HttpClient) {}

  // === CONTENT CRUD ===

  getAllContents(): Observable<Content[]> {
    return this.http.get<Content[]>(this.apiUrl);
  }

  getContentById(id: number): Observable<Content> {
    return this.http.get<Content>(`${this.apiUrl}/${id}`);
  }

  createContent(request: CreateContentRequest): Observable<Content> {
    return this.http.post<Content>(this.apiUrl, request);
  }

  updateContent(id: number, request: UpdateContentRequest): Observable<Content> {
    return this.http.put<Content>(`${this.apiUrl}/${id}`, request);
  }

  deleteContent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchContents(query: string, type?: string): Observable<Content[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (type) params = params.set('type', type);
    
    return this.http.get<Content[]>(`${this.apiUrl}/search`, { params });
  }

  // === CONTENT STATISTICS ===

  getContentStatistics(): Observable<{
    totalContents: number;
    contentsByType: { [key: string]: number };
    averageDuration: string;
    mostUsedContents: Content[];
  }> {
    return this.http.get<any>(`${this.apiUrl}/statistics`);
  }

  // === CONTENT USAGE ===

  getContentUsage(id: number): Observable<{
    contentId: number;
    programsUsingThis: number;
    totalEnrollments: number;
    lastUsed: Date;
  }> {
    return this.http.get<any>(`${this.apiUrl}/${id}/usage`);
  }

  // === CONTENT ACTIONS ===

  duplicateContent(id: number, newTitle: string): Observable<Content> {
    return this.http.post<Content>(`${this.apiUrl}/${id}/duplicate`, { title: newTitle });
  }

  // === CONTENT TYPES AND TEMPLATES ===

  getContentTypes(): Observable<{ value: string; label: string; description: string }[]> {
    return this.http.get<any[]>(`${this.apiUrl}/types`);
  }

  getContentTemplates(type: string): Observable<{ id: string; name: string; template: string }[]> {
    return this.http.get<any[]>(`${this.apiUrl}/templates/${type}`);
  }
} 