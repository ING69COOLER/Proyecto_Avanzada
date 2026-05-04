import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-crear-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crear.html',
  styleUrls: ['./crear.css'],
})
export class CrearSolicitud {
  tipoSolicitud = '';
  descripcion = '';
  canalOrigen = '';
  mensaje = '';

  tipos = ['HOMOLOGACION','SOLICITUD_CUPOS','CONSULTA_ACADEMICA','REGISTRO_ASIGNATURA'];
  canales = ['PORTAL_WEB','SAC'];

  constructor(private solicitudService: SolicitudService, private router: Router) {}

  submit() {
    if (!this.tipoSolicitud || !this.descripcion || !this.canalOrigen) {
      this.mensaje = 'Todos los campos son obligatorios';
      return;
    }
    const payload = {
      tipoSolicitud: this.tipoSolicitud,
      descripcion: this.descripcion,
      canalOrigen: this.canalOrigen,
    };
    this.solicitudService.crear(payload).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al crear solicitud'),
    });
  }
}
