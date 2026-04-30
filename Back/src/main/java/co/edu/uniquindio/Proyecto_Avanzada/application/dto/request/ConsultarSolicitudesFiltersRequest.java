package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para filtrar solicitudes sin exponer value objects del dominio.
 */
public record ConsultarSolicitudesFiltersRequest(

        @Size(max = 20, message = "El estado debe tener maximo 20 caracteres")
        String estadoSolicitud,

        @Size(max = 50, message = "El tipo de solicitud debe tener maximo 50 caracteres")
        String tipoSolicitud,

        @Size(min = 5, max = 20, message = "La identificacion del responsable debe tener entre 5 y 20 caracteres")
        String identificacionResponsable,

        @Size(max = 20, message = "La prioridad debe tener maximo 20 caracteres")
        String prioridadSolicitud) {
}
