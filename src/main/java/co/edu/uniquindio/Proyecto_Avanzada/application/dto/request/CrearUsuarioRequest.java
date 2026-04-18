package co.edu.uniquindio.Proyecto_Avanzada.application.dto.request;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.RolEnumDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar un usuario en el sistema.
 */
public record CrearUsuarioRequest(

        @NotBlank(message = "El nombre del usuario es obligatorio")
        @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
        String nombre,

        @NotBlank(message = "La identificacion del usuario es obligatoria")
        @Size(min = 5, max = 20, message = "La identificacion debe tener entre 5 y 20 caracteres")
        String identificacion,

        @NotBlank(message = "El correo del usuario es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        @Size(max = 200, message = "El correo no puede superar los 200 caracteres")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password,

        @NotNull(message = "El estado de actividad es obligatorio")
        Boolean activo,

        @Valid
        @NotNull(message = "El rol del usuario es obligatorio")
        RolEnumDTO rol) {
}
