package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.NivelPrioridadEnumDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para asignar prioridad a una solicitud.
 */
public record PriorizarSolicitudRequest(

        String identificacionUsuario,

        @Valid
        @NotNull(message = "El nivel de prioridad es obligatorio")
        NivelPrioridadEnumDTO nivelPrioridad,

        @NotBlank(message = "La justificacion de prioridad es obligatoria")
        @Size(min = 5, max = 500, message = "La justificacion debe tener entre 5 y 500 caracteres")
        String justificacion) {
}
