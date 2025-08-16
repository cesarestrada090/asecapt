import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Complaint {
  id: string;
  complaintNumber: string;
  type: 'reclamo' | 'queja' | 'sugerencia';
  name: string;
  email: string;
  phone: string;
  document: string;
  description: string;
  createdAt: string;
  status: 'pendiente' | 'en_proceso' | 'resuelto';
  response?: string;
}

export interface CreateComplaintRequest {
  type: string;
  name: string;
  email: string;
  phone: string;
  document: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class ComplaintService {
  private apiUrl = `${environment.apiUrl}/complaints`;

  constructor(private http: HttpClient) {}

  createComplaint(complaint: CreateComplaintRequest): Observable<Complaint> {
    return this.http.post<Complaint>(this.apiUrl, complaint);
  }

  getComplaintByNumber(complaintNumber: string): Observable<Complaint> {
    return this.http.get<Complaint>(`${this.apiUrl}/${complaintNumber}`);
  }
}
