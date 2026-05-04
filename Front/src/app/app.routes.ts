import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { CrearSolicitud } from './componentes/solicitudes/crear/crear';
import { SolicitudesList } from './componentes/solicitudes/list/list';
import { SolicitudDetalle } from './componentes/solicitudes/detalle/detalle';
import { ClasificarSolicitud } from './componentes/solicitudes/clasificar/clasificar';
import { PriorizarSolicitud } from './componentes/solicitudes/priorizar/priorizar';
import { AsignarSolicitud } from './componentes/solicitudes/asignar/asignar';
import { CambiarEstadoSolicitud } from './componentes/solicitudes/cambiar-estado/cambiar-estado';
import { CerrarSolicitud } from './componentes/solicitudes/cerrar/cerrar';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'solicitudes', component: SolicitudesList },
  { path: 'solicitudes/crear', component: CrearSolicitud },
  { path: 'solicitudes/:codigo', component: SolicitudDetalle },
  { path: 'solicitudes/:codigo/clasificar', component: ClasificarSolicitud },
  { path: 'solicitudes/:codigo/priorizar', component: PriorizarSolicitud },
  { path: 'solicitudes/:codigo/asignar', component: AsignarSolicitud },
  { path: 'solicitudes/:codigo/estado', component: CambiarEstadoSolicitud },
  { path: 'solicitudes/:codigo/cerrar', component: CerrarSolicitud },
  { path: '**', pathMatch: 'full', redirectTo: '/' },
];
