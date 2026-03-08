package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import java.util.Observable;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;

public class CierreSolicitudService {
    public void cerrarSolicitud(Usuario user, Solicitud solicitud, String observacion){
        if (!user.puedeCerrarSolicitud()) {
            throw new IllegalArgumentException("Usuario invalido para cerrar la solicitud");
        }
        //acabar gays
    }
}
