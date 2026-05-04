import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CrearUsuarioRequest {
  nombre: string;
  identificacion: string;
  correo: string;
  activo: boolean;
  rol: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private base = '/api/usuarios';
  constructor(private http: HttpClient) {}

  registrar(request: CrearUsuarioRequest): Observable<any> {
    return this.http.post<any>(`${this.base}/registro`, request);
  }
}
