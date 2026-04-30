package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Capa Dominio: Servicios de Negocio (AAA Riguroso)")
class ServiciosDominioTest {

    private RegistroSolicitudesService registroService;
    private ClasificacionSolicitudesService clasificacionService;
    private PriorizacionService priorizacionService;
    private AtencionSolicitudesService atencionService;
    private CierreSolicitudService cierreService;

    private Usuario estudianteActivo;
    private Usuario coordinador;
    private Usuario docente;
    private Usuario usuarioInactivo;

    @BeforeEach
    void setUp() {
        registroService = new RegistroSolicitudesService();
        clasificacionService = new ClasificacionSolicitudesService();
        priorizacionService = new PriorizacionService();
        atencionService = new AtencionSolicitudesService();
        cierreService = new CierreSolicitudService();

        estudianteActivo = new Usuario(1L, "Estudiante Activo", "777", "correo@edu.co", true, Rol.ESTUDIANTE);
        coordinador = new Usuario(2L, "Coordinador", "555", "coord@edu.co", true, Rol.COORDINADOR);
        docente = new Usuario(3L, "Docente", "999", "docente@edu.co", true, Rol.DOCENTE);
        usuarioInactivo = new Usuario(4L, "Inactivo", "123", "inactivo@edu.co", false, Rol.ESTUDIANTE);
    }

    @Nested
    @DisplayName("RegistroSolicitudesService - Creacion de Solicitudes")
    class RegistroSolicitudesServiceTest {

        @Test
        @DisplayName("(Exito) - Estudiante activo crea solicitud con datos iniciales correctos")
        void registrarSolicitud_EstudianteActivo_Exitoso() {
            // Arrange
            TipoSolicitud tipo = TipoSolicitud.CONSULTA_ACADEMICA;
            String desc = "Consulta sobre nota de parcial";
            CanalOrigen canal = CanalOrigen.SAC;

            // Act
            Solicitud nueva = registroService.registrarSolicitud(estudianteActivo, tipo, desc, canal);

            // Assert
            assertNotNull(nueva);
            assertEquals(EstadoSolicitud.REGISTRADA, nueva.getEstado());
            assertEquals(estudianteActivo, nueva.getUsuarioSolicitante());
            assertNull(nueva.getPrioridad());
            assertNull(nueva.getFechaCierre());
        }

