package co.edu.uniquindio.Proyecto_Avanzada.application.command;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;

/**
 * Comando de aplicacion para priorizar una solicitud.
 */
public record PriorizarSolicitudCommand(
        String identificacionUsuario,
        NivelPrioridad nivelPrioridad,
        String justificacion) {
}
