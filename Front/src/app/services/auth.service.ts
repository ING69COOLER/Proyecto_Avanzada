import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest { username: string; password: string }
export interface TokenResponse { token: string }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = '/auth';
  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/login`, payload);
  }

  register(payload: any): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/register`, payload);
  }
}
