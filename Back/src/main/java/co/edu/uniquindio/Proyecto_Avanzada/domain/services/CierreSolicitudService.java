package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

/**
 * RF-04: Servicio de Dominio para cierre de solicitudes
 * Gestiona la transicion final del ciclo de vida de una solicitud al estado CERRADA
 * 
 *
 */

import org.springframework.stereotype.Service;

// caso de uso
@Service
public class CierreSolicitudService {
    
    /**
     * RF-04/RF-08: Cierra una solicitud academica
     * Valida que:
     * - El usuario sea COORDINADOR (unico autorizado para cerrar)
     * - La solicitud exista y no este ya cerrada
     * - La observacion sea valida
     * 
     * @param user Usuario que realiza el cierre
     * @param solicitud Solicitud a cerrar
     * @param observacion Observacion sobre el cierre
     * @throws SolicitudException 
     */
    public Solicitud cerrarSolicitud(Usuario user, Solicitud solicitud, String observacion) throws SolicitudException {
        // Validar usuario autorizado
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!user.puedeCerrarSolicitud()) {
            throw new IllegalArgumentException("El usuario no tiene permisos para cerrar solicitudes. Solo COORDINADOR puede cerrar.");
        }
        
        // Validar solicitud
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        
        // Validar observacion
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La observacion no puede estar vacia");
        }
        
        // RF-06: Registrar en historial y cambiar estado
        solicitud.cerrarSolicitud(user, observacion);
        
        return solicitud;
    }
}
