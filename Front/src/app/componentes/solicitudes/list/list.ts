import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  imports: [CommonModule, FormsModule],
  templateUrl: './list.html',
  styleUrls: ['./list.css'],
})
export class SolicitudesList {
  solicitudes: any[] = [];
  responsables: UsuarioResumen[] = [];
  selectedSolicitud: any = null;
  selectedTab: DashboardTab = 'registrar';
  mensaje = '';
  successMessage = '';
  loading = false;

  tipos = ['HOMOLOGACION', 'SOLICITUD_CUPOS', 'CONSULTA_ACADEMICA', 'REGISTRO_ASIGNATURA'];
  canales = ['PORTAL_WEB', 'SAC'];
  niveles = ['ALTA', 'MEDIA', 'BAJA'];

  crearForm = {
    tipoSolicitud: '',
    canalOrigen: 'PORTAL_WEB',
    descripcion: '',
  };

  actionForm = {
    tipoSolicitud: '',
    nivelPrioridad: '',
    identificacionResponsable: '',
    observacion: '',
  };

  constructor(
    private route: ActivatedRoute,
    private solicitudService: SolicitudService,
    private usuarioService: UsuarioService,
    public authService: AuthService
  ) {
    this.selectedTab = this.getInitialTab();
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
    this.loading = true;
    this.mensaje = '';
    const params = this.authService.canManageSolicitudes()
      ? { size: 30 }
      : { size: 30, identificacionResponsable: this.authService.getIdentification() || '' };

    this.solicitudService.listar(params).subscribe({
      next: (res) => {
        this.solicitudes = res?.content || res || [];
        if (!this.selectedSolicitud && this.solicitudes.length) {
          this.selectSolicitud(this.solicitudes[0], false);
        }
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.mensaje = this.getErrorMessage(err, 'Error cargando solicitudes');
      },
    });
  }

  loadResponsables(): void {
    this.usuarioService.listarResponsables().subscribe({
      next: (res) => (this.responsables = res || []),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error cargando responsables')),
    });
  }

  selectTab(tab: DashboardTab): void {
    this.selectedTab = tab;
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
    this.selectedSolicitud = solicitud;
    this.actionForm.observacion = '';
    this.actionForm.tipoSolicitud = this.rawCodigo(solicitud?.tipoSolicitud || solicitud?.tipo);
    this.actionForm.nivelPrioridad = this.rawCodigo(solicitud?.prioridad?.nivel || solicitud?.prioridad);

    if (loadDetail && solicitud?.codigo) {
      this.loadDetalle(solicitud.codigo);
    }
  }

  selectSolicitudByCodigo(codigo: string): void {
    const solicitud = this.solicitudes.find((item) => String(item.codigo) === String(codigo));
    if (solicitud) {
      this.selectSolicitud(solicitud);
    }
  }

  submitCrear(): void {
    this.clearMessages();
    if (!this.crearForm.tipoSolicitud || !this.crearForm.canalOrigen || !this.crearForm.descripcion.trim()) {
      this.mensaje = 'Completa tipo, canal y descripcion antes de registrar.';
      return;
    }

    this.loading = true;
    this.solicitudService.crear({ ...this.crearForm, descripcion: this.crearForm.descripcion.trim() }).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = `Solicitud #${res?.codigo || ''} registrada correctamente.`;
        this.crearForm = { tipoSolicitud: '', canalOrigen: 'PORTAL_WEB', descripcion: '' };
        if (this.authService.canConsultSolicitudes()) {
          this.load();
        }
      },
      error: (err) => {
        this.loading = false;
        this.mensaje = this.getErrorMessage(err, 'Error al crear solicitud');
      },
    });
  }

  submitClasificar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.actionForm.tipoSolicitud) {
      this.mensaje = 'Selecciona un tipo.';
      return;
    }
    if (!this.ensureObservation('La observacion de clasificacion')) {
      return;
    }

    this.solicitudService.clasificar(this.selectedSolicitud.codigo, {
      tipoSolicitud: this.actionForm.tipoSolicitud,
      observacion: this.actionForm.observacion.trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud clasificada correctamente.'),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error al clasificar')),
    });
  }

  submitPriorizar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.actionForm.nivelPrioridad) {
      this.mensaje = 'Selecciona un nivel de prioridad.';
      return;
    }
    if (!this.ensureObservation('La justificacion')) {
      return;
    }

    this.solicitudService.priorizar(this.selectedSolicitud.codigo, {
      nivelPrioridad: this.actionForm.nivelPrioridad,
      justificacion: this.actionForm.observacion.trim(),
    }).subscribe({
      next: () => this.afterAction('Prioridad actualizada correctamente.'),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error al priorizar')),
    });
  }

  submitAsignar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.actionForm.identificacionResponsable.trim()) {
      this.mensaje = 'Indica la identificacion del responsable.';
      return;
    }
    if (this.actionForm.identificacionResponsable.trim().length < 5) {
      this.mensaje = 'La identificacion del responsable debe tener al menos 5 caracteres.';
      return;
    }
    if (!this.ensureObservation('La observacion de asignacion')) {
      return;
    }

    this.solicitudService.asignar(this.selectedSolicitud.codigo, {
      identificacionResponsable: this.actionForm.identificacionResponsable.trim(),
      observacion: this.actionForm.observacion.trim(),
    }).subscribe({
      next: () => this.afterAction('Responsable asignado correctamente.'),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error al asignar')),
    });
  }

  submitAtender(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.ensureObservation('La observacion de atencion')) {
      return;
    }

    this.solicitudService.cambiarEstado(this.selectedSolicitud.codigo, {
      nuevoEstado: 'ATENDIDA',
      observacion: this.actionForm.observacion.trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud marcada como atendida.'),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error al atender la solicitud')),
    });
  }

  submitCerrar(): void {
    if (!this.ensureSelection()) {
      return;
    }
    if (!this.ensureObservation('La observacion de cierre')) {
      return;
    }

    this.solicitudService.cerrar(this.selectedSolicitud.codigo, {
      observacionCierre: this.actionForm.observacion.trim(),
    }).subscribe({
      next: () => this.afterAction('Solicitud cerrada correctamente.'),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error al cerrar')),
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
      next: (res) => (this.selectedSolicitud = res),
      error: (err) => (this.mensaje = this.getErrorMessage(err, 'Error cargando detalle')),
    });
  }

  private afterAction(message: string): void {
    this.successMessage = message;
    this.mensaje = '';
    this.actionForm.observacion = '';
    this.loadDetalle(this.selectedSolicitud.codigo);
    this.load();
  }

  private ensureSelection(): boolean {
    this.clearMessages();
    if (!this.selectedSolicitud?.codigo) {
      this.mensaje = 'Selecciona una solicitud.';
      return false;
    }
    return true;
  }

  private clearMessages(): void {
    this.mensaje = '';
    this.successMessage = '';
  }

  responsableLabel(responsable: UsuarioResumen): string {
    return `${responsable.nombre} - ${responsable.identificacion}`;
  }

  private ensureObservation(label: string): boolean {
    const value = this.actionForm.observacion.trim();
    if (!value) {
      this.mensaje = `${label} es obligatoria.`;
      return false;
    }
    if (value.length < 5 || value.length > 500) {
      this.mensaje = `${label} debe tener entre 5 y 500 caracteres.`;
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
