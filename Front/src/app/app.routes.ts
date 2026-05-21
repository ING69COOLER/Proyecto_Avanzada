import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { SolicitudesList } from './componentes/solicitudes/list/list';
import { SolicitudDetalle } from './componentes/solicitudes/detalle/detalle';
import { ClasificarSolicitud } from './componentes/solicitudes/clasificar/clasificar';
import { PriorizarSolicitud } from './componentes/solicitudes/priorizar/priorizar';
import { AsignarSolicitud } from './componentes/solicitudes/asignar/asignar';
import { CambiarEstadoSolicitud } from './componentes/solicitudes/cambiar-estado/cambiar-estado';
import { CerrarSolicitud } from './componentes/solicitudes/cerrar/cerrar';
import { authGuard } from './services/auth.guard';
import { roleGuard } from './guards/role.guard';

import { CrearSolicitud } from './componentes/solicitudes/crear/crear';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { 
    path: 'solicitudes', 
    component: SolicitudesList, 
    canActivate: [authGuard] 
  },
  { 
    path: 'solicitudes/crear', 
    component: CrearSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ESTUDIANTE', 'ADMINISTRATIVO'] }
  },
  { 
    path: 'solicitudes/:codigo', 
    component: SolicitudDetalle, 
    canActivate: [authGuard] 
  },
  { 
    path: 'solicitudes/:codigo/clasificar', 
    component: ClasificarSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COORDINADOR'] }
  },
  { 
    path: 'solicitudes/:codigo/priorizar', 
    component: PriorizarSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COORDINADOR'] }
  },
  { 
    path: 'solicitudes/:codigo/asignar', 
    component: AsignarSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COORDINADOR'] }
  },
  { 
    path: 'solicitudes/:codigo/estado', 
    component: CambiarEstadoSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['DOCENTE', 'ADMINISTRATIVO'] }
  },
  { 
    path: 'solicitudes/:codigo/cerrar', 
    component: CerrarSolicitud, 
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COORDINADOR'] }
  },
  { path: '**', pathMatch: 'full', redirectTo: '/' },
];
