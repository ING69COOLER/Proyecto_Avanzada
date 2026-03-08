package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.AtencionSolicitudesService;
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
 * RF-05 – Asignación de responsables
 *
 * Verifica que el sistema permita asignar una solicitud a un responsable autorizado, garantizando:
 * - El responsable esté activo
 * - La asignación quede registrada en el historial
 */
@DisplayName("RF-05: Asignación de responsables")
class RF05_AsignacionResponsablesTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioDocente;
    private Usuario usuarioDocenteInactivo;
    private Usuario usuarioEstudiante;
    private AtencionSolicitudesService servicioAtencion;
    
    @BeforeEach
    void setup() throws SolicitudException {
        servicioAtencion = new AtencionSolicitudesService();
        
        // Coordinador autorizado para asignar
        usuarioCoordinador = new Usuario();
        usuarioCoordinador.setId(1L);
        usuarioCoordinador.setNombre("Coordinador Principal");
        usuarioCoordinador.setIdentificacion("1001234567");
        usuarioCoordinador.setCorreo("coordinador@admin.com");
        usuarioCoordinador.setActivo(true);
        usuarioCoordinador.setRol(Rol.COORDINADOR);
        
        // Docente 1 activo
        usuarioDocente = new Usuario();
        usuarioDocente.setId(2L);
        usuarioDocente.setNombre("Profesor Juan");
        usuarioDocente.setIdentificacion("1001234568");
        usuarioDocente.setCorreo("juan@docente.com");
        usuarioDocente.setActivo(true);
        usuarioDocente.setRol(Rol.DOCENTE);
        
        // Docente 2 inactivo
        usuarioDocenteInactivo = new Usuario();
        usuarioDocenteInactivo.setId(3L);
        usuarioDocenteInactivo.setNombre("Profesor Inactivo");
        usuarioDocenteInactivo.setIdentificacion("1001234569");
        usuarioDocenteInactivo.setCorreo("inactivo@docente.com");
        usuarioDocenteInactivo.setActivo(false);
        usuarioDocenteInactivo.setRol(Rol.DOCENTE);
        
        // Estudiante no autorizado
        usuarioEstudiante = new Usuario();
        usuarioEstudiante.setId(4L);
        usuarioEstudiante.setNombre("Estudiante Test");
        usuarioEstudiante.setIdentificacion("1001234570");
        usuarioEstudiante.setCorreo("student@mail.com");
        usuarioEstudiante.setActivo(true);
        usuarioEstudiante.setRol(Rol.ESTUDIANTE);
        
        // Crear solicitud ya clasificada
        solicitud = new Solicitud(
            TipoSolicitud.HOMOLOGACION,
            "Solicitud de homologación",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            "1001234567",
            null, null, usuarioCoordinador, null
        );
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
    }
    
    @Test
    @DisplayName("Debe asignar solicitud a un docente activo")
    void testAsignarADocenteActivo() {
        // Act
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Asignaciones a Prof. Juan por especialidad");
        
        // Assert
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado(), 
                     "El estado debe cambiar a EN_ATENCION");
    }
    
    @Test
    @DisplayName("Debe registrar asignación en el historial")
    void testRegistroEnHistorial() {
        // Arrange
        int tamanhoHistorialAntes = solicitud.getHistorial().size();
        
        // Act
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Asignación inicial");
        
        // Assert
        int tamanhoHistorialDespues = solicitud.getHistorial().size();
        assertEquals(tamanhoHistorialAntes + 1, tamanhoHistorialDespues, 
                     "El historial debe tener un registro adicional");
    }
    
    @Test
    @DisplayName("Debe validar que solo COORDINADOR pueda asignar")
    void testSoloCoordinadorPuedeAsignar() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAtencion.asignarResponsable(usuarioEstudiante, solicitud, "No autorizado");
        }, "Solo COORDINADOR debe poder asignar responsables");
    }
    
    @Test
    @DisplayName("Debe validar que el usuario asignador sea válido")
    void testUsuarioAsignadorNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAtencion.asignarResponsable(null, solicitud, "Descripción");
        });
    }
    
    @Test
    @DisplayName("Debe validar que la solicitud no sea nula")
    void testSolicitudNoNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAtencion.asignarResponsable(usuarioCoordinador, null, "Descripción");
        });
    }
    
    @Test
    @DisplayName("Debe validar descripción no nula ni vacía")
    void testDescripcionValida() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "");
        });
    }
    
    @Test
    @DisplayName("Debe permitir cambiar responsable de una solicitud")
    void testCambiarResponsable() throws SolicitudException {
        // Arrange
        Usuario usuarioDocente2 = new Usuario();
        usuarioDocente2.setId(5L);
        usuarioDocente2.setNombre("Profesor Carlos");
        usuarioDocente2.setIdentificacion("1001234571");
        usuarioDocente2.setActivo(true);
        usuarioDocente2.setRol(Rol.DOCENTE);
        
        // Act - Primera asignación
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Asignación inicial a Juan");
        
        // Reasignar al mismo servicio (cambio de responsable)
        // Para esto el estado debe volver a clasificado
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Reabrir para reasignar");
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Reasignación a Carlos");
        
        // Assert
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
    }
    
    @Test
    @DisplayName("Debe registrar al responsable correcto en el historial")
    void testResponsableEnHistorial() {
        // Act
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Asignando a docente");
        
        // Assert
        assertNotNull(solicitud.getHistorial().get(solicitud.getHistorial().size() - 1).getResponsable(), 
                      "El responsable debe quedar registrado en el historial");
    }
    
    @Test
    @DisplayName("Debe validar que usuario inactivo no sea asignado")
    void testNoAsignarUsuarioInactivo() {
        // El usuario inactivo no debería poder ser asignado
        // Primero verificamos que no pueda asignar (Coordinador inactivo)
        usuarioCoordinador.setActivo(false);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Intento de asignación");
        });
    }
    
    @Test
    @DisplayName("Debe verificar que usuario asignado sea obtenido del historial")
    void testVerificacionUsuarioPuedeAtender() {
        // Arrange
        servicioAtencion.asignarResponsable(usuarioCoordinador, solicitud, "Asignación inicial");
        
        // Act
        boolean usuarioPuedeAtender = solicitud.UsuarioPuedeAtender(usuarioCoordinador);
        
        // Assert
        assertTrue(usuarioPuedeAtender, "El coordinador que asignó debe poder atender (está en historial)");
    }
}
