import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  identificacion: string;
  correo: string;
  password: string;
  activo: boolean;
  rol: { codigo: string };
}

export interface TokenResponse {
  token: string;
}

export type UserRole = 'ESTUDIANTE' | 'ADMINISTRATIVO' | 'COORDINADOR' | 'DOCENTE';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = '/auth';
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/login`, payload).pipe(
      tap(response => this.saveToken(response.token))
    );
  }

  register(payload: RegisterRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/register`, payload).pipe(
      tap(response => this.saveToken(response.token))
    );
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() < expiry;
    } catch {
      return false;
    }
  }

  getUserInfo(): any {
    const token = this.getToken();
    if (!token) return null;

    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }

  getRole(): UserRole | null {
    const role = this.getUserInfo()?.role;
    return role || null;
  }

  getIdentification(): string | null {
    return this.getUserInfo()?.sub || null;
  }

  hasRole(...roles: UserRole[]): boolean {
    const role = this.getRole();
    return !!role && roles.includes(role);
  }

  canRegisterSolicitudes(): boolean {
    return this.hasRole('ESTUDIANTE', 'ADMINISTRATIVO');
  }

  canManageSolicitudes(): boolean {
    return this.hasRole('COORDINADOR');
  }

  canAttendSolicitudes(): boolean {
    return this.hasRole('DOCENTE', 'ADMINISTRATIVO');
  }

  canConsultSolicitudes(): boolean {
    return this.canManageSolicitudes() || this.canAttendSolicitudes() || this.hasRole('ESTUDIANTE');
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }
}
