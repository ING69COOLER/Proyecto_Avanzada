package co.edu.uniquindio.Proyecto_Avanzada.application.command;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * Comando de aplicacion para registrar una nueva solicitud.
 */
public record CrearSolicitudCommand(
        TipoSolicitud tipoSolicitud,
        String descripcion,
        CanalOrigen canalOrigen,
        String identificacionSolicitante) {
}
