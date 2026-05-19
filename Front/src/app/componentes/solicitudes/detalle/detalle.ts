import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-solicitud-detalle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle.html',
  styleUrls: ['./detalle.css'],
})
export class SolicitudDetalle {
  solicitud: any = null;
  mensaje = '';
  codigo = 0;

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
    if (this.codigo) this.load();
  }

  load() {
    this.solicitudService.detalle(this.codigo).subscribe({
      next: (res) => (this.solicitud = res),
      error: (err) => (this.mensaje = err?.error?.message || 'Error cargando detalle'),
    });
  }
}
