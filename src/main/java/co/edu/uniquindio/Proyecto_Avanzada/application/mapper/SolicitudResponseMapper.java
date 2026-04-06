package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import java.util.Collections;
import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.PrioridadDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudResumenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TipoSolicitudDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.UsuarioResumenDTO;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.HistorialSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * Mapper para transformar entidades del dominio de solicitudes en DTOs de respuesta.
 */
public final class SolicitudResponseMapper {

    private SolicitudResponseMapper() {
    }

    public static SolicitudDetalleResponse toDetalleResponse(Solicitud solicitud) {
        return new SolicitudDetalleResponse(
                solicitud.getCodigo(),
                toTipoSolicitudDTO(solicitud.getTipo()),
                solicitud.getDescripcion(),
                solicitud.getCanalOrigen(),
                solicitud.getFechaHoraRegistro(),
                solicitud.getFechaCierre(),
                solicitud.getEstado(),
                toUsuarioResumenDTO(solicitud.getUsuarioSolicitante()),
                toPrioridadDTO(solicitud.getPrioridad()),
                toHistorialResponseList(solicitud.getHistorial()));
    }

    public static SolicitudResumenResponse toResumenResponse(Solicitud solicitud) {
        return new SolicitudResumenResponse(
                solicitud.getCodigo(),
                toTipoSolicitudDTO(solicitud.getTipo()),
                solicitud.getCanalOrigen(),
                solicitud.getEstado(),
                solicitud.getFechaHoraRegistro(),
                toUsuarioResumenDTO(solicitud.getUsuarioSolicitante()),
                toPrioridadDTO(solicitud.getPrioridad()));
    }

    public static List<SolicitudResumenResponse> toResumenResponseList(List<Solicitud> solicitudes) {
        if (solicitudes == null) {
            return Collections.emptyList();
        }
        return solicitudes.stream()
                .map(SolicitudResponseMapper::toResumenResponse)
                .toList();
    }

    public static List<EventoHistorialResponse> toHistorialResponseList(List<HistorialSolicitud> historial) {
        if (historial == null) {
            return Collections.emptyList();
        }
        return historial.stream()
                .map(SolicitudResponseMapper::toEventoHistorialResponse)
                .toList();
    }

    public static EventoHistorialResponse toEventoHistorialResponse(HistorialSolicitud evento) {
        return new EventoHistorialResponse(
                evento.getFechaHora(),
                evento.getEstado(),
                evento.getAccion(),
                evento.getObservacion(),
                toUsuarioResumenDTO(evento.getResponsable()));
    }

    public static UsuarioResumenDTO toUsuarioResumenDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResumenDTO(
                usuario.getNombre(),
                usuario.getIdentificacion(),
                usuario.getCorreo(),
                usuario.getActivo(),
                usuario.getRol() != null ? usuario.getRol().name() : null);
    }

    public static TipoSolicitudDTO toTipoSolicitudDTO(TipoSolicitud tipoSolicitud) {
        if (tipoSolicitud == null) {
            return null;
        }
        return new TipoSolicitudDTO(tipoSolicitud, humanizeEnum(tipoSolicitud.name()));
    }

    public static PrioridadDTO toPrioridadDTO(Prioridad prioridad) {
        if (prioridad == null) {
            return null;
        }
        return new PrioridadDTO(prioridad.nivel(), prioridad.descripcion());
    }

    private static String humanizeEnum(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.toLowerCase()
                .replace('_', ' ')
                .transform(SolicitudResponseMapper::capitalizeWords);
    }

    private static String capitalizeWords(String value) {
        String[] words = value.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return builder.toString();
    }
}
