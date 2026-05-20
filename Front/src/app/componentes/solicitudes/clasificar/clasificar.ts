import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-clasificar-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './clasificar.html',
  styleUrls: ['./clasificar.css'],
})
export class ClasificarSolicitud {
  private fb = inject(FormBuilder);
  codigo = 0;
  mensaje = signal('');

  clasificarForm = this.fb.group({
    tipo: ['', [Validators.required]],
    observacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  tipos = ['HOMOLOGACION','SOLICITUD_CUPOS','CONSULTA_ACADEMICA','REGISTRO_ASIGNATURA'];

  constructor(private route: ActivatedRoute, private solicitudService: SolicitudService, private router: Router) {
    this.codigo = Number(this.route.snapshot.paramMap.get('codigo'));
  }

  submit() {
    if (this.clasificarForm.invalid) {
      this.clasificarForm.markAllAsTouched();
      return;
    }
    const formValue = this.clasificarForm.getRawValue();
    this.solicitudService.clasificar(this.codigo, {
      tipoSolicitud: formValue.tipo,
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.router.navigate(['/solicitudes', this.codigo]),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al clasificar'),
    })
  }
}
