package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.AsignarResponsableCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.CambiarEstadoCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.CerrarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.ClasificarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.CrearSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.PriorizarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.PriorizarSolicitudRequest;

/**
 * Mapper que transforma DTOs de entrada HTTP en comandos de aplicacion.
 */
public final class RequestCommandMapper {

    private RequestCommandMapper() {
    }

    public static CrearSolicitudCommand toCommand(CrearSolicitudRequest request) {
        return new CrearSolicitudCommand(
                request.tipoSolicitud(),
                request.descripcion(),
                request.canalOrigen(),
                request.identificacionSolicitante());
    }

    public static ClasificarSolicitudCommand toCommand(ClasificarSolicitudRequest request) {
        return new ClasificarSolicitudCommand(
                request.tipoSolicitud(),
                request.identificacionUsuario(),
                request.observacion());
    }

    public static AsignarResponsableCommand toCommand(AsignarResponsableRequest request) {
        return new AsignarResponsableCommand(
                request.identificacionCoordinador(),
                request.identificacionResponsable(),
                request.observacion());
    }

    public static CambiarEstadoCommand toCommand(CambiarEstadoRequest request) {
        return new CambiarEstadoCommand(
                request.nuevoEstado(),
                request.identificacionUsuario(),
                request.observacion());
    }

    public static CerrarSolicitudCommand toCommand(CerrarSolicitudRequest request) {
        return new CerrarSolicitudCommand(
                request.identificacionUsuario(),
                request.observacionCierre());
    }

    public static PriorizarSolicitudCommand toCommand(PriorizarSolicitudRequest request) {
        return new PriorizarSolicitudCommand(
                request.identificacionUsuario(),
                request.nivelPrioridad(),
                request.justificacion());
    }
}
