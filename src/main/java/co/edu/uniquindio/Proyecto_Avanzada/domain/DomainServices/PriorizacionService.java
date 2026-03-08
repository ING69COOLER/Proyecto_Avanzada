package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;

@Service
public class PriorizacionService {
    //RN3 el usuario debe de poder priorizar segun su rol
    public void priorizarSolicitud(Usuario usuario, String justifiacion, Solicitud solicitud, NivelPrioridad prioridad){
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (justifiacion == null) {
            throw new IllegalArgumentException("La justificación no puede ser nula");
        }
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
        if (!usuario.puedePriorizar()) {
            throw new IllegalArgumentException("El usuario no puede priorizar");
        }

        solicitud.priorizarSolicitud(prioridad, justifiacion);


    }
}
