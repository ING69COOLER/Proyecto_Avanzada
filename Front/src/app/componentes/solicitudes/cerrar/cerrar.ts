import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-cerrar-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cerrar.html',
  styleUrls: ['./cerrar.css'],
})
export class CerrarSolicitud {
  codigo = 0;
  observacion = '';
  mensaje = '';

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (!this.observacion) { this.mensaje = 'Observación requerida'; return }
    this.solicitudService.cerrar(this.codigo, { observacionCierre: this.observacion }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al cerrar'),
    })
  }
}
