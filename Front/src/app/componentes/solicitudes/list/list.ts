import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-solicitudes-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './list.html',
  styleUrls: ['./list.css'],
})
export class SolicitudesList {
  solicitudes: any[] = [];
  mensaje = '';

  constructor(private solicitudService: SolicitudService) {
    this.load();
  }

  load() {
    this.solicitudService.listar().subscribe({
      next: (res) => {
        // si es Page, mapear content
        this.solicitudes = res?.content || res || [];
      },
      error: (err) => (this.mensaje = err?.error?.message || 'Error cargando solicitudes'),
    });
  }
}
