import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-priorizar-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './priorizar.html',
  styleUrls: ['./priorizar.css'],
})
export class PriorizarSolicitud {
  private fb = inject(FormBuilder);
  codigo = 0;
  mensaje = signal('');

  priorizarForm = this.fb.group({
    nivel: ['', [Validators.required]],
    justificacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  niveles = ['ALTA','MEDIA','BAJA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (this.priorizarForm.invalid) {
      this.priorizarForm.markAllAsTouched();
      return;
    }
    const formValue = this.priorizarForm.getRawValue();
    this.solicitudService.priorizar(this.codigo, {
      nivelPrioridad: formValue.nivel,
      justificacion: (formValue.justificacion || '').trim(),
    }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al priorizar'),
    })
  }
}
