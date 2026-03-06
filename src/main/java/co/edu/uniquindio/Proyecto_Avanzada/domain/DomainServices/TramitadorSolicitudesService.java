package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
/*
Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad) */
public class TramitadorSolicitudesService {
    // metodo para crear solicitud incluyendo reglas validadas en usuario
    public void registrarSolicitudBasica(Usuario responsableCreacion, TipoSolicitud tipo, 
                                    String descripcion, CanalOrigen canalOrigen, 
                                    LocalDateTime fechaHoraRegisro, String identificacion){

        if(responsableCreacion.puedeRegistrarSolicitud()){
            RepositorioSolicitud.getInstancia().guardarSolicitud(new Solicitud(tipo, descripcion, canalOrigen, 
                    fechaHoraRegisro, identificacion,  null,  null,
                    null,  null));
        }
        
    }

    public void registrarSolicitudCompleta(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad){

        if(usuarioSolicitante.puedeRegistrarSolicitud()){
            RepositorioSolicitud.getInstancia().guardarSolicitud(new Solicitud( tipo,  descripcion,  canalOrigen, 
                     fechaHoraRegisro,  identificacion,  fechaCierre,  estado,
                     usuarioSolicitante,  prioridad));
        }

    }

}
