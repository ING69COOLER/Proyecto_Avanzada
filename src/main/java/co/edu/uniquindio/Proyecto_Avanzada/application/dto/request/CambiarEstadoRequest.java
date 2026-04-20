package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.EstadoSolicitudEnumDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para solicitar una transicion de estado sobre una solicitud.
 */
public record CambiarEstadoRequest(

        @Valid
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoSolicitudEnumDTO nuevoEstado,

        String identificacionUsuario,

        @NotBlank(message = "La observacion del cambio de estado es obligatoria")
        @Size(min = 5, max = 500, message = "La observacion debe tener entre 5 y 500 caracteres")
        String observacion) {
}
