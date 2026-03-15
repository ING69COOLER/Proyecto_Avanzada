package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ClasificacionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-02 – Clasificación de solicitudes
 *
 * Verifica que el sistema permita clasificar una solicitud académica según su tipo:
 * - Registro de asignaturas
 * - Homologación
 * - Cancelación de asignaturas
 * - Solicitud de cupos
 * - Consulta académica
 */
@DisplayName("RF-02: Clasificación de solicitudes")
class RF02_ClasificacionSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioEstudiante;
    private RepositorioSolicitud repositorio;
    private ClasificacionSolicitudesService clasificacionSolicitudesService;
    
    @BeforeEach
    void setup() {
        repositorio = RepositorioSolicitud.getInstancia();
        clasificacionSolicitudesService = new ClasificacionSolicitudesService();
        
        // Coordinador autorizado para clasificar
        usuarioCoordinador = new Usuario(1L, "Carlos López", "1001234567", "carlos@admin.com", true, Rol.COORDINADOR);
        
        // Estudiante no autorizado para clasificar
        usuarioEstudiante = new Usuario(2L, "Pedro García", "1001234568", "pedro@student.com", true, Rol.ESTUDIANTE);
        
        // Crear solicitud base
        solicitud = new Solicitud(
            TipoSolicitud.CONSULTA_ACADEMICA,
            "Solicitud sin clasificar",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            null,
            EstadoSolicitud.REGISTRADA,
            usuarioCoordinador,
            null
        );
    }
    
    @Test
    @DisplayName("Debe clasificar solicitud como REGISTRO_ASIGNATURA")
    void testClasificarComoRegistroAsignatura() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada como registro de asignatura");
        
        // Assert
        assertEquals(TipoSolicitud.REGISTRO_ASIGNATURA, solicitud.getTipo(), 
                     "El tipo debe ser REGISTRO_ASIGNATURA");
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado(), 
                     "El estado debe cambiar a CLASIFICADA");
    }
    
    @Test
    @DisplayName("Debe clasificar solicitud como HOMOLOGACION")
    void testClasificarComoHomologacion() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada como homologación");
        
        // Assert
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }
    
    @Test
    @DisplayName("Debe clasificar solicitud como CANCELACION_ASIGNATURA")
    void testClasificarComoCancelacion() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.CANCELACION_ASIGNATURA, usuarioCoordinador, "Clasificada como cancelación");
        
        // Assert
        assertEquals(TipoSolicitud.CANCELACION_ASIGNATURA, solicitud.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }
    
    @Test
    @DisplayName("Debe clasificar solicitud como SOLICITUD_CUPOS")
    void testClasificarComoSolicitudCupos() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.SOLICITUD_CUPOS, usuarioCoordinador, "Clasificada como solicitud de cupos");
        
        // Assert
        assertEquals(TipoSolicitud.SOLICITUD_CUPOS, solicitud.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }
    
    @Test
    @DisplayName("Debe validar que el tipo de solicitud no sea nulo")
    void testTipoNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            solicitud.clasificarSolicitud(null, usuarioCoordinador, "observación");
        }, "Debe lanzar excepción si el tipo es nulo");
    }
    
    @Test
    @DisplayName("Debe registrar la clasificación en el historial")
    void testRegistroEnHistorial() throws SolicitudException {
        // Arrange
        int tamanhoHistorialAntes = solicitud.getHistorial().size();
        
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada correctamente");
        
        // Assert
        int tamanhoHistorialDespues = solicitud.getHistorial().size();
        assertEquals(tamanhoHistorialAntes + 1, tamanhoHistorialDespues, 
                     "El historial debe tener una entrada adicional");
    }
    //Solicitud solicitud, TipoSolicitud tipoSolicitud,
           // Usuario usuario, String observacion
    @Test
    @DisplayName("Debe validar que solo COORDINADOR pueda clasificar")
    void testValidarPermisosCordinador() {
        // Arrange - Solicitud registrada y usuario estudiante sin permisos
        
        // Act & Assert - El servicio de dominio valida los permisos del usuario
        assertThrows(IllegalArgumentException.class, () -> {
            clasificacionSolicitudesService.clasificarSolicitud(solicitud, TipoSolicitud.REGISTRO_ASIGNATURA, usuarioEstudiante, "obs");
        }, "Debe lanzar excepción si el usuario no es COORDINADOR");
    }
    
    @Test
    @DisplayName("Debe validar usuario activo para clasificar")
    void testValidarUsuarioActivo() {
        // Arrange - Coordinador inactivo intenta clasificar
        usuarioCoordinador.setActivo(false);
        
        // Act & Assert - El servicio de dominio valida que el usuario esté activo
        assertThrows(IllegalArgumentException.class, () -> {
            clasificacionSolicitudesService.clasificarSolicitud(solicitud, TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "obs");
        }, "Debe lanzar excepción si el usuario no está activo");
    }
}
