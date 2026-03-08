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
 * RF-08 – Cierre de solicitudes
 *
 * Verifica que el sistema permita cerrar una solicitud únicamente cuando:
 * - La solicitud haya sido atendida (estado ATENDIDA)
 * - Se registre una observación de cierre
 * - Validar que una solicitud cerrada no pueda ser modificada
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
        usuarioCoordinador = new Usuario();
        usuarioCoordinador.setId(1L);
        usuarioCoordinador.setNombre("Coordinador");
        usuarioCoordinador.setIdentificacion("1001234567");
        usuarioCoordinador.setActivo(true);
        usuarioCoordinador.setRol(Rol.COORDINADOR);
        
        // Docente (no autorizado para cerrar)
        usuarioDocente = new Usuario();
        usuarioDocente.setId(2L);
        usuarioDocente.setNombre("Docente");
        usuarioDocente.setIdentificacion("1001234568");
        usuarioDocente.setActivo(true);
        usuarioDocente.setRol(Rol.DOCENTE);
        
        // Estudiante (no autorizado)
        usuarioEstudiante = new Usuario();
        usuarioEstudiante.setId(3L);
        usuarioEstudiante.setNombre("Estudiante");
        usuarioEstudiante.setIdentificacion("1001234569");
        usuarioEstudiante.setActivo(true);
        usuarioEstudiante.setRol(Rol.ESTUDIANTE);
        
        // Crear solicitud en estado REGISTRADA
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud de prueba",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            "1001234567",
            null, null, usuarioCoordinador, null
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
    @DisplayName("No debe cerrar si el usuario no es COORDINADOR")
    void testNoDebeCerrarSiUsuarioNoEsCoordinador() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act & Assert - Intento de cierre por DOCENTE
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioDocente, "Observación");
        }, "Solo COORDINADOR puede cerrar");
    }
    
    @Test
    @DisplayName("No debe cerrar si el usuario es ESTUDIANTE")
    void testNoDebeCerrarSiUsuarioEsEstudiante() throws SolicitudException {
        // Arrange - Completar ciclo hasta ATENDIDA
        solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act & Assert - Intento de cierre por ESTUDIANTE
        assertThrows(SolicitudException.class, () -> {
            solicitud.cerrarSolicitud(usuarioEstudiante, "Observación");
        }, "ESTUDIANTE no puede cerrar");
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
