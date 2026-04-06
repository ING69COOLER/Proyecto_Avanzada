package co.edu.uniquindio.Proyecto_Avanzada.application.command;

/**
 * Comando de aplicacion para asignar un responsable a una solicitud.
 */
public record AsignarResponsableCommand(
        String identificacionCoordinador,
        String identificacionResponsable,
        String observacion) {
}
