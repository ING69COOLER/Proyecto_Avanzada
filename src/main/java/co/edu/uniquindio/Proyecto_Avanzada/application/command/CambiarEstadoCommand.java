package co.edu.uniquindio.Proyecto_Avanzada.application.command;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;

/**
 * Comando de aplicacion para solicitar un cambio de estado.
 */
public record CambiarEstadoCommand(
        EstadoSolicitud nuevoEstado,
        String identificacionUsuario,
        String observacion) {
}
