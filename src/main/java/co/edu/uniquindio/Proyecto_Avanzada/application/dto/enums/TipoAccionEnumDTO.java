package co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoAccionEnumDTO(

        @NotBlank(message = "El codigo del tipo de accion es obligatorio")
        @Size(max = 50, message = "El codigo del tipo de accion no puede superar los 50 caracteres")
        String codigo) {
}
