package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida que representa un evento auditable dentro del historial de una solicitud.
 */
public record EventoHistorialResponse(

        @NotNull(message = "La fecha y hora del evento es obligatoria")
        LocalDateTime fechaHora,

        @NotNull(message = "El estado asociado al evento es obligatorio")
        EstadoSolicitud estado,

        @NotNull(message = "La accion asociada al evento es obligatoria")
        TipoAccion accion,

        @NotBlank(message = "La observacion del evento es obligatoria")
        @Size(max = 500, message = "La observacion del evento no puede superar los 500 caracteres")
        String observacion,

        @Valid
        @NotNull(message = "El responsable del evento es obligatorio")
        UsuarioResumenDTO responsable) {
}
