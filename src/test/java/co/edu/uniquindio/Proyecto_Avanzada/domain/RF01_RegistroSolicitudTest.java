package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-01 – Registro de solicitudes académicas
 *
 * Verifica que el sistema permita registrar una solicitud almacenando:
 * - Tipo de solicitud
 * - Descripción de la solicitud
 * - Canal de origen (CSU, correo, SAC, telefónico, etc.)
 * - Fecha y hora de registro
 * - Identificación del solicitante
 */
class RF01_RegistroSolicitudTest {

    private Usuario usuarioValido;
    private final LocalDateTime ahora = LocalDateTime.of(2026, 3, 2, 10, 0);

    @BeforeEach
    void setUp() {
        // Creamos un usuario con todos sus datos, incluyendo su identificación
        usuarioValido = new Usuario(
                null, // id (sin persistencia en test unitario)
                "María García",
                "CC-1234567890", // identificacion del solicitante
                "maria.garcia@uniquindio.edu.co",
                true,
                Rol.ESTUDIANTE,
                null // solicitudes — se inicializa dentro del constructor
        );
    }
   
    // ------------------------------------------------------------------ //
    // CASO 2 – Validaciones: campos obligatorios nulos o vacíos //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si el tipo de solicitud es nulo")
    void registrarSolicitud_tipoNulo_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioValido.registrarSolicitud(
                null,
                "Descripción válida",
                CanalOrigen.CSU,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si la descripción es nula")
    void registrarSolicitud_descripcionNula_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.HOMOLOGACION,
                null,
                CanalOrigen.SAC,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si la descripción está vacía")
    void registrarSolicitud_descripcionVacia_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.CANCELACION_ASIGNATURA,
                "   ", // blank
                CanalOrigen.TELEFONO,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si el canal de origen es nulo")
    void registrarSolicitud_canalNulo_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.SOLICITUD_CUPOS,
                "Solicito cupo",
                null,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si la fecha/hora de registro es nula")
    void registrarSolicitud_fechaNula_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.REGISTRO_ASIGNATURA,
                "Descripción válida",
                CanalOrigen.EMAIL,
                null,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar excepción si el usuario no tiene identificación")
    void registrarSolicitud_usuarioSinIdentificacion_debeLanzarExcepcion() {
        Usuario usuarioSinId = new Usuario(
                null, "Sin Nombre", null, "x@x.com", true, Rol.ESTUDIANTE, null);

        assertThrows(IllegalArgumentException.class, () -> usuarioSinId.registrarSolicitud(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Consulta vacía",
                CanalOrigen.PORTAL_WEB,
                ahora,
                null));
    }

 
    
}