        @Test
        @DisplayName("(Fallo) - Usuario inactivo NO puede registrar")
        void registrarSolicitud_UsuarioInactivo_LanzaExcepcion() {
            // Arrange
            TipoSolicitud tipo = TipoSolicitud.HOMOLOGACION;

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                registroService.registrarSolicitud(usuarioInactivo, tipo, "Desc", CanalOrigen.PORTAL_WEB);
            });
        }

        @Test
        @DisplayName("(Fallo) - Coordinador NO puede registrar solicitud (permiso de estudiante)")
        void registrarSolicitud_CoordinadorIntenta_LanzaExcepcion() {
            // Arrange
            // Coordinador no tiene permiso de registro

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                registroService.registrarSolicitud(coordinador, TipoSolicitud.HOMOLOGACION, "D", CanalOrigen.PORTAL_WEB);
            });
        }
    }

    @Nested
    @DisplayName("ClasificacionSolicitudesService - Clasificacion de Solicitudes")
    class ClasificacionSolicitudesServiceTest {

        @Test
        @DisplayName("(Exito) - Coordinador clasifica solicitud registrada")
        void clasificarSolicitud_CoordinadorClasifica_Exitoso() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.SOLICITUD_CUPOS, "Test", CanalOrigen.PORTAL_WEB);

            // Act
            Solicitud clasificada = clasificacionService.clasificarSolicitud(solicitud, 
                    TipoSolicitud.HOMOLOGACION, coordinador, "Reclasificada a homologacion");

            // Assert
            assertEquals(EstadoSolicitud.CLASIFICADA, clasificada.getEstado());
            assertEquals(TipoSolicitud.HOMOLOGACION, clasificada.getTipo());
        }

        @Test
        @DisplayName("(Fallo) - Estudiante NO puede clasificar (solo COORDINADOR)")
        void clasificarSolicitud_EstudianteIntenta_LanzaExcepcion() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.SOLICITUD_CUPOS, "Test", CanalOrigen.PORTAL_WEB);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                        estudianteActivo, "Intento de clasificar");
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede clasificar solicitud nula")
        void clasificarSolicitud_SolicitudNula_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                clasificacionService.clasificarSolicitud(null, TipoSolicitud.HOMOLOGACION, 
                        coordinador, "Test");
            });
        }
    }

    @Nested
    @DisplayName("PriorizacionService - Priorizacion de Solicitudes")
    class PriorizacionServiceTest {

        @Test
        @DisplayName("(Exito) - Coordinador prioriza solicitud clasificada")
        void priorizarSolicitud_CoordinadorPrioriza_Exitoso() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.HOMOLOGACION, "Test", CanalOrigen.PORTAL_WEB);
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                    coordinador, "Clasificada");

            // Act
            Solicitud priorizada = priorizacionService.priorizarSolicitud(coordinador, 
                    "Es urgente cierra semestre", solicitud, NivelPrioridad.ALTA);

            // Assert
            assertNotNull(priorizada.getPrioridad());
            assertEquals(NivelPrioridad.ALTA, priorizada.getPrioridad().nivel());
        }

        @Test
        @DisplayName("(Fallo) - Estudiante NO puede priorizar (solo COORDINADOR)")
        void priorizarSolicitud_EstudianteIntenta_LanzaExcepcion() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.HOMOLOGACION, "Test", CanalOrigen.PORTAL_WEB);
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                    coordinador, "Clasificada");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                priorizacionService.priorizarSolicitud(estudianteActivo, "Justificacion", 
                        solicitud, NivelPrioridad.MEDIA);
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede priorizar con justificacion nula")
        void priorizarSolicitud_JustificacionNula_LanzaExcepcion() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.HOMOLOGACION, "Test", CanalOrigen.PORTAL_WEB);
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                    coordinador, "Clasificada");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                priorizacionService.priorizarSolicitud(coordinador, null, 
                        solicitud, NivelPrioridad.MEDIA);
            });
        }
    }

    @Nested
    @DisplayName("AtencionSolicitudesService - Asignacion y Atencion")
    class AtencionSolicitudesServiceTest {

        @Test
        @DisplayName("(Exito) - Coordinador asigna responsable a solicitud clasificada")
        void asignarResponsable_CoordinadorAsigna_Exitoso() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.HOMOLOGACION, "Test", CanalOrigen.PORTAL_WEB);
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                    coordinador, "Clasificada");

            // Act
            Solicitud asignada = atencionService.asignarResponsable(coordinador, solicitud, 
                    "Se asigna al docente Juan");

            // Assert
            assertEquals(EstadoSolicitud.EN_ATENCION, asignada.getEstado());
        }

        @Test
        @DisplayName("(Fallo) - Docente NO puede asignar responsables (solo COORDINADOR)")
        void asignarResponsable_DocenteIntenta_LanzaExcepcion() throws SolicitudException {
            // Arrange
            Solicitud solicitud = registroService.registrarSolicitud(estudianteActivo, 
                    TipoSolicitud.HOMOLOGACION, "Test", CanalOrigen.PORTAL_WEB);
            clasificacionService.clasificarSolicitud(solicitud, TipoSolicitud.HOMOLOGACION, 
                    coordinador, "Clasificada");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                atencionService.asignarResponsable(docente, solicitud, "Intento asignar");
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede asignar responsable a solicitud nula")
        void asignarResponsable_SolicitudNula_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                atencionService.asignarResponsable(coordinador, null, "Intento asignacion");
            });
        }
    }

    @Nested
    @DisplayName("CierreSolicitudService - Cierre de Solicitudes")
    class CierreSolicitudServiceTest {

        @Test
        @DisplayName("(Fallo) - Docente NO puede cerrar (solo COORDINADOR)")
        void cerrarSolicitud_DocenteIntenta_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                cierreService.cerrarSolicitud(docente, null, "Intento cerrar");
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede cerrar con observacion nula")
        void cerrarSolicitud_ObservacionNula_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                cierreService.cerrarSolicitud(coordinador, null, null);
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede cerrar con observacion vacia")
        void cerrarSolicitud_ObservacionVacia_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                cierreService.cerrarSolicitud(coordinador, null, "");
            });
        }

        @Test
        @DisplayName("(Fallo) - No se puede cerrar solicitud nula")
        void cerrarSolicitud_SolicitudNula_LanzaExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                cierreService.cerrarSolicitud(coordinador, null, "Observacion");
            });
        }

        @Test
        @DisplayName("(Fallo) - Coordinador inactivo NO puede cerrar")
        void cerrarSolicitud_CoordinadorInactivo_LanzaExcepcion() {
            // Arrange
            Usuario coordinadorInactivo = new Usuario(6L, "Coord Inactivo", "666", "coord@edu.co", false, Rol.COORDINADOR);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                cierreService.cerrarSolicitud(coordinadorInactivo, null, "Observacion");
            });
        }
    }
}
