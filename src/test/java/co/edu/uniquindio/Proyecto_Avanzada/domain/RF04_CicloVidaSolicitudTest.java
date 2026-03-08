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
 * RF-04 – Gestión del ciclo de vida de la solicitud
 *
 * Verifica que el sistema gestione el ciclo de vida de una solicitud, permitiendo:
 * - Estados: REGISTRADA, CLASIFICADA, EN_ATENCION, ATENDIDA, CERRADA
 * - Validar que las transiciones entre estados sean coherentes
 */
@DisplayName("RF-04: Gestión del ciclo de vida de solicitudes")
class RF04_CicloVidaSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioDocente;
    
    @BeforeEach
    void setup() {
        // Coordinador
        usuarioCoordinador = new Usuario();
        usuarioCoordinador.setId(1L);
        usuarioCoordinador.setNombre("Coordinador Test");
        usuarioCoordinador.setIdentificacion("1001234567");
        usuarioCoordinador.setActivo(true);
        usuarioCoordinador.setRol(Rol.COORDINADOR);
        
        // Docente
        usuarioDocente = new Usuario();
        usuarioDocente.setId(2L);
        usuarioDocente.setNombre("Docente Test");
        usuarioDocente.setIdentificacion("1001234568");
        usuarioDocente.setActivo(true);
        usuarioDocente.setRol(Rol.DOCENTE);
        
        // Crear solicitud en estado REGISTRADA
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud test",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            "1001234567",
            null, null, usuarioCoordinador, null
        );
    }
    
    @Test
    @DisplayName("Debe crear solicitud en estado REGISTRADA")
    void testEstadoInicial() {
        // Assert
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado(), 
                     "Una nueva solicitud debe estar en estado REGISTRADA");
    }
    
    @Test
    @DisplayName("Debe transitar de REGISTRADA a CLASIFICADA")
    void testTransicionRegistradaAClasificada() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificando solicitud");
        
        // Assert
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado(), 
                     "Debe transitar a estado CLASIFICADA");
    }
    
    @Test
    @DisplayName("Debe transitar de CLASIFICADA a EN_ATENCION")
    void testTransicionClasificadaAEnAtencion() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        
        // Act
        solicitud.asignarResponsable(usuarioDocente, "Asignando a docente responsable");
        
        // Assert
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado(), 
                     "Debe transitar a estado EN_ATENCION");
    }
    
    @Test
    @DisplayName("Debe transitar de EN_ATENCION a ATENDIDA")
    void testTransicionEnAtencionAAtendida() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        
        // Act
        solicitud.atenderSolicitud(usuarioDocente, "Solicitud atendida satisfactoriamente");
        
        // Assert
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado(), 
                     "Debe transitar a estado ATENDIDA");
    }
    
    @Test
    @DisplayName("Debe transitar de ATENDIDA a CERRADA")
    void testTransicionAtendidaACerrada() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act
        solicitud.cerrarSolicitud(usuarioCoordinador, "Solicitud cerrada correctamente");
        
        // Assert
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado(), 
                     "Debe transitar a estado CERRADA");
    }
    
    @Test
    @DisplayName("Debe permitir ciclo completo: REGISTRADA -> CLASIFICADA -> EN_ATENCION -> ATENDIDA -> CERRADA")
    void testCicloCompletoExitoso() throws SolicitudException {
        // Arrange
        assertSame(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
        
        // Act 1: Clasificar
        solicitud.clasificarSolicitud(TipoSolicitud.CANCELACION_ASIGNATURA, usuarioCoordinador, "Clasificación");
        
        // Assert 1
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
        
        // Act 2: Asignar
        solicitud.asignarResponsable(usuarioDocente, "Asignación");
        
        // Assert 2
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
        
        // Act 3: Atender
        solicitud.atenderSolicitud(usuarioDocente, "Atención");
        
        // Assert 3
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
        
        // Act 4: Cerrar
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cierre");
        
        // Assert 4
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }
    
    @Test
    @DisplayName("Debe registrar fecha de cierre cuando se cierra solicitud")
    void testRegistroFechaCierre() throws SolicitudException {
        // Arrange
        assertNull(solicitud.getFechaCierre(), "La fecha de cierre debe ser nula inicialmente");
        
        // Completar ciclo
        solicitud.clasificarSolicitud(TipoSolicitud.CONSULTA_ACADEMICA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act
        LocalDateTime fechaAntesDeCerrar = LocalDateTime.now();
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cerrada");
        LocalDateTime fechaDespuesDeCerrar = LocalDateTime.now();
        
        // Assert
        assertNotNull(solicitud.getFechaCierre(), "La fecha de cierre debe ser registrada");
        assertTrue(
            solicitud.getFechaCierre().isAfter(fechaAntesDeCerrar.minusSeconds(1)) &&
            solicitud.getFechaCierre().isBefore(fechaDespuesDeCerrar.plusSeconds(1)),
            "La fecha de cierre debe estar cerca del momento actual"
        );
    }
    
    @Test
    @DisplayName("Debe contar entregas en historial que reflejen transiciones de estado")
    void testTransicionesRegistradasEnHistorial() throws SolicitudException {
        // Arrange
        int tamanhoHistorialInicial = solicitud.getHistorial().size();
        
        // Act - Realizar transiciones
        solicitud.clasificarSolicitud(TipoSolicitud.SOLICITUD_CUPOS, usuarioCoordinador, "Clasificada");
        int tamanhoAlClasificar = solicitud.getHistorial().size();
        
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        int tamanhoAlAsignar = solicitud.getHistorial().size();
        
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        int tamanhoAlAtender = solicitud.getHistorial().size();
        
        solicitud.cerrarSolicitud(usuarioCoordinador, "Cerrada");
        int tamanhoAlCerrar = solicitud.getHistorial().size();
        
        // Assert
        assertTrue(tamanhoAlClasificar > tamanhoHistorialInicial);
        assertTrue(tamanhoAlAsignar > tamanhoAlClasificar);
        assertTrue(tamanhoAlAtender > tamanhoAlAsignar);
        assertTrue(tamanhoAlCerrar > tamanhoAlAtender);
    }
}
