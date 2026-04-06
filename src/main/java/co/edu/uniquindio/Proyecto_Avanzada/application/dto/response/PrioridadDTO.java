package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida que representa la prioridad actual asignada a una solicitud.
 */
public record PrioridadDTO(

        @NotNull(message = "El nivel de prioridad es obligatorio")
        NivelPrioridad nivel,

        @NotBlank(message = "La descripcion de la prioridad es obligatoria")
        @Size(max = 500, message = "La descripcion de la prioridad no puede superar los 500 caracteres")
        String descripcion) {
}
