package co.edu.uniquindio.Proyecto_Avanzada.application.command;

/**
 * Comando de aplicacion para cerrar una solicitud.
 */
public record CerrarSolicitudCommand(
        String identificacionUsuario,
        String observacionCierre) {
}
