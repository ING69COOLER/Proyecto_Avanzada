import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SolicitudService } from '../../../services/solicitud.service';

@Component({
  selector: 'app-crear-solicitud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './crear.html',
  styleUrls: ['./crear.css'],
})
export class CrearSolicitud {
  private fb = inject(FormBuilder);
  mensaje = signal('');

  solicitudForm = this.fb.group({
    tipoSolicitud: ['', [Validators.required]],
    canalOrigen: ['', [Validators.required]],
    descripcion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  tipos = ['HOMOLOGACION','SOLICITUD_CUPOS','CONSULTA_ACADEMICA','REGISTRO_ASIGNATURA'];
  canales = ['PORTAL_WEB','SAC'];

  constructor(private solicitudService: SolicitudService, private router: Router) {}

  submit() {
    if (this.solicitudForm.invalid) {
      this.solicitudForm.markAllAsTouched();
      return;
    }
    const formValue = this.solicitudForm.getRawValue();
    const payload = {
      tipoSolicitud: formValue.tipoSolicitud || '',
      descripcion: (formValue.descripcion || '').trim(),
      canalOrigen: formValue.canalOrigen || '',
    };
    this.solicitudService.crear(payload).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => this.mensaje.set(err?.error?.message || 'Error al crear solicitud'),
    });
  }
}
