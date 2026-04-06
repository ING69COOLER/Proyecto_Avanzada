package co.edu.uniquindio.Proyecto_Avanzada.application.command;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * Comando de aplicacion para clasificar una solicitud.
 */
public record ClasificarSolicitudCommand(
        TipoSolicitud tipoSolicitud,
        String identificacionUsuario,
        String observacion) {
}
