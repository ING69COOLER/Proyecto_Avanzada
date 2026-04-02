package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;

/**
 * RF-03: Servicio de Dominio para la priorizacion de solicitudes academicas.
 *
 * Asigna una prioridad a cada solicitud basandose en reglas del negocio:
 * tipo de solicitud, impacto academico y fecha limite asociada.
 * La prioridad queda registrada junto con una justificacion obligatoria.
 *
 * RF-13: Solo el COORDINADOR puede priorizar solicitudes (RN3).
 */
@Service
public class PriorizacionService {

    /**
     * RF-03: Asigna un nivel de prioridad a la solicitud con su respectiva
     * justificacion.
     * RF-13: Verifica que el usuario tenga el rol de COORDINADOR antes de priorizar
     * (RN3).
     *
     * Valida todos los parametros obligatorios y delega la operacion a la entidad
     * Solicitud, que aplica las reglas de negocio (RF-04/08/13).
     *
     * @param usuario      Usuario que prioriza (debe ser COORDINADOR activo)
     * @param justifiacion Razon por la que se asigna la prioridad (ej: impacto
     *                     academico)
     * @param solicitud    Solicitud a priorizar (no puede ser nula)
     * @param prioridad    Nivel de prioridad a asignar (ALTA, MEDIA, BAJA)
     * @throws SolicitudException       Si la solicitud esta cerrada o el rol es
     *                                  invalido
     * @throws IllegalArgumentException Si alguno de los parametros requeridos es
     *                                  nulo
     */

    //caso de uso
    public Solicitud priorizarSolicitud(Usuario usuario, String justifiacion,
            Solicitud solicitud, NivelPrioridad prioridad)
            throws SolicitudException {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (justifiacion == null) {
            throw new IllegalArgumentException("La justificacion no puede ser nula");
        }
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
        // RF-13: verificar que el usuario tenga el rol de COORDINADOR (RN3)
        if (!usuario.puedePriorizar()) {
            throw new IllegalArgumentException(
                    "Acceso denegado: el usuario no puede priorizar solicitudes." +
                            " Rol actual: " + usuario.getRol());
        }

        // RF-03: delegar a la entidad la asignacion de prioridad (valida RF-08)
        solicitud.priorizarSolicitud(prioridad, justifiacion);
        return solicitud;
    }
}
