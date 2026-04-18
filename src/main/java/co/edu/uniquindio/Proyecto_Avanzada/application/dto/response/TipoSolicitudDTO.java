package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.TipoSolicitudEnumDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida que representa el tipo de solicitud expuesto por la API.
 */
public record TipoSolicitudDTO(

        @Valid
        @NotNull(message = "El codigo del tipo de solicitud es obligatorio")
        TipoSolicitudEnumDTO codigo,

        @NotBlank(message = "El nombre del tipo de solicitud es obligatorio")
        @Size(max = 100, message = "El nombre del tipo de solicitud no puede superar los 100 caracteres")
        String nombre) {
}
