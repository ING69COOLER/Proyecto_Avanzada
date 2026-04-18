package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.CanalOrigenEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.TipoSolicitudEnumDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar una nueva solicitud en el sistema.
 */
public record CrearSolicitudRequest(

        @Valid
        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitudEnumDTO tipoSolicitud,

        @NotBlank(message = "La descripcion es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripcion debe tener entre 10 y 1000 caracteres")
        String descripcion,

        @Valid
        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigenEnumDTO canalOrigen,

        @NotBlank(message = "La identificacion del solicitante es obligatoria")
        @Size(min = 5, max = 20, message = "La identificacion del solicitante debe tener entre 5 y 20 caracteres")
        String identificacionSolicitante) {
}
