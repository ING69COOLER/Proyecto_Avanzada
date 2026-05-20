import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-asignar-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './asignar.html',
  styleUrls: ['./asignar.css'],
})
export class AsignarSolicitud {
  private fb = inject(FormBuilder);
  codigo = 0;
  mensaje = signal('');

  asignarForm = this.fb.group({
    identificacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
    observacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit(): void {
    if (this.asignarForm.invalid) {
      this.asignarForm.markAllAsTouched();
      return;
    }

    const formValue = this.asignarForm.getRawValue();
    this.solicitudService.asignar(this.codigo, {
      identificacionResponsable: (formValue.identificacion || '').trim(),
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al asignar'),
    });
  }
}
