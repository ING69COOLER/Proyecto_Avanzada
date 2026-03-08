package co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import java.util.List;

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
    
    /**
     * RF-07: Consulta solicitudes asignadas a un responsable
     */
    public List<Solicitud> consultarResponsable(Usuario usuario);
   
}
