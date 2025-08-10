import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { buildApiUrl } from '../constants';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message?: string;
  user?: {
    id: number;
    username: string;
    type: number;
    active: boolean;
    isEmailVerified: boolean;
    person?: {
      firstName: string;
      lastName: string;
      email: string;
      documentNumber: string;
    };
  };
  errorCode?: string;
}

export interface User {
  id: number;
  username: string;
  type: number;
  active: boolean;
  isEmailVerified: boolean;
  person?: {
    firstName: string;
    lastName: string;
    email: string;
    documentNumber: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is already logged in (localStorage)
    this.loadUserFromStorage();
  }

  /**
   * Login with username and password
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    const url = buildApiUrl('auth/login');

    return this.http.post<LoginResponse>(url, credentials)
      .pipe(
        tap(response => {
          console.log('🔍 AuthService - Login response:', response);
          if (response.success && response.user) {
            console.log('💾 AuthService - Setting current user:', response.user);
            this.setCurrentUser(response.user);
            console.log('✅ AuthService - User set successfully');
          }
        }),
        catchError(error => {
          console.error('Login error:', error);
          // Return user-friendly error
          const errorResponse: LoginResponse = {
            success: false,
            errorCode: error.error?.errorCode || 'NETWORK_ERROR',
            message: error.error?.message || 'Error de conexión. Intente nuevamente.'
          };
          return of(errorResponse);
        })
      );
  }

  /**
   * Logout user
   */
  logout(): Observable<any> {
    const url = buildApiUrl('auth/logout');

    return this.http.post(url, {})
      .pipe(
        tap(() => {
          this.clearCurrentUser();
        }),
        catchError(error => {
          console.error('Logout error:', error);
          // Clear user anyway
          this.clearCurrentUser();
          return of({ success: true });
        })
      );
  }

  /**
   * Validate current session
   */
  validateSession(): Observable<any> {
    const url = buildApiUrl('auth/validate');

    return this.http.get(url)
      .pipe(
        catchError(error => {
          console.error('Session validation error:', error);
          this.clearCurrentUser();
          return of({ success: false });
        })
      );
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.type === 1 : false;
  }

  /**
   * Check if user is student
   */
  isStudent(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.type === 3 : false;
  }

  /**
   * Check if user is instructor
   */
  isInstructor(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.type === 2 : false;
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Set current user and save to localStorage
   */
  private setCurrentUser(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }

  /**
   * Clear current user and remove from localStorage
   */
  private clearCurrentUser(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  /**
   * Load user from localStorage on service initialization
   */
  private loadUserFromStorage(): void {
    try {
      const userData = localStorage.getItem('currentUser');
      if (userData) {
        const user: User = JSON.parse(userData);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      }
    } catch (error) {
      console.error('Error loading user from storage:', error);
      this.clearCurrentUser();
    }
  }

  /**
   * Get user full name
   */
  getUserFullName(): string {
    const user = this.getCurrentUser();
    if (user?.person) {
      return `${user.person.firstName} ${user.person.lastName}`;
    }
    return user?.username || 'Usuario';
  }

  /**
   * Get user type text
   */
  getUserTypeText(): string {
    const user = this.getCurrentUser();
    if (!user) return 'Invitado';

    switch (user.type) {
      case 1: return 'Administrador';
      case 2: return 'Instructor';
      case 3: return 'Estudiante';
      default: return 'Usuario';
    }
  }
}
