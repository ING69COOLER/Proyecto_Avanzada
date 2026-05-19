package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.ErrorResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.ErrorResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseMapper mapper;

    // Resuelve errores de seguridad (403 Forbidden)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "Acceso denegado: No tiene los permisos necesarios para esta operacion.",
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Resuelve errores de validacion de @Valid en cuerpos JSON y responde 400 con detalle por campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        String message = validationErrors.isEmpty()
                ? "La solicitud contiene datos invalidos."
                : validationErrors.values().iterator().next();

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors);

        return ResponseEntity.badRequest().body(body);
    }

    // Resuelve violaciones de restricciones (query/path params) y responde 400.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
            null);

        return ResponseEntity.badRequest().body(body);
    }

    // Resuelve IllegalArgumentException: por defecto responde 400, o 404 si el mensaje indica recurso inexistente.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentErrors(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = mapper.toErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            "operacion invalida: " + ex.getMessage(),
            request.getRequestURI()
        ,null);

        return ResponseEntity.status(status).body(body);
    }

    // Resuelve SolicitudException: Si el mensaje contiene "Acceso denegado" responde 403, de lo contrario 400.
    @ExceptionHandler(SolicitudException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudErrors(
            SolicitudException ex,
            HttpServletRequest request) {

        boolean isForbidden = ex.getMessage().toLowerCase().contains("acceso denegado");
        HttpStatus status = isForbidden ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
        
        ErrorResponse body = mapper.toErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            null);

        return ResponseEntity.status(status).body(body);
    }

    // Resuelve Gateway timeout o errores de integracion externa (Bad Gateway)
    @ExceptionHandler(org.springframework.web.client.HttpServerErrorException.BadGateway.class)
    public ResponseEntity<ErrorResponse> handleBadGateway(
            org.springframework.web.client.HttpServerErrorException.BadGateway ex,
            HttpServletRequest request) {

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                "Error en el servicio externo (502): " + ex.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    // Resuelve Service Unavailable (503)
    @ExceptionHandler(org.springframework.web.client.HttpServerErrorException.ServiceUnavailable.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(
            org.springframework.web.client.HttpServerErrorException.ServiceUnavailable ex,
            HttpServletRequest request) {

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "Servicio no disponible temporalmente (503): " + ex.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(org.springframework.dao.InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseResourceErrors(
            org.springframework.dao.InvalidDataAccessResourceUsageException ex,
            HttpServletRequest request) {

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Error de base de datos: verifique que las tablas requeridas existan y que el backend este conectado a la base correcta.",
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // Fallback global para excepciones no controladas; responde 500.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrors(
            Exception ex,
            HttpServletRequest request) {

        // TODO: remove debug message before production
        String debugMsg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                debugMsg,
                request.getRequestURI(),
            null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
