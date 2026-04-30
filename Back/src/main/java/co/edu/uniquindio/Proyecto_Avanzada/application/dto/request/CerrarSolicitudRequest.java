package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para cerrar formalmente una solicitud.
 */
public record CerrarSolicitudRequest(

        String identificacionUsuario,

        @NotBlank(message = "La observacion de cierre es obligatoria")
        @Size(min = 5, max = 500, message = "La observacion de cierre debe tener entre 5 y 500 caracteres")
        String observacionCierre) {
}
