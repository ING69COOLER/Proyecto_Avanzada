package co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

public interface IRepositorioSolicitud {

//repositorio
/*
    El sistema debe permitir consultar solicitudes según diferentes criterios, tales como:
    Estado
    Tipo de solicitud
    Prioridad
    Responsable asignado
📌  Justificación: Facilitar el seguimiento y la gestión operativa.
    */

    public void guardarSolicitud(Solicitud solicitud);
    public void consultarEstado(EstadoSolicitud estadoSolicitud);
    public void consultarTipoSolicitud(TipoSolicitud tipoSolicitud);
    public void consultarPrioridad(Prioridad prioridad);
    public void consultarResponsable(Usuario usuario);
   
}
