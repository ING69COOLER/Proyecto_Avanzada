package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import java.time.OffsetDateTime;
import java.util.Map;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.ErrorResponse;

/**
 * Mapper para construir respuestas de error consistentes para la API.
 */
public final class ErrorResponseMapper {

    private ErrorResponseMapper() {
    }

    public static ErrorResponse from(int status, String error, String message, String path) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status,
                error,
                message,
                path,
                null);
    }

    public static ErrorResponse from(int status, String error, String message, String path,
            Map<String, String> validationErrors) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status,
                error,
                message,
                path,
                validationErrors);
    }
}
