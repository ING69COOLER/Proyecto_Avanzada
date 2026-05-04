import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-priorizar-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './priorizar.html',
  styleUrls: ['./priorizar.css'],
})
export class PriorizarSolicitud {
  codigo = 0;
  nivel = '';
  justificacion = '';
  mensaje = '';

  niveles = ['ALTA','MEDIA','BAJA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (!this.nivel) { this.mensaje = 'Seleccione un nivel'; return }
    this.solicitudService.priorizar(this.codigo, { nivelPrioridad: this.nivel, justificacion: this.justificacion }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al priorizar'),
    })
  }
}
