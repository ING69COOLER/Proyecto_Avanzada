package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de salida con la vista resumida de una solicitud para listados y consultas breves.
 */
public record SolicitudResumenResponse(

        @NotNull(message = "El codigo de la solicitud es obligatorio")
        Long codigo,

        @Valid
        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitudDTO tipoSolicitud,

        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigen canalOrigen,

        @NotNull(message = "El estado de la solicitud es obligatorio")
        EstadoSolicitud estado,

        @NotNull(message = "La fecha de registro es obligatoria")
        LocalDateTime fechaHoraRegistro,

        @Valid
        UsuarioResumenDTO usuarioSolicitante,

        @Valid
        PrioridadDTO prioridad) {
}
