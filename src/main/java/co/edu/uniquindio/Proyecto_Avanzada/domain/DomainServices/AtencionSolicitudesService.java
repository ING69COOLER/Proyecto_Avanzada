package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.stereotype.Service;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;

/**
 * RF-04, RF-05: Servicio de Dominio para la atención de solicitudes
 * Gestiona la asignación de responsables y la atención de solicitudes
 */
@Service
public class AtencionSolicitudesService {
    public void asignarResponsable(Usuario user, Solicitud solicitud, String descripcion){
        if(user == null){
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if(solicitud == null){
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (!user.puedeAsignar()) {
            throw new IllegalArgumentException("Usuario invalido para asignacion de responsable");
        }

        solicitud.asignarResponsable(user, descripcion);
    }
// esta como tal no esta pero es transitoria para el cierre, asi que la tomo como "el tipo atendio la solicitud y la marca como completada"
    public void atenderSolicitud(Usuario user, Solicitud solicitud, String observacion){
        if(user == null){
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if(solicitud == null){
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if(observacion == null || observacion.trim().isEmpty()){
            throw new IllegalArgumentException("La observacion no puede ser vacia");
        }
        if (!solicitud.UsuarioPuedeAtender(user)) {
            throw new IllegalArgumentException("Usuario no es el responsable para atenderla");
        }
        if (!user.puedeAtender()) {
            throw new IllegalArgumentException("Usuario no tiene el rol para atenderla");
        }
        solicitud.atenderSolicitud(user, observacion);
    }


}
