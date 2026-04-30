package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida estandar para reportar errores funcionales y de validacion en la API.
 */
public record ErrorResponse(

        @NotNull(message = "La marca de tiempo del error es obligatoria")
        OffsetDateTime timestamp,

        @NotNull(message = "El codigo de estado HTTP es obligatorio")
        Integer status,

        @NotBlank(message = "El codigo del error es obligatorio")
        @Size(max = 100, message = "El codigo del error no puede superar los 100 caracteres")
        String error,

        @NotBlank(message = "El mensaje del error es obligatorio")
        @Size(max = 500, message = "El mensaje del error no puede superar los 500 caracteres")
        String message,

        @NotBlank(message = "La ruta de la solicitud es obligatoria")
        @Size(max = 300, message = "La ruta de la solicitud no puede superar los 300 caracteres")
        String path,

        Map<String, String> validationErrors) {
}
