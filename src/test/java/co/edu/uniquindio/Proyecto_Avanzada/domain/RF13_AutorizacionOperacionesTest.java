package co.edu.uniquindio.Proyecto_Avanzada.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.AtencionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.CierreSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ClasificacionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.PriorizacionService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
/*
 
 * * ClasificacionTest:
 * - "COORDINADOR puede clasificar la solicitud"
 * - "ESTUDIANTE no puede clasificar - debe lanzar excepción"
 * - "DOCENTE no puede clasificar - debe lanzar excepción"
 *
 * PriorizacionTest:
 * - "COORDINADOR puede priorizar la solicitud"
 * - "ESTUDIANTE no puede priorizar - debe lanzar SolicitudException"
 * - "DOCENTE no puede priorizar - debe lanzar SolicitudException"
 *
 * AsignacionTest:
 * - "COORDINADOR puede asignar responsable"
 * - "ESTUDIANTE no puede asignar responsable - debe lanzar SolicitudException"
 * - "DOCENTE no puede asignar responsable - debe lanzar SolicitudException"
 *
 * AtencionTest:
 * - "DOCENTE puede atender la solicitud"
 * - "ESTUDIANTE no puede atender - debe lanzar SolicitudException"
 * - "COORDINADOR no puede atender - debe lanzar SolicitudException"
 *
 * CierreTest:
 * - "COORDINADOR puede cerrar la solicitud"
 * - "DOCENTE no puede cerrar - debe lanzar SolicitudException"
 * - "ESTUDIANTE no puede cerrar - debe lanzar SolicitudException"
 *
 * SolicitudCerradaTest:
 * - "No se puede clasificar una solicitud cerrada"
 * - "No se puede priorizar una solicitud cerrada"
 * - "No se puede asignar responsable a solicitud cerrada"
 *
 * RegistroTest:
 * - "ESTUDIANTE puede registrar solicitudes"
 * - "COORDINADOR no puede registrar solicitudes"
 * - "DOCENTE no puede registrar solicitudes"
*/
@DisplayName("RF-13: Autorizacion basica de operaciones")
class RF13_AutorizacionOperacionesTest {

    private Usuario estudiante;
    private Usuario docente;
    private Usuario coordinador;
    private Solicitud solicitud;

