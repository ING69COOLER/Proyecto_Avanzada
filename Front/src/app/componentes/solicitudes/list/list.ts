import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';
import { AuthService, UserRole } from '../../../services/auth.service';
import { UsuarioResumen, UsuarioService } from '../../../services/usuario.service';

type DashboardTab = 'registrar' | 'listado' | 'clasificar' | 'priorizar' | 'asignar' | 'atender' | 'cerrar';

interface DashboardTabOption {
  id: DashboardTab;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-solicitudes-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './list.html',
  styleUrls: ['./list.css'],
})
export class SolicitudesList {
  private fb = inject(FormBuilder);

  solicitudes = signal<any[]>([]);
  responsables = signal<UsuarioResumen[]>([]);
  selectedSolicitud = signal<any | null>(null);
  selectedTab = signal<DashboardTab>('registrar');
  mensaje = signal('');
  successMessage = signal('');
  loading = signal(false);

  tipos = ['HOMOLOGACION', 'SOLICITUD_CUPOS', 'CONSULTA_ACADEMICA', 'REGISTRO_ASIGNATURA'];
  canales = ['PORTAL_WEB', 'SAC'];
  niveles = ['ALTA', 'MEDIA', 'BAJA'];

  crearForm = this.fb.group({
    tipoSolicitud: ['', [Validators.required]],
    canalOrigen: ['PORTAL_WEB', [Validators.required]],
    descripcion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(500)]],
  });

  selectionForm = this.fb.group({
    codigo: [''],
  });

  actionForm = this.fb.group({
    tipoSolicitud: [''],
    nivelPrioridad: [''],
    identificacionResponsable: [''],
    observacion: ['', [Validators.minLength(5), Validators.maxLength(500)]],
  });

  constructor(
    private route: ActivatedRoute,
    private solicitudService: SolicitudService,
    private usuarioService: UsuarioService,
    public authService: AuthService
  ) {
    this.selectedTab.set(this.getInitialTab());
    this.selectionForm.controls.codigo.valueChanges.subscribe((codigo) => this.selectSolicitudByCodigo(codigo || ''));
    if (this.authService.canConsultSolicitudes()) {
      this.load();
    }
    if (this.authService.canManageSolicitudes()) {
      this.loadResponsables();
    }
  }

  get role(): UserRole | null {
    return this.authService.getRole();
  }

  get visibleTabs(): DashboardTabOption[] {
    const tabs: DashboardTabOption[] = [];

    if (this.authService.canRegisterSolicitudes()) {
      tabs.push({ id: 'registrar', label: 'Registrar solicitud', icon: 'bi-clipboard-plus' });
    }

    if (this.authService.canConsultSolicitudes()) {
      tabs.push({
        id: 'listado',
        label: this.authService.hasRole('ESTUDIANTE', 'DOCENTE', 'ADMINISTRATIVO') ? 'Mis solicitudes' : 'Bandeja',
        icon: 'bi-inboxes',
      });
    }

    if (this.authService.canManageSolicitudes()) {
      tabs.push(
        { id: 'clasificar', label: 'Clasificar', icon: 'bi-tags' },
        { id: 'priorizar', label: 'Priorizar', icon: 'bi-sort-up' },
        { id: 'asignar', label: 'Asignar', icon: 'bi-person-check' },
        { id: 'cerrar', label: 'Cerrar', icon: 'bi-check2-circle' }
      );
    }

    if (this.authService.canAttendSolicitudes()) {
      tabs.push({ id: 'atender', label: 'Atender', icon: 'bi-tools' });
    }

    return tabs;
  }

  load(): void {
    this.loading.set(true);
    this.mensaje.set('');
    const params = this.authService.canManageSolicitudes()
      ? { size: 30 }
      : { size: 30, identificacionResponsable: this.authService.getIdentification() || '' };

    this.solicitudService.listar(params).subscribe({
      next: (res) => {
        const solicitudes = res?.content || res || [];
        this.solicitudes.set(solicitudes);
        if (!this.selectedSolicitud() && solicitudes.length) {
          this.selectSolicitud(solicitudes[0], false);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.mensaje.set(this.getErrorMessage(err, 'Error cargando solicitudes'));
      },
    });
  }

  loadResponsables(): void {
    this.usuarioService.listarResponsables().subscribe({
      next: (res) => this.responsables.set(res || []),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error cargando responsables')),
    });
  }

  selectTab(tab: DashboardTab): void {
    this.selectedTab.set(tab);
    this.clearMessages();
  }

  private getInitialTab(): DashboardTab {
    const requestedTab = this.route.snapshot.queryParamMap.get('tab') as DashboardTab | null;
    const defaultTab = this.visibleTabs[0]?.id || 'registrar';
    return requestedTab && this.visibleTabs.some((tab) => tab.id === requestedTab)
      ? requestedTab
      : defaultTab;
  }

  selectSolicitud(solicitud: any, loadDetail = true): void {
    this.selectedSolicitud.set(solicitud);
    this.selectionForm.patchValue({ codigo: solicitud?.codigo ? String(solicitud.codigo) : '' }, { emitEvent: false });
    this.actionForm.patchValue({
      observacion: '',
      tipoSolicitud: this.rawCodigo(solicitud?.tipoSolicitud || solicitud?.tipo),
      nivelPrioridad: this.rawCodigo(solicitud?.prioridad?.nivel || solicitud?.prioridad),
      identificacionResponsable: '',
    });
    this.actionForm.markAsUntouched();

    if (loadDetail && solicitud?.codigo) {
      this.loadDetalle(solicitud.codigo);
    }
  }

  selectSolicitudByCodigo(codigo: string): void {
    const solicitud = this.solicitudes().find((item) => String(item.codigo) === String(codigo));
    if (solicitud) {
      this.selectSolicitud(solicitud);
    }
  }

  submitCrear(): void {
    this.clearMessages();
    if (this.crearForm.invalid) {
      this.crearForm.markAllAsTouched();
      return;
    }

    const formValue = this.crearForm.getRawValue();
    this.loading.set(true);
    this.solicitudService.crear({
      tipoSolicitud: formValue.tipoSolicitud || '',
      canalOrigen: formValue.canalOrigen || '',
      descripcion: (formValue.descripcion || '').trim(),
    }).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.successMessage.set(`Solicitud #${res?.codigo || ''} registrada correctamente.`);
        this.crearForm.reset({ tipoSolicitud: '', canalOrigen: 'PORTAL_WEB', descripcion: '' });
        if (this.authService.canConsultSolicitudes()) {
          this.load();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.mensaje.set(this.getErrorMessage(err, 'Error al crear solicitud'));
      },
    });
  }

  submitClasificar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.actionForm.controls.tipoSolicitud.value) {
      this.actionForm.controls.tipoSolicitud.markAsTouched();
      this.mensaje.set('Selecciona un tipo.');
      return;
    }
    if (!this.ensureObservation('La observacion de clasificacion')) {
      return;
    }

    const selected = this.selectedSolicitud();
    if (!selected) return;
    const formValue = this.actionForm.getRawValue();
    this.solicitudService.clasificar(selected.codigo, {
      tipoSolicitud: formValue.tipoSolicitud,
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud clasificada correctamente.'),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error al clasificar')),
    });
  }

  submitPriorizar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.actionForm.controls.nivelPrioridad.value) {
      this.actionForm.controls.nivelPrioridad.markAsTouched();
      this.mensaje.set('Selecciona un nivel de prioridad.');
      return;
    }
    if (!this.ensureObservation('La justificacion')) {
      return;
    }

    const selected = this.selectedSolicitud();
    if (!selected) return;
    const formValue = this.actionForm.getRawValue();
    this.solicitudService.priorizar(selected.codigo, {
      nivelPrioridad: formValue.nivelPrioridad,
      justificacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.afterAction('Prioridad actualizada correctamente.'),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error al priorizar')),
    });
  }

  submitAsignar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    const formValue = this.actionForm.getRawValue();
    const identificacion = (formValue.identificacionResponsable || '').trim();
    if (!identificacion) {
      this.actionForm.controls.identificacionResponsable.markAsTouched();
      this.mensaje.set('Indica la identificacion del responsable.');
      return;
    }
    if (identificacion.length < 5) {
      this.actionForm.controls.identificacionResponsable.markAsTouched();
      this.mensaje.set('La identificacion del responsable debe tener al menos 5 caracteres.');
      return;
    }
    if (!this.ensureObservation('La observacion de asignacion')) {
      return;
    }

    const selected = this.selectedSolicitud();
    if (!selected) return;
    this.solicitudService.asignar(selected.codigo, {
      identificacionResponsable: identificacion,
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.afterAction('Responsable asignado correctamente.'),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error al asignar')),
    });
  }

  submitAtender(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.ensureObservation('La observacion de atencion')) {
      return;
    }

    const selected = this.selectedSolicitud();
    if (!selected) return;
    const formValue = this.actionForm.getRawValue();
    this.solicitudService.cambiarEstado(selected.codigo, {
      nuevoEstado: 'ATENDIDA',
      observacion: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud marcada como atendida.'),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error al atender la solicitud')),
    });
  }

  submitCerrar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.ensureObservation('La observacion de cierre')) {
      return;
    }

    const selected = this.selectedSolicitud();
    if (!selected) return;
    const formValue = this.actionForm.getRawValue();
    this.solicitudService.cerrar(selected.codigo, {
      observacionCierre: (formValue.observacion || '').trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud cerrada correctamente.'),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error al cerrar')),
    });
  }

  displayCodigo(value: any): string {
    const codigo = this.rawCodigo(value);
    if (!codigo) return 'Sin asignar';
    return codigo.toLowerCase().replaceAll('_', ' ');
  }

  rawCodigo(value: any): string {
    if (!value) return '';
    if (typeof value === 'string') return value;
    if (value.codigo?.codigo) return value.codigo.codigo;
    if (value.nivel?.codigo) return value.nivel.codigo;
    if (value.codigo) return value.codigo;
    if (value.nivel) return this.rawCodigo(value.nivel);
    if (value.nombre) return value.nombre;
    return String(value);
  }

  private loadDetalle(codigo: number): void {
    this.solicitudService.detalle(codigo).subscribe({
      next: (res) => this.selectedSolicitud.set(res),
      error: (err) => this.mensaje.set(this.getErrorMessage(err, 'Error cargando detalle')),
    });
  }

  private afterAction(message: string): void {
    this.successMessage.set(message);
    this.mensaje.set('');
    this.actionForm.patchValue({ observacion: '' });
    this.actionForm.markAsUntouched();
    const selected = this.selectedSolicitud();
    if (!selected) return;
    this.loadDetalle(selected.codigo);
    this.load();
  }

  private ensureSelection(): boolean {
    this.clearMessages();
    if (!this.selectedSolicitud()?.codigo) {
      this.mensaje.set('Selecciona una solicitud.');
      return false;
    }
    return true;
  }

  private clearMessages(): void {
    this.mensaje.set('');
    this.successMessage.set('');
  }

  responsableLabel(responsable: UsuarioResumen): string {
    return `${responsable.nombre} - ${responsable.identificacion}`;
  }

  usuarioLabel(usuario: any): string {
    if (!usuario) return 'Sin asignar';
    const nombre = usuario.nombre || 'Usuario';
    const identificacion = usuario.identificacion ? ` - ${usuario.identificacion}` : '';
    return `${nombre}${identificacion}`;
  }

  responsableActual(solicitud: any): string {
    const historial = solicitud?.historial || [];
    const asignacion = [...historial]
      .reverse()
      .find((evento) => this.rawCodigo(evento?.accion) === 'ASIGNACION');

    return this.usuarioLabel(asignacion?.responsable);
  }

  private ensureObservation(label: string): boolean {
    const control = this.actionForm.controls.observacion;
    const value = (control.value || '').trim();
    if (!value) {
      control.markAsTouched();
      this.mensaje.set(`${label} es obligatoria.`);
      return false;
    }
    if (value.length < 5 || value.length > 500) {
      control.markAsTouched();
      this.mensaje.set(`${label} debe tener entre 5 y 500 caracteres.`);
      return false;
    }
    return true;
  }

  private getErrorMessage(err: any, fallback: string): string {
    const validationErrors = err?.error?.validationErrors;
    if (validationErrors && typeof validationErrors === 'object') {
      const firstError = Object.values(validationErrors)[0];
      if (firstError) return String(firstError);
    }

    const message = err?.error?.message || fallback;
    if (typeof message === 'string' && message.includes('default message [')) {
      const matches = [...message.matchAll(/default message \[([^\]]+)\]/g)];
      return matches.at(-1)?.[1] || fallback;
    }

    return message;
  }
}
