import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { buildApiUrl } from '../constants';

export interface PersonInfo {
  id: number;
  firstName: string;
  lastName: string;
  documentNumber: string;
  documentType: string;
  phoneNumber: string;
  email: string;
  gender?: string;
  birthDate?: string;
  bio?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {}

  /**
   * Get user profile information
   */
  getProfile(userId: number): Observable<PersonInfo> {
    const url = buildApiUrl(`v1/app/profile/${userId}`);
    return this.http.get<PersonInfo>(url);
  }

  /**
   * Change user password
   */
  changePassword(passwordData: ChangePasswordRequest): Observable<any> {
    // Get current user ID from auth service
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    const userId = currentUser.id;

    if (!userId) {
      throw new Error('User not authenticated');
    }

    const url = buildApiUrl(`v1/app/profile/${userId}/change-password`);
    return this.http.put(url, passwordData);
  }

  /**
   * Update user profile
   */
  updateProfile(userId: number, profileData: Partial<PersonInfo>): Observable<PersonInfo> {
    const url = buildApiUrl(`users/${userId}/profile`);
    return this.http.put<PersonInfo>(url, profileData);
  }

  /**
   * Get user enrollments
   */
  getEnrollments(userId: number): Observable<any[]> {
    const url = buildApiUrl(`enrollments/user/${userId}`);
    return this.http.get<any[]>(url);
  }

  /**
   * Download certificate
   */
  downloadCertificate(enrollmentId: number): Observable<Blob> {
    const url = buildApiUrl(`certificates/download/${enrollmentId}`);
    return this.http.get(url, { responseType: 'blob' });
  }
}
