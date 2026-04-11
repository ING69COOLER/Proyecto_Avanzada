package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * RF-02: Servicio de Dominio para la clasificacion de solicitudes academicas.
 *
 * Permite categorizar una solicitud segun su tipo (registro de asignatura,
 * homologacion, cancelacion, cupos, consulta academica, etc.).
 *
 * RF-13: Verifica que el usuario tenga el rol COORDINADOR antes de clasificar
 * (RN2).
 */

// caso de uso
@Service
public class ClasificacionSolicitudesService {

    /**
     * RF-02: Clasifica una solicitud asignandole un tipo especifico.
     * RF-13: Solo el COORDINADOR puede ejecutar esta operacion (RN2).
     *
     * Valida que la solicitud, el tipo y el usuario sean validos antes de
     * delegar la operacion a la entidad Solicitud, que a su vez registra
     * el cambio en el historial (RF-06) y transiciona el estado (RF-04).
     *
     * @param solicitud     Solicitud a clasificar (no puede ser nula)
     * @param tipoSolicitud Tipo a asignar (no puede ser nulo)
     * @param usuario       Usuario que clasifica (debe tener rol COORDINADOR)
     * @param observacion   Justificacion de la clasificacion
     * @throws SolicitudException       Si la solicitud esta cerrada
     * @throws IllegalArgumentException Si el usuario no tiene el rol adecuado o
     *                                  algun parametro es nulo
     */
    public Solicitud clasificarSolicitud(Solicitud solicitud, TipoSolicitud tipoSolicitud,
            Usuario usuario, String observacion) throws SolicitudException {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (tipoSolicitud == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        // RF-13: verificar que el usuario tenga el rol de COORDINADOR (RN2)
        if (!usuario.puedeClasificarSolicitud()) {
            throw new IllegalArgumentException(
                    "Acceso denegado: el usuario no tiene el rol para clasificar solicitudes." +
                            " Rol actual: " + usuario.getRol());
        }

        // RF-02: delegar la clasificacion a la entidad (que valida RF-08 y registra
        // RF-06)
        solicitud.clasificarSolicitud(tipoSolicitud, usuario, observacion);

        return solicitud;
    }
}
