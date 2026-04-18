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

    // Resuelve errores de validacion de @Valid en cuerpos JSON y responde 400 con detalle por campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse body = mapper.toErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "La solicitud contiene datos invalidos." + ex.getMessage(),
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

    // Resuelve SolicitudException: por defecto responde 400, o 404 si el mensaje indica recurso inexistente.
    @ExceptionHandler(SolicitudException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudErrors(
            SolicitudException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = mapper.toErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            "excepcion de solicitud: "+ ex.getMessage(),
            request.getRequestURI()
        , null);

        return ResponseEntity.status(status).body(body);
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
