package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;

import org.springframework.stereotype.Service;

@Service
public class ConsultaSolicitudesService {

    // cambiaria a que solo verifique el usuario (hecho)
    public void consultasValidacion(Usuario responsable) {
        if (responsable == null) {
            throw new IllegalArgumentException("El usuario responsable no puede ser nulo");
        }
        if (!responsable.puedeAsignar() && !responsable.puedeAtenderSolicitud()) {
            throw new IllegalArgumentException("El usuario no tiene permisos para consultar solicitudes");
        }
    }

    

}
