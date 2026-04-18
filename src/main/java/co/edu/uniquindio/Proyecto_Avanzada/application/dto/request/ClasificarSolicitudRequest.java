package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.TipoSolicitudEnumDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para clasificar una solicitud con el tipo definido por el dominio.
 */
public record ClasificarSolicitudRequest(

        @Valid
        @NotNull(message = "El tipo de solicitud clasificado es obligatorio")
        TipoSolicitudEnumDTO tipoSolicitud,

        @NotBlank(message = "La identificacion del usuario que clasifica es obligatoria")
        @Size(min = 5, max = 20, message = "La identificacion del usuario debe tener entre 5 y 20 caracteres")
        String identificacionUsuario,

        @NotBlank(message = "La observacion de clasificacion es obligatoria")
        @Size(min = 5, max = 500, message = "La observacion debe tener entre 5 y 500 caracteres")
        String observacion) {
}
