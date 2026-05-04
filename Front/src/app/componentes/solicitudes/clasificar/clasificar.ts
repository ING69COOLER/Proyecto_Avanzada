import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-clasificar-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clasificar.html',
  styleUrls: ['./clasificar.css'],
})
export class ClasificarSolicitud {
  codigo = 0;
  tipo = '';
  observacion = '';
  mensaje = '';

  tipos = ['HOMOLOGACION','SOLICITUD_CUPOS','CONSULTA_ACADEMICA','REGISTRO_ASIGNATURA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (!this.tipo) { this.mensaje = 'Seleccione un tipo'; return }
    this.solicitudService.clasificar(this.codigo, { tipoSolicitud: this.tipo, observacion: this.observacion }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => (this.mensaje = err?.error?.message || 'Error al clasificar'),
    })
  }
}
