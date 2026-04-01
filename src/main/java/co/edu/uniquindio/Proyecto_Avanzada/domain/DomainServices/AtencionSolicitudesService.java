package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.stereotype.Service;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

/**
 * RF-04 / RF-05: Servicio de Dominio para la atencion de solicitudes
 * academicas.
 *
 * Gestiona dos operaciones dentro del ciclo de vida de la solicitud:
 * - Asignacion de responsable (transfiere la solicitud a EN_ATENCION)
 * - Atencion de solicitud (marca la solicitud como ATENDIDA)
 *
 * RF-13: Verifica el rol del usuario antes de cada operacion.
 * RF-06: Las acciones quedan registradas en el historial a traves de la entidad
 * Solicitud.
 */
@Service
public class AtencionSolicitudesService {

    /**
     * RF-05: Asigna un responsable a la solicitud y la pasa a estado EN_ATENCION.
     * RF-04: Gestiona la transicion de estado dentro del ciclo de vida.
     * RF-13: Solo el COORDINADOR puede asignar responsables.
     * RF-06: La asignacion queda registrada en el historial de la solicitud.
     *
     * @param user        Usuario que realiza la asignacion (debe ser COORDINADOR
     *                    activo)
     * @param solicitud   Solicitud a asignar (no puede ser nula)
     * @param descripcion Observacion de la asignacion
     * @throws SolicitudException       Si la solicitud esta cerrada o el rol es
     *                                  invalido
     * @throws IllegalArgumentException Si alguno de los parametros es nulo
     */
    public void asignarResponsable(Usuario user, Solicitud solicitud,
            String descripcion) throws SolicitudException {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        // RF-13: verificar que el usuario tenga rol de COORDINADOR
        if (!user.puedeAsignar()) {
            throw new IllegalArgumentException(
                    "Acceso denegado: el usuario no tiene el rol para asignar responsables." +
                            " Rol actual: " + user.getRol());
        }
        if (user == null || !user.puedeAsignar()) {
            throw new SolicitudException(
                    "Acceso denegado: solo el COORDINADOR puede asignar responsables." +
                            (user != null ? " Rol actual: " + user.getRol() : ""));
        }

        // RF-05: delegar a la entidad, que valida RF-08/13 y registra RF-06
        solicitud.asignarResponsable(user, descripcion);
    }

    /**
     * RF-04: Marca la solicitud como ATENDIDA, avanzando su ciclo de vida.
     * RF-13: Solo el DOCENTE puede atender solicitudes.
     * RF-05: Verifica que el usuario haya sido asignado previamente a la solicitud.
     * RF-06: La atencion queda registrada en el historial de la solicitud.
     *
     * Esta operacion es transitoria: prepara la solicitud para que el COORDINADOR
     * pueda cerrarla formalmente (RF-08).
     *
     * @param user        Usuario que atiende (debe ser DOCENTE asignado)
     * @param solicitud   Solicitud a atender (no puede ser nula)
     * @param observacion Descripcion de lo realizado (no puede ser vacia)
     * @throws SolicitudException       Si la solicitud esta cerrada o el rol es
     *                                  invalido
     * @throws IllegalArgumentException Si alguno de los parametros es nulo/vacio o
     *                                  el usuario no esta asignado
     */
    public void atenderSolicitud(Usuario user, Solicitud solicitud,
            String observacion) throws SolicitudException {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La observacion no puede ser vacia");
        }
        // RF-05: el usuario debe haber sido asignado previamente a esta solicitud
        
        if (!solicitud.obtenerUsuariosDeHistorias().stream().filter((usuario) -> usuario.getIdentificacion()
                                                    .equals(user.getIdentificacion())).toList().isEmpty()) 
                                                    {
            throw new IllegalArgumentException("El usuario no es el responsable asignado a esta solicitud");    
        }
        // RF-13: verificar que el usuario tenga rol de DOCENTE
        if (!user.puedeAtenderSolicitud()) {
            throw new IllegalArgumentException(
                    "Acceso denegado: el usuario no tiene el rol para atender solicitudes." +
                            " Rol actual: " + user.getRol());
        }

        // RF-04: delegar a la entidad, que registra RF-06 y transiciona el estado
        solicitud.atenderSolicitud(user, observacion);
    }
}
