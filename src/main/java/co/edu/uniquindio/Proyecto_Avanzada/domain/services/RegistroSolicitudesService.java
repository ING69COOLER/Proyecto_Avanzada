package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
/*
Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad) */
//RN1
//caso de uso
@Service
public class RegistroSolicitudesService {

    // metodo para crear solicitud incluyendo reglas validadas en usuario
    public Solicitud registrarSolicitudBasica(Usuario responsableCreacion, TipoSolicitud tipo, 
                                    String descripcion, CanalOrigen canalOrigen){

        if(!responsableCreacion.puedeRegistrarSolicitud()){
            throw new IllegalArgumentException("EL usuario es invalido");
        }

        LocalDateTime fechaHoraRegistro = LocalDateTime.now();

        Solicitud solicitud = new Solicitud(tipo, descripcion, canalOrigen, 
                    fechaHoraRegistro,  null,  null,
                    responsableCreacion,  null);

        return solicitud;

    }

    public Solicitud registrarSolicitudCompleta(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad){

        if(!usuarioSolicitante.puedeRegistrarSolicitud()){
            throw new IllegalArgumentException("EL usuario es invalido");
        }

        LocalDateTime fechaHoraRegistro = LocalDateTime.now();

        Solicitud solicitud = new Solicitud(tipo, descripcion, canalOrigen, 
                    fechaHoraRegistro,  fechaCierre,  estado,
                    usuarioSolicitante,  prioridad);

        return solicitud;

    }
}
