import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-cambiar-estado-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cambiar-estado.html',
  styleUrls: ['./cambiar-estado.css'],
})
export class CambiarEstadoSolicitud {
  codigo = 0;
  estado = '';
  observacion = '';
  mensaje = '';

  estados = ['REGISTRADA','CLASIFICADA','EN_ATENCION','ATENDIDA','CERRADA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (!this.estado) { this.mensaje = 'Seleccione un estado'; return }
    this.solicitudService.cambiarEstado(this.codigo, { nuevoEstado: this.estado, observacion: this.observacion }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al cambiar estado'),
    })
  }
}
