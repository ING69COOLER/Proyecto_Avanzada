package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import java.util.List;

/**
 * RF-07: Servicio de Dominio para consultas de solicitudes académicas
 * Facilita el seguimiento y la gestión operativa de solicitudes
 * Permite consultar por: estado, tipo, prioridad y responsable asignado
 */
//caso de uso
@Service
public class ConsultaSolicitudesService {

    @Autowired
    private IRepositorioSolicitud repositorioSolicitud;

    /**
     * RF-07: Consulta solicitudes por estado
     * @param estado Estado de la solicitud a buscar
     * @return Lista de solicitudes con el estado especificado
     */
    public List<Solicitud> consultarPorEstado(EstadoSolicitud estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        return repositorioSolicitud.consultarEstado(estado);
    }

    /**
     * RF-07: Consulta solicitudes por tipo de solicitud
     * @param tipo Tipo de solicitud a buscar (REGISTRO_ASIGNATURA, HOMOLOGACION, etc.)
     * @return Lista de solicitudes del tipo especificado
     */
    public List<Solicitud> consultarPorTipo(TipoSolicitud tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        return repositorioSolicitud.consultarTipoSolicitud(tipo);
    }

    /**
     * RF-07: Consulta solicitudes por prioridad
     * @param prioridad Prioridad de la solicitud a buscar
     * @return Lista de solicitudes con la prioridad especificada
     */
    public List<Solicitud> consultarPorPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
        return repositorioSolicitud.consultarPrioridad(prioridad);
    }

    /**
     * RF-07: Consulta solicitudes asignadas a un responsable
     * @param responsable Usuario responsable de la solicitud
     * @return Lista de solicitudes asignadas al responsable especificado
     */

    //ambiguedad resuelta
    public List<Solicitud> consultarPorResponsable(Usuario responsable) {
        if (responsable == null) {
            throw new IllegalArgumentException("El usuario responsable no puede ser nulo");
        }
        if (!responsable.puedeAsignar() && !responsable.puedeAtenderSolicitud()) {
            throw new IllegalArgumentException("El usuario no tiene permisos para consultar solicitudes");
        }
        return repositorioSolicitud.consultarResponsable(responsable);
    }

    /**
     * RF-07: Consulta avanzada con múltiples criterios
     * Busca solicitudes que cumplan con el estado especificado
     * @param estado Estado a filtrar
     * @return Lista de solicitudes filtradas
     */
    public List<Solicitud> consultarSolicitudesPendientes() {
        return repositorioSolicitud.consultarEstado(EstadoSolicitud.REGISTRADA);
    }

    /**
     * RF-07: Obtiene solicitudes que requieren atención inmediata
     * @return Lista de solicitudes en estado EN_ATENCION
     */
    public List<Solicitud> consultarSolicitudesEnAtencion() {
        return repositorioSolicitud.consultarEstado(EstadoSolicitud.EN_ATENCION);
    }

    /**
     * RF-07: Obtiene solicitudes cerradas
     * @return Lista de solicitudes cerradas
     */
    public List<Solicitud> consultarSolicitudesCerradas() {
        return repositorioSolicitud.consultarEstado(EstadoSolicitud.CERRADA);
    }

}
