package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.PriorizacionService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-03 – Priorización de solicitudes
 *
 * Verifica que el sistema asigne una prioridad a cada solicitud con base en reglas definidas:
 * - Tipo de solicitud
 * - Impacto académico
 * - Fecha límite asociada
 * - La prioridad debe quedar registrada junto con una justificación
 */
@DisplayName("RF-03: Priorización de solicitudes")
class RF03_PriorizacionSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioEstudiante;
    private PriorizacionService servicioPriorizacion;
    
    @BeforeEach
    void setup() {
        servicioPriorizacion = new PriorizacionService();
        
        // Coordinador autorizado para priorizar
        usuarioCoordinador = new Usuario(1L, "Ana Martínez", "1001234567", "ana@admin.com", true, Rol.COORDINADOR);
        
        // Estudiante no autorizado para priorizar
        usuarioEstudiante = new Usuario(2L, "Juan Rodríguez", "1001234568", "juan@student.com", true, Rol.ESTUDIANTE);
        
        // Crear solicitud base
        solicitud = new Solicitud(
            TipoSolicitud.CANCELACION_ASIGNATURA,
            "Solicitud sin prioridad",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            null,
            EstadoSolicitud.REGISTRADA,
            usuarioCoordinador,
            null
        );
    }
    
    @Test
    @DisplayName("Debe asignar prioridad ALTA a solicitud")
    void testAsignarPrioridadAlta() throws SolicitudException {
        // Act
        servicioPriorizacion.priorizarSolicitud(
            usuarioCoordinador,
            "Cancelación en período de prueba académica",
            solicitud,
            NivelPrioridad.ALTA
        );
        
        // Assert
        assertNotNull(solicitud.getPrioridad(), "La prioridad no debe ser nula");
        assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().getNivel(), 
                     "La prioridad debe ser ALTA");
    }
    
    @Test
    @DisplayName("Debe asignar prioridad MEDIA a solicitud")
    void testAsignarPrioridadMedia() throws SolicitudException {
        // Act
        servicioPriorizacion.priorizarSolicitud(
            usuarioCoordinador,
            "Solicitud de homologación regular",
            solicitud,
            NivelPrioridad.MEDIA
        );
        
        // Assert
        assertNotNull(solicitud.getPrioridad());
        assertEquals(NivelPrioridad.MEDIA, solicitud.getPrioridad().getNivel());
    }
    
    @Test
    @DisplayName("Debe asignar prioridad BAJA a solicitud")
    void testAsignarPrioridadBaja() throws SolicitudException {
        // Act
        servicioPriorizacion.priorizarSolicitud(
            usuarioCoordinador,
            "Consulta académica sin urgencia",
            solicitud,
            NivelPrioridad.BAJA
        );
        
        // Assert
        assertNotNull(solicitud.getPrioridad());
        assertEquals(NivelPrioridad.BAJA, solicitud.getPrioridad().getNivel());
    }
    
    @Test
    @DisplayName("Debe guardar justificación de priorización")
    void testJustificacionPriorizacion() throws SolicitudException {
        // Arrange
        String justificacion = "Estudiante próximo a graduarse con fecha límite en 2 semanas";
        
        // Act
        servicioPriorizacion.priorizarSolicitud(
            usuarioCoordinador,
            justificacion,
            solicitud,
            NivelPrioridad.ALTA
        );
        
        // Assert
        assertEquals(justificacion, solicitud.getPrioridad().getDescripcion(), 
                     "La justificación debe almacenarse");
    }
    
    @Test
    @DisplayName("Debe validar que la justificación no sea nula")
    void testJustificacionNoNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPriorizacion.priorizarSolicitud(
                usuarioCoordinador,
                null,
                solicitud,
                NivelPrioridad.ALTA
            );
        }, "Debe lanzar excepción si la justificación es nula");
    }
    
    @Test
    @DisplayName("Debe validar que solo COORDINADOR pueda priorizar")
    void testSoloCoordinadorPuedePriorizar() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPriorizacion.priorizarSolicitud(
                usuarioEstudiante,
                "Justificación válida",
                solicitud,
                NivelPrioridad.ALTA
            );
        }, "Debe lanzar excepción si el usuario no es COORDINADOR");
    }
    
    @Test
    @DisplayName("Debe validar usuario no nulo")
    void testUsuarioNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPriorizacion.priorizarSolicitud(
                null,
                "Justificación",
                solicitud,
                NivelPrioridad.ALTA
            );
        });
    }
    
    @Test
    @DisplayName("Debe validar solicitud no nula")
    void testSolicitudNoNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPriorizacion.priorizarSolicitud(
                usuarioCoordinador,
                "Justificación",
                null,
                NivelPrioridad.ALTA
            );
        });
    }
    
    @Test
    @DisplayName("Debe validar nivel de prioridad no nulo")
    void testNivelPrioridadNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioPriorizacion.priorizarSolicitud(
                usuarioCoordinador,
                "Justificación",
                solicitud,
                null
            );
        });
    }
    
    @Test
    @DisplayName("Debe validar que Prioridad contenga justificación no vacía")
    void testPrioridadValidaJustificacion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Prioridad(NivelPrioridad.ALTA, "");
        }, "Prioridad debe validar que la justificación no esté vacía");
    }
}
