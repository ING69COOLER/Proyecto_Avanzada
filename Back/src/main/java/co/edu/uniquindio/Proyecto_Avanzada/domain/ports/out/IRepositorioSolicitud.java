package co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * RF-07: Interfaz para operaciones CRUD y consultas de solicitudes
 * Facilita el seguimiento y la gestión operativa de solicitudes
 */
public interface IRepositorioSolicitud {

    /**
     * Guarda una nueva solicitud
     */
    public void guardarSolicitud(Solicitud solicitud);
    
    /**
     * RF-07: Consulta solicitudes por estado
     */
    public List<Solicitud> consultarEstado(EstadoSolicitud estadoSolicitud);
    
    /**
     * RF-07: Consulta solicitudes por tipo de solicitud
     */
    public List<Solicitud> consultarTipoSolicitud(TipoSolicitud tipoSolicitud);
    
    /**
     * RF-07: Consulta solicitudes por prioridad
     */
    public List<Solicitud> consultarPrioridad(Prioridad prioridad);

    public List<Solicitud> consultarPorNivelPrioridad(NivelPrioridad nivelPrioridad);
    
    /**
     * RF-07: Consulta solicitudes asignadas a un responsable
     */
    public List<Solicitud> consultarResponsable(Usuario usuario);

    //obtiene una solicitud por
    public Optional<Solicitud> obtenerPorId(Long id);

    public List<Solicitud> listar();

    public Page<Solicitud> listar(Pageable pageable);

    public Page<Solicitud> consultarEstado(EstadoSolicitud estadoSolicitud, Pageable pageable);

    public Page<Solicitud> consultarTipoSolicitud(TipoSolicitud tipoSolicitud, Pageable pageable);

    public Page<Solicitud> consultarPorNivelPrioridad(NivelPrioridad nivelPrioridad, Pageable pageable);

    public Page<Solicitud> consultarResponsable(Usuario usuario, Pageable pageable);
}
