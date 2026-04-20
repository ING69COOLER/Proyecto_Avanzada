package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para asignar un responsable a una solicitud.
 */
public record AsignarResponsableRequest(

        String identificacionCoordinador,

        @NotBlank(message = "La identificacion del responsable asignado es obligatoria")
        @Size(min = 5, max = 20, message = "La identificacion del responsable debe tener entre 5 y 20 caracteres")
        String identificacionResponsable,

        @NotBlank(message = "La observacion de asignacion es obligatoria")
        @Size(min = 5, max = 500, message = "La observacion debe tener entre 5 y 500 caracteres")
        String observacion) {
}
