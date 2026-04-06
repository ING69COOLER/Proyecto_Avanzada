package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para solicitar una transicion de estado sobre una solicitud.
 */
public record CambiarEstadoRequest(

        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoSolicitud nuevoEstado,

        @NotBlank(message = "La identificacion del usuario responsable es obligatoria")
        @Size(min = 5, max = 20, message = "La identificacion del usuario debe tener entre 5 y 20 caracteres")
        String identificacionUsuario,

        @NotBlank(message = "La observacion del cambio de estado es obligatoria")
        @Size(min = 5, max = 500, message = "La observacion debe tener entre 5 y 500 caracteres")
        String observacion) {
}