    private ClasificacionSolicitudesService clasificacionService;
    private PriorizacionService priorizacionService;
    private AtencionSolicitudesService atencionService;
    private CierreSolicitudService cierreService;

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
                null,
                null,
                estudiante,
                null);

        IRepositorioSolicitud repoMock = Mockito.mock(IRepositorioSolicitud.class);

        clasificacionService = new ClasificacionSolicitudesService();
        ReflectionTestUtils.setField(clasificacionService, "repositorioSolicitud", repoMock);

        cierreService = new CierreSolicitudService();
        ReflectionTestUtils.setField(cierreService, "repositorioSolicitud", repoMock);

        priorizacionService = new PriorizacionService();
        atencionService = new AtencionSolicitudesService();
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
            assertDoesNotThrow(() -> clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, coordinador, "OK"),
                    "COORDINADOR debe poder clasificar");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede clasificar - debe lanzar excepcion")
        void estudianteNoPuedeClasificar() {
            assertThrows(Exception.class,
                    () -> clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, estudiante, "Intento"),
                    "ESTUDIANTE no debe poder clasificar");
        }

        @Test
        @DisplayName("DOCENTE no puede clasificar - debe lanzar excepcion")
        void docenteNoPuedeClasificar() {
            assertThrows(Exception.class,
                    () -> clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, docente, "Intento"),
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
            assertDoesNotThrow(() -> priorizacionService.priorizarSolicitud(coordinador, "Urgente", solicitud, NivelPrioridad.ALTA),
                    "COORDINADOR debe poder priorizar");

            assertNotNull(solicitud.getPrioridad());
            assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().nivel());
        }

        @Test
        @DisplayName("ESTUDIANTE no puede priorizar - debe lanzar excepcion")
        void estudianteNoPuedePriorizar() {
            assertThrows(Exception.class,
                    () -> priorizacionService.priorizarSolicitud(estudiante, "Urgente", solicitud, NivelPrioridad.ALTA),
                    "ESTUDIANTE no debe poder priorizar");
        }

        @Test
        @DisplayName("DOCENTE no puede priorizar - debe lanzar excepcion")
        void docenteNoPuedePriorizar() {
            assertThrows(Exception.class,
                    () -> priorizacionService.priorizarSolicitud(docente, "Normal", solicitud, NivelPrioridad.MEDIA),
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
            assertDoesNotThrow(() -> atencionService.asignarResponsable(coordinador, solicitud, "Asignado al docente"),
                    "COORDINADOR debe poder asignar");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede asignar responsable - debe lanzar excepcion")
        void estudianteNoPuedeAsignar() {
            assertThrows(Exception.class, () -> atencionService.asignarResponsable(estudiante, solicitud, "Intento"),
                    "ESTUDIANTE no debe poder asignar");
        }

        @Test
        @DisplayName("DOCENTE no puede asignar responsable - debe lanzar excepcion")
        void docenteNoPuedeAsignar() {
            assertThrows(Exception.class, () -> atencionService.asignarResponsable(docente, solicitud, "Intento"),
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
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "Clasificada");
            atencionService.asignarResponsable(coordinador, solicitud, "Asignada al docente");
            solicitud.asignarResponsable(docente, "Docente added to history to allow attention");
        }

        @Test
        @DisplayName("DOCENTE puede atender la solicitud")
        void docentePuedeAtender() {
            assertDoesNotThrow(() -> atencionService.atenderSolicitud(docente, solicitud, "Solicitud resuelta"),
                    "DOCENTE debe poder atender");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede atender - debe lanzar excepcion")
        void estudianteNoPuedeAtender() {
            assertThrows(Exception.class, () -> atencionService.atenderSolicitud(estudiante, solicitud, "Intento"),
                    "ESTUDIANTE no debe poder atender");
        }

        @Test
        @DisplayName("COORDINADOR no puede atender - debe lanzar excepcion")
        void coordinadorNoPuedeAtender() {
            assertThrows(Exception.class, () -> atencionService.atenderSolicitud(coordinador, solicitud, "Intento"),
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
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "Clasificada");
            atencionService.asignarResponsable(coordinador, solicitud, "Asignada");
            solicitud.asignarResponsable(docente, "Docente added to history to allow attention");
            atencionService.atenderSolicitud(docente, solicitud, "Atendida");
        }

        @Test
        @DisplayName("COORDINADOR puede cerrar la solicitud")
        void coordinadorPuedeCerrar() {
            assertDoesNotThrow(() -> cierreService.cerrarSolicitud(coordinador, solicitud, "Cerrada exitosamente"),
                    "COORDINADOR debe poder cerrar");

            assertNotNull(solicitud.getFechaCierre(), "Debe registrar la fecha de cierre");
        }

        @Test
        @DisplayName("DOCENTE no puede cerrar - debe lanzar excepcion")
        void docenteNoPuedeCerrar() {
            assertThrows(Exception.class, () -> cierreService.cerrarSolicitud(docente, solicitud, "Intento"),
                    "DOCENTE no debe poder cerrar solicitudes");
        }

        @Test
        @DisplayName("ESTUDIANTE no puede cerrar - debe lanzar excepcion")
        void estudianteNoPuedeCerrar() {
            assertThrows(Exception.class, () -> cierreService.cerrarSolicitud(estudiante, solicitud, "Intento"),
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
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, "OK");
            atencionService.asignarResponsable(coordinador, solicitud, "Asignada");
            solicitud.asignarResponsable(docente, "Docente added to history to allow attention");
            atencionService.atenderSolicitud(docente, solicitud, "Atendida");
            cierreService.cerrarSolicitud(coordinador, solicitud, "Cerrada");
        }

        @Test
        @DisplayName("No se puede clasificar una solicitud cerrada")
        void noSeClasificaSolicitudCerrada() {
            assertThrows(Exception.class,
                    () -> clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, coordinador, "Intento"),
                    "No se debe poder modificar una solicitud cerrada");
        }

        @Test
        @DisplayName("No se puede priorizar una solicitud cerrada")
        void noSePriorizaSolicitudCerrada() {
            assertThrows(Exception.class,
                    () -> priorizacionService.priorizarSolicitud(coordinador, "Intento", solicitud, NivelPrioridad.ALTA),
                    "No se debe poder priorizar una solicitud cerrada");
        }

        @Test
        @DisplayName("No se puede asignar responsable a solicitud cerrada")
        void noSeAsignaSolicitudCerrada() {
            assertThrows(Exception.class, () -> atencionService.asignarResponsable(coordinador, solicitud, "Intento"),
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
        return new Usuario(id, nombre, identificacion, null, true, rol);
    }
}
