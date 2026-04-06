package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de salida con la informacion resumida de un usuario expuesta por la API.
 */
public record UsuarioResumenDTO(

        @NotBlank(message = "El nombre del usuario no puede estar vacio")
        @Size(max = 150, message = "El nombre del usuario no puede superar los 150 caracteres")
        String nombre,

        @NotBlank(message = "La identificacion del usuario no puede estar vacia")
        @Size(min = 5, max = 20, message = "La identificacion del usuario debe tener entre 5 y 20 caracteres")
        String identificacion,

        @NotBlank(message = "El correo del usuario no puede estar vacio")
        @Size(max = 200, message = "El correo del usuario no puede superar los 200 caracteres")
        String correo,

        @NotNull(message = "El estado de actividad del usuario es obligatorio")
        Boolean activo,

        @NotBlank(message = "El rol del usuario no puede estar vacio")
        @Size(max = 50, message = "El rol del usuario no puede superar los 50 caracteres")
        String rol) {
}
