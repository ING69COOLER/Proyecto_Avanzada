package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-13 – Autorizacion basica de operaciones
 *
 * Verifica que el sistema restrinja correctamente cada operacion
 * segun el rol del usuario:
 * - ESTUDIANTE: puede registrar solicitudes
 * - COORDINADOR: puede clasificar, priorizar, asignar y cerrar
 * - DOCENTE: puede atender solicitudes
 *
 * Casos probados:
 * - Acceso permitido (rol correcto)
 * - Acceso denegado (rol incorrecto, lanza SolicitudException /
 * IllegalArgumentException)
 * - Proteccion de solicitud cerrada (no modificable)
 */
@DisplayName("RF-13: Autorizacion basica de operaciones")
class RF13_AutorizacionOperacionesTest {

    private Usuario estudiante;
    private Usuario docente;
    private Usuario coordinador;
    private Solicitud solicitud;

    @BeforeEach
    void setup() {
        estudiante = crearUsuario(1L, "Juan Perez", "1001", Rol.ESTUDIANTE);
        docente = crearUsuario(2L, "Prof. Garcia", "1002", Rol.DOCENTE);
        coordinador = crearUsuario(3L, "Coord. Luis", "1003", Rol.COORDINADOR);

        solicitud = new Solicitud(
                TipoSolicitud.REGISTRO_ASIGNATURA,
                "Inscripcion en Programacion Avanzada",
                CanalOrigen.PORTAL_WEB,
                LocalDateTime.now(),
                "1001",
                null, null, estudiante, null);
    }

    // =========================================================================
    // RF-13: Clasificacion (solo COORDINADOR)
    // =========================================================================
    @Nested
    @DisplayName("Clasificacion de solicitudes")
    class ClasificacionTest {

        @Test
        @DisplayName("COORDINADOR puede clasificar la solicitud")
        void coordinadorPuedeClasificar() {
            assertDoesNotThrow(() -> solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, coordinador, "OK"),
                    "COORDINADOR debe poder clasificar");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede clasificar - debe lanzar excepcion")
        void estudianteNoPuedeClasificar() {
            assertThrows(Exception.class,
                    () -> solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, estudiante, "Intento"),
                    "ESTUDIANTE no debe poder clasificar");
        }

