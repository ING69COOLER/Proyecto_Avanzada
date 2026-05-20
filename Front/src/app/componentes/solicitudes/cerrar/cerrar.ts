import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-cerrar-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cerrar.html',
  styleUrls: ['./cerrar.css'],
})
export class CerrarSolicitud {
  private fb = inject(FormBuilder);
  codigo = 0;
  mensaje = signal('');

  cerrarForm = this.fb.group({
    observacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit(): void {
    if (this.cerrarForm.invalid) {
      this.cerrarForm.markAllAsTouched();
      return;
    }

    const formValue = this.cerrarForm.getRawValue();
    this.solicitudService.cerrar(this.codigo, {
      observacionCierre: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al cerrar'),
    });
  }
}
