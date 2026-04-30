package co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstadoSolicitudEnumDTO(

        @NotBlank(message = "El codigo del estado es obligatorio")
        @Size(max = 50, message = "El codigo del estado no puede superar los 50 caracteres")
        String codigo) {
}
