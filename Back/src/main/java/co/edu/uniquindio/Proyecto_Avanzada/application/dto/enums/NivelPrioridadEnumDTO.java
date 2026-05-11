package co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record NivelPrioridadEnumDTO(

        @NotBlank(message = "El codigo del nivel de prioridad es obligatorio")
        @Size(max = 50, message = "El codigo del nivel de prioridad no puede superar los 50 caracteres")
        String codigo) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static NivelPrioridadEnumDTO from(Object value) {
        return new NivelPrioridadEnumDTO(extractCodigo(value));
    }

    private static String extractCodigo(Object value) {
        if (value instanceof String codigo) {
            return codigo;
        }
        if (value instanceof Map<?, ?> map) {
            Object codigo = map.get("codigo");
            return codigo != null ? codigo.toString() : null;
        }
        return null;
    }
}
