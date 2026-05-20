import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-cambiar-estado-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cambiar-estado.html',
  styleUrls: ['./cambiar-estado.css'],
})
export class CambiarEstadoSolicitud {
  private fb = inject(FormBuilder);
  codigo = 0;
  mensaje = signal('');

  estadoForm = this.fb.group({
    estado: ['', [Validators.required]],
    observacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  estados = ['REGISTRADA','CLASIFICADA','EN_ATENCION','ATENDIDA','CERRADA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit(): void {
    if (this.estadoForm.invalid) {
      this.estadoForm.markAllAsTouched();
      return;
    }

    const formValue = this.estadoForm.getRawValue();
    this.solicitudService.cambiarEstado(this.codigo, {
      nuevoEstado: formValue.estado,
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al cambiar estado'),
    });
  }
}
