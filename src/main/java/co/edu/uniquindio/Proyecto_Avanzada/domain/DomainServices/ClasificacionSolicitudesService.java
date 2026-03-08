package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
public class ClasificacionSolicitudesService {
    private IRepositorioSolicitud repositorioSolicitud;
    //RN2 
    public void clasificarSolicitud(Solicitud solicitud, TipoSolicitud tipoSolicitud, Usuario usuario, String observacion){
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (tipoSolicitud == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        //RN2
        if (!usuario.puedeClasificarSolicitud()) {
            throw new IllegalArgumentException("el usuario no tiene el rango para clasificar la solicitud");
        }

        solicitud.clasificarSolicitud(tipoSolicitud);
        solicitud.crearHistoria(EstadoSolicitud.CLASIFICADA, TipoAccion.CLASIFICADA, usuario, observacion);
        repositorioSolicitud.guardarSolicitud(solicitud);
    }
}
