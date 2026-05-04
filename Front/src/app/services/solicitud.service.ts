import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CrearSolicitudRequest {
  tipoSolicitud: string;
  descripcion: string;
  canalOrigen: string;
}

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private base = '/api/solicitudes';
  constructor(private http: HttpClient) {}

  crear(request: CrearSolicitudRequest): Observable<any> {
    return this.http.post<any>(`${this.base}`, request);
  }

  listar(params?: any): Observable<any> {
    return this.http.get<any>(`${this.base}` , { params });
  }

  detalle(codigo: number): Observable<any> {
    return this.http.get<any>(`${this.base}/${codigo}`);
  }

  clasificar(codigo: number, payload: any) {
    return this.http.patch<any>(`${this.base}/${codigo}/clasificacion`, payload);
  }

  priorizar(codigo: number, payload: any) {
    return this.http.patch<any>(`${this.base}/${codigo}/prioridad`, payload);
  }

  asignar(codigo: number, payload: any) {
    return this.http.patch<any>(`${this.base}/${codigo}/asignacion`, payload);
  }

  cambiarEstado(codigo: number, payload: any) {
    return this.http.patch<any>(`${this.base}/${codigo}/estado`, payload);
  }

  cerrar(codigo: number, payload: any) {
    return this.http.patch<any>(`${this.base}/${codigo}/cierre`, payload);
  }
}
