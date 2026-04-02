package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;

public class ConsultaSolicitudesService {
   

    public List<Solicitud> consultarPorResponsable(List<Solicitud> solicitudes, Usuario responsable) {
        if (solicitudes == null) {
            throw new IllegalArgumentException("La lista de solicitudes no puede ser nula");
        }
        if (responsable == null) {
            throw new IllegalArgumentException("El usuario responsable no puede ser nulo");
        }
        if (!responsable.puedeAsignar() && !responsable.puedeAtenderSolicitud()) {
            throw new IllegalArgumentException("El usuario no tiene permisos para consultar solicitudes");
        }

        return solicitudes.stream()
                .filter(s -> s.obtenerUsuariosDeHistorias().stream()
                        .anyMatch(u -> u.getIdentificacion().equals(responsable.getIdentificacion())))
                .collect(Collectors.toList());
    }

}
