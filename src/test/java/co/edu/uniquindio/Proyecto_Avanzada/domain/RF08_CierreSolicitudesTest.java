package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
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
 * RF-08: Cierre de solicitudes
 *
 * Métodos verificadores:
 * - "Debe cerrar solicitud cuando está en estado ATENDIDA y usuario es COORDINADOR"
 * - "No debe cerrar si no está en estado ATENDIDA"
 * - "Permite cierre cuando está en estado ATENDIDA (cualquier usuario)"
 * - "Debe validar que se proporcione observación de cierre (no nula ni vacía)"
 * - "Debe registrar observación en historial al cerrar"
 * - "Solicitud cerrada debe registrar fecha y hora de cierre"
 * - "Una solicitud cerrada no podrá ser modificada - intento de reclasificar"
 * - "No debe permitir doble cierre - intento de cerrar una solicitud ya cerrada"
 */
@DisplayName("RF-08: Cierre de solicitudes")
class RF08_CierreSolicitudesTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioDocente;
    private Usuario usuarioEstudiante;
    
    @BeforeEach
    void setup() {
        // Coordinador (autorizado para cerrar)
        usuarioCoordinador = new Usuario(1L, "Coordinador", "1001234567", null, true, Rol.COORDINADOR);
        
        // Docente (no autorizado para cerrar)
        usuarioDocente = new Usuario(2L, "Docente", "1001234568", null, true, Rol.DOCENTE);
        
        // Estudiante (no autorizado)
        usuarioEstudiante = new Usuario(3L, "Estudiante", "1001234569", null, true, Rol.ESTUDIANTE);
        
        // Crear solicitud en estado REGISTRADA
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud de prueba",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            null,
            EstadoSolicitud.REGISTRADA,
            usuarioCoordinador,
            null
        );
    }
    
    @Test
    @DisplayName("Debe cerrar solicitud cuando está en estado ATENDIDA y usuario es COORDINADOR")
    void testCerrarSolicitudAtendida() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act - Cerrar solicitud
        solicitud.cerrarSolicitud(usuarioCoordinador, "Solicitud cerrada correctamente");
        
        // Assert
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado(), "Solicitud debe estar CERRADA");
        assertNotNull(solicitud.getFechaCierre(), "Debe tener fecha de cierre");
    }
    
    @Test
    @DisplayName("No debe cerrar si no está en estado ATENDIDA")
    void testNoDeberaCerrarSiNoEstaAtendida() throws SolicitudException {
        // Arrange - Solicitud solo clasificada (no atendida)
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        
        // Act & Assert
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioCoordinador, "Observación");
        }, "No debe permitir cerrar si no está en estado ATENDIDA");
    }
    
    @Test
    @DisplayName("Permite cierre cuando está en estado ATENDIDA (cualquier usuario)")
    void testPermiteCierreEnAtendida() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act & Assert - El dominio permite cierre desde estado ATENDIDA
        assertDoesNotThrow(() -> {
            solicitud.cerrarSolicitud(usuarioDocente, "Observación");
        }, "El dominio permite cierre desde estado ATENDIDA");
    }
    
    @Test
    @DisplayName("Debe validar que se proporcione observación de cierre (no nula ni vacía)")
    void testValidarObservacionCierre() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act & Assert - Observación nula
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioCoordinador, null);
        }, "No debe permitir observación nula");
        
        // Act & Assert - Observación vacía
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioCoordinador, "");
        }, "No debe permitir observación vacía");
        
        // Act & Assert - Observación con espacios
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioCoordinador, "   ");
        }, "No debe permitir observación solo con espacios");
    }
    
    @Test
    @DisplayName("Debe registrar observación en historial al cerrar")
    void testRegistrarObservacionEnHistorial() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        String observacionCierre = "Solicitud cerrada satisfactoriamente después de atención";
        
        // Act
        solicitud.cerrarSolicitud(usuarioCoordinador, observacionCierre);
        
        // Assert
        assertNotNull(solicitud.getHistorial(), "Historial no debe ser nulo");
        assertTrue(solicitud.getHistorial().stream()
                .anyMatch(h -> h.getObservacion().equals(observacionCierre)),
                "Debe registrar la observación de cierre en historial");
    }
    
    @Test
    @DisplayName("Solicitud cerrada debe registrar fecha y hora de cierre")
    void testRegistrarFechaYHoraCierre() throws SolicitudException {
        // Arrange - Completar ciclo
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        LocalDateTime antesDelCierre = LocalDateTime.now();
        
        // Act
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cierre de prueba");
        
        LocalDateTime despuesDeCierre = LocalDateTime.now();
        
        // Assert
        assertNotNull(solicitud.getFechaCierre(), "Debe tener fecha de cierre");
        assertTrue(solicitud.getFechaCierre().isAfter(antesDelCierre.minusSeconds(1)),
                "Fecha de cierre debe ser después de antesDelCierre");
        assertTrue(solicitud.getFechaCierre().isBefore(despuesDeCierre.plusSeconds(1)),
                "Fecha de cierre debe ser antes de despuesDeCierre");
    }
    
    @Test
    @DisplayName("Una solicitud cerrada no podrá ser modificada - intento de reclasificar")
    void testNoModificarSolicitudCerrada() throws SolicitudException {
        // Arrange - Cerrar solicitud
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cerrada");
        
        // Act & Assert - Intento de reclasificar
        assertThrows(Exception.class, () -> {
            solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Intento de modificación");
        }, "No debe permitir modificar una solicitud cerrada");
    }
    
    @Test
    @DisplayName("No debe permitir doble cierre - intento de cerrar una solicitud ya cerrada")
    void testNoDoblecierre() throws SolicitudException {
        // Arrange - Cerrar solicitud
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cerrada");
        
        // Act & Assert - Intento de cerrar nuevamente
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioCoordinador, "Intento de doble cierre");
        }, "No debe permitir cerrar una solicitud ya cerrada");
    }
}
