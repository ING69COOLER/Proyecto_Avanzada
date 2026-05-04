import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-asignar-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './asignar.html',
  styleUrls: ['./asignar.css'],
})
export class AsignarSolicitud {
  codigo = 0;
  identificacion = '';
  observacion = '';
  mensaje = '';

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (!this.identificacion) { this.mensaje = 'Identificación del responsable requerida'; return }
    this.solicitudService.asignar(this.codigo, { identificacionResponsable: this.identificacion, observacion: this.observacion }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al asignar'),
    })
  }
}
