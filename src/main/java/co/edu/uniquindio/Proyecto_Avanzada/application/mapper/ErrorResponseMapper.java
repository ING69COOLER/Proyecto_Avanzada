package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.ErrorResponse;

/**
 * Mapper para construir respuestas de error consistentes para la API usando MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ErrorResponseMapper {

    @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "error", source = "error")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "path", source = "path")
    @Mapping(target = "validationErrors", source = "validationErrors")
    ErrorResponse toErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors);

    default ErrorResponse toErrorResponse(int status, String error, String message, String path) {
        return toErrorResponse(status, error, message, path, null);
    }
}
