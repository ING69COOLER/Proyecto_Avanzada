package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida con el detalle completo de una solicitud y su trazabilidad.
 */
public record SolicitudDetalleResponse(

        @NotNull(message = "El codigo de la solicitud es obligatorio")
        Long codigo,

        @Valid
        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitudDTO tipoSolicitud,

        @NotBlank(message = "La descripcion de la solicitud es obligatoria")
        @Size(max = 1000, message = "La descripcion de la solicitud no puede superar los 1000 caracteres")
        String descripcion,

        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigen canalOrigen,

        @NotNull(message = "La fecha de registro es obligatoria")
        LocalDateTime fechaHoraRegistro,

        LocalDateTime fechaCierre,

        @NotNull(message = "El estado de la solicitud es obligatorio")
        EstadoSolicitud estado,

        @Valid
        @NotNull(message = "El usuario solicitante es obligatorio")
        UsuarioResumenDTO usuarioSolicitante,

        @Valid
        PrioridadDTO prioridad,

        @Valid
        @NotNull(message = "El historial de la solicitud es obligatorio")
        List<EventoHistorialResponse> historial) {
}
