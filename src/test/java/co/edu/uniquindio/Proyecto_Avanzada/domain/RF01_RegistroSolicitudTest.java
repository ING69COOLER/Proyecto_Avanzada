package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.UserException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
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
        usuarioValido = new Usuario(
                null,
                "María García",
                "CC-1234567890",
                "maria.garcia@uniquindio.edu.co",
                true,
                Rol.ESTUDIANTE,
                null);
    }

    // ------------------------------------------------------------------ //
    // CASO 1 – Registro exitoso //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("RF-01 | Debe registrar la solicitud correctamente y guardarla en el repositorio")
    void registrarSolicitud_datosValidos_debeGuardarEnRepositorio() throws UserException {
        int cantidadAntes = RepositorioSolicitud.getInstancia().listar().size();

        usuarioValido.registrarSolicitud(
                TipoSolicitud.HOMOLOGACION,
                "Solicitud de homologación de materia",
                CanalOrigen.CSU,
                ahora,
                null);

        int cantidadDespues = RepositorioSolicitud.getInstancia().listar().size();
        assertEquals(cantidadAntes + 1, cantidadDespues,
                "Debe haber una solicitud más en el repositorio después del registro");
    }

    // ------------------------------------------------------------------ //
    // CASO 2 – Validaciones: campos obligatorios nulos o vacíos //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si el tipo de solicitud es nulo")
    void registrarSolicitud_tipoNulo_debeLanzarExcepcion() {
        assertThrows(UserException.class, () -> usuarioValido.registrarSolicitud(
                null,
                "Descripción válida",
                CanalOrigen.CSU,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si la descripción es nula")
    void registrarSolicitud_descripcionNula_debeLanzarExcepcion() {
        assertThrows(UserException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.HOMOLOGACION,
                null,
                CanalOrigen.SAC,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si la descripción está vacía")
    void registrarSolicitud_descripcionVacia_debeLanzarExcepcion() {
        assertThrows(UserException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.CANCELACION_ASIGNATURA,
                "   ",
                CanalOrigen.TELEFONO,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si el canal de origen es nulo")
    void registrarSolicitud_canalNulo_debeLanzarExcepcion() {
        assertThrows(UserException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.SOLICITUD_CUPOS,
                "Solicito cupo",
                null,
                ahora,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si la fecha/hora de registro es nula")
    void registrarSolicitud_fechaNula_debeLanzarExcepcion() {
        assertThrows(UserException.class, () -> usuarioValido.registrarSolicitud(
                TipoSolicitud.REGISTRO_ASIGNATURA,
                "Descripción válida",
                CanalOrigen.EMAIL,
                null,
                null));
    }

    @Test
    @DisplayName("RF-01 | Debe lanzar UserException si el usuario no tiene identificación")
    void registrarSolicitud_usuarioSinIdentificacion_debeLanzarExcepcion() {
        Usuario usuarioSinId = new Usuario(
                null, "Sin Nombre", null, "x@x.com", true, Rol.ESTUDIANTE, null);

        assertThrows(UserException.class, () -> usuarioSinId.registrarSolicitud(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Consulta vacía",
                CanalOrigen.PORTAL_WEB,
                ahora,
                null));
    }
}