        @Test
        @DisplayName("DOCENTE no puede clasificar - debe lanzar excepcion")
        void docenteNoPuedeClasificar() {
            assertThrows(Exception.class,
                    () -> solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, docente, "Intento"),
                    "DOCENTE no debe poder clasificar");
        }
    }

    // =========================================================================
    // RF-13: Priorizacion (solo COORDINADOR)
    // =========================================================================
    @Nested
    @DisplayName("Priorizacion de solicitudes")
    class PriorizacionTest {

        @Test
        @DisplayName("COORDINADOR puede priorizar la solicitud")
        void coordinadorPuedePriorizar() {
            assertDoesNotThrow(() -> solicitud.priorizarSolicitud(NivelPrioridad.ALTA, "Urgente", coordinador),
                    "COORDINADOR debe poder priorizar");

            assertNotNull(solicitud.getPrioridad());
            assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().getNivel());
        }

        @Test
        @DisplayName("ESTUDIANTE no puede priorizar - debe lanzar SolicitudException")
        void estudianteNoPuedePriorizar() {
            assertThrows(SolicitudException.class,
                    () -> solicitud.priorizarSolicitud(NivelPrioridad.ALTA, "Urgente", estudiante),
                    "ESTUDIANTE no debe poder priorizar");
        }

        @Test
        @DisplayName("DOCENTE no puede priorizar - debe lanzar SolicitudException")
        void docenteNoPuedePriorizar() {
            assertThrows(SolicitudException.class,
                    () -> solicitud.priorizarSolicitud(NivelPrioridad.MEDIA, "Normal", docente),
                    "DOCENTE no debe poder priorizar");
        }
    }

    // =========================================================================
    // RF-13: Asignacion de responsable (solo COORDINADOR)
    // =========================================================================
    @Nested
    @DisplayName("Asignacion de responsables")
    class AsignacionTest {

        @Test
        @DisplayName("COORDINADOR puede asignar responsable")
        void coordinadorPuedeAsignar() {
            assertDoesNotThrow(() -> solicitud.asignarResponsable(coordinador, "Asignado al docente"),
                    "COORDINADOR debe poder asignar");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede asignar responsable - debe lanzar SolicitudException")
        void estudianteNoPuedeAsignar() {
            assertThrows(SolicitudException.class, () -> solicitud.asignarResponsable(estudiante, "Intento"),
                    "ESTUDIANTE no debe poder asignar");
        }

        @Test
        @DisplayName("DOCENTE no puede asignar responsable - debe lanzar SolicitudException")
        void docenteNoPuedeAsignar() {
            assertThrows(SolicitudException.class, () -> solicitud.asignarResponsable(docente, "Intento"),
                    "DOCENTE no debe poder asignar");
        }
    }

    // =========================================================================
    // RF-13: Atencion (solo DOCENTE)
    // =========================================================================
    @Nested
    @DisplayName("Atencion de solicitudes")
    class AtencionTest {

        @BeforeEach
        void prepararSolicitudEnAtencion() throws SolicitudException {
            // Llevar la solicitud a EN_ATENCION para poder atenderla
            solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "Clasificada");
            solicitud.asignarResponsable(coordinador, "Asignada al docente");
        }

        @Test
        @DisplayName("DOCENTE puede atender la solicitud")
        void docentePuedeAtender() {
            assertDoesNotThrow(() -> solicitud.atenderSolicitud(docente, "Solicitud resuelta"),
                    "DOCENTE debe poder atender");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede atender - debe lanzar SolicitudException")
        void estudianteNoPuedeAtender() {
            assertThrows(SolicitudException.class, () -> solicitud.atenderSolicitud(estudiante, "Intento"),
                    "ESTUDIANTE no debe poder atender");
        }

        @Test
        @DisplayName("COORDINADOR no puede atender - debe lanzar SolicitudException")
        void coordinadorNoPuedeAtender() {
            assertThrows(SolicitudException.class, () -> solicitud.atenderSolicitud(coordinador, "Intento"),
                    "COORDINADOR no debe poder atender (ese es el rol del DOCENTE)");
        }
    }

    // =========================================================================
    // RF-13: Cierre (solo COORDINADOR)
    // =========================================================================
    @Nested
    @DisplayName("Cierre de solicitudes")
    class CierreTest {

        @BeforeEach
        void prepararSolicitudAtendida() throws SolicitudException {
            solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "Clasificada");
            solicitud.asignarResponsable(coordinador, "Asignada");
            solicitud.atenderSolicitud(docente, "Atendida");
        }

        @Test
        @DisplayName("COORDINADOR puede cerrar la solicitud")
        void coordinadorPuedeCerrar() {
            assertDoesNotThrow(() -> solicitud.cerrarSolicitud(coordinador, "Cerrada exitosamente"),
                    "COORDINADOR debe poder cerrar");

            assertNotNull(solicitud.getFechaCierre(), "Debe registrar la fecha de cierre");
        }

        @Test
        @DisplayName("DOCENTE no puede cerrar - debe lanzar SolicitudException")
        void docenteNoPuedeCerrar() {
            assertThrows(SolicitudException.class, () -> solicitud.cerrarSolicitud(docente, "Intento"),
                    "DOCENTE no debe poder cerrar solicitudes");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede cerrar - debe lanzar SolicitudException")
        void estudianteNoPuedeCerrar() {
            assertThrows(SolicitudException.class, () -> solicitud.cerrarSolicitud(estudiante, "Intento"),
                    "ESTUDIANTE no debe poder cerrar solicitudes");
        }
    }

    // =========================================================================
    // RF-13: Solicitud cerrada no es modificable (RF-08 + RF-13)
    // =========================================================================
    @Nested
    @DisplayName("Proteccion de solicitud cerrada")
    class SolicitudCerradaTest {

        @BeforeEach
        void cerrarSolicitud() throws SolicitudException {
            solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "OK");
            solicitud.asignarResponsable(coordinador, "Asignada");
            solicitud.atenderSolicitud(docente, "Atendida");
            solicitud.cerrarSolicitud(coordinador, "Cerrada");
        }

        @Test
        @DisplayName("No se puede clasificar una solicitud cerrada")
        void noSeClasificaSolicitudCerrada() {
            assertThrows(SolicitudException.class,
                    () -> solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, coordinador, "Intento"),
                    "No se debe poder modificar una solicitud cerrada");
        }

        @Test
        @DisplayName("No se puede priorizar una solicitud cerrada")
        void noSePriorizaSolicitudCerrada() {
            assertThrows(SolicitudException.class,
                    () -> solicitud.priorizarSolicitud(NivelPrioridad.ALTA, "Intento", coordinador),
                    "No se debe poder priorizar una solicitud cerrada");
        }

        @Test
        @DisplayName("No se puede asignar responsable a solicitud cerrada")
        void noSeAsignaSolicitudCerrada() {
            assertThrows(SolicitudException.class, () -> solicitud.asignarResponsable(coordinador, "Intento"),
                    "No se debe poder asignar a una solicitud cerrada");
        }
    }

    // =========================================================================
    // RF-13: Permisos de registro (ESTUDIANTE y ADMINISTRATIVO)
    // =========================================================================
    @Nested
    @DisplayName("Registro de solicitudes - permisos por rol")
    class RegistroTest {

        @Test
        @DisplayName("ESTUDIANTE puede registrar solicitudes")
        void estudiantePuedeRegistrar() {
            assertTrue(estudiante.puedeRegistrarSolicitud(),
                    "ESTUDIANTE debe poder registrar solicitudes");
        }

        @Test
        @DisplayName("COORDINADOR no puede registrar solicitudes")
        void coordinadorNoPuedeRegistrar() {
            assertFalse(coordinador.puedeRegistrarSolicitud(),
                    "COORDINADOR no debe tener permiso de registro");
        }

        @Test
        @DisplayName("DOCENTE no puede registrar solicitudes")
        void docenteNoPuedeRegistrar() {
            assertFalse(docente.puedeRegistrarSolicitud(),
                    "DOCENTE no debe tener permiso de registro");
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================
    private Usuario crearUsuario(Long id, String nombre, String identificacion, Rol rol) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre(nombre);
        u.setIdentificacion(identificacion);
        u.setActivo(true);
        u.setRol(rol);
        return u;
    }
}
