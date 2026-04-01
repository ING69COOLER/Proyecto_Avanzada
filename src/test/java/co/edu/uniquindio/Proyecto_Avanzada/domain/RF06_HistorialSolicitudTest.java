package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.HistorialSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-06: Registro del historial auditable de la solicitud
 *
 * Métodos verificadores:
 * - "Debe crear entrada de historial al registrar solicitud"
 * - "Debe registrar fecha y hora en cada entrada de historial"
 * - "Debe registrar el tipo de acción realizada"
 * - "Debe registrar el usuario responsable de la acción"
 * - "Debe registrar observaciones de cada acción"
 * - "Debe registrar el estado en el momento de la acción"
 * - "Debe mantener historial completo con múltiples acciones"
 * - "Debe registrar acción de CLASIFICACION"
 * - "Debe registrar acción de ASIGNACION"
 * - "Debe registrar acción de CAMBIO_ESTADO (Atención)"
 * - "Debe registrar acción de CIERRE"
 * - "Debe mantener orden cronológico en historial"
 * - "Debe permitir consultar usuario mediante método obtenerUsuario"
 * - "Debe registrar historial completo de ciclo de vida"
 */
@DisplayName("RF-06: Registro del historial de solicitudes")
class RF06_HistorialSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    private Usuario usuarioDocente;
    
    @BeforeEach
    void setup() {
        usuarioCoordinador = new Usuario(1L, "Coordinador Test", "1001234567", null, true, Rol.COORDINADOR);
        
        usuarioDocente = new Usuario(2L, "Docente Test", "1001234568", null, true, Rol.DOCENTE);
        
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud para historial",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            null,
            EstadoSolicitud.REGISTRADA,
            usuarioCoordinador,
            null
        );
    }
    
    @Test
    @DisplayName("Debe crear entrada de historial al registrar solicitud")
    void testEntradaHistorialAlRegistrar() {
        // Assert
        assertNotNull(solicitud.getHistorial(), "El historial debe inicializarse");
        assertFalse(solicitud.getHistorial().isEmpty(), "Debe haber entrada inicial de creación");
        assertEquals(1, solicitud.getHistorial().size(), "Debe haber exactamente una entrada inicial");
    }
    
    @Test
    @DisplayName("Debe registrar fecha y hora en cada entrada de historial")
    void testRegistroFechaHora() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        assertNotNull(entrada.getFechaHora(), "La fecha y hora debe registrarse");
        assertTrue(
            entrada.getFechaHora().isBefore(LocalDateTime.now().plusSeconds(1)) &&
            entrada.getFechaHora().isAfter(LocalDateTime.now().minusMinutes(1)),
            "La fecha debe estar cerca del momento actual"
        );
    }
    
    @Test
    @DisplayName("Debe registrar el tipo de acción realizada")
    void testRegistroTipoAccion() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        assertNotNull(entrada.getAccion(), "El tipo de acción debe registrarse");
        assertEquals(TipoAccion.CREACION, entrada.getAccion(), "La acción inicial debe ser CREACION");
    }
    
    @Test
    @DisplayName("Debe registrar el usuario responsable de la acción")
    void testRegistroUsuarioResponsable() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        assertNotNull(entrada.getResponsable(), "El usuario responsable debe registrarse");
        assertEquals(usuarioCoordinador.getNombre(), entrada.getResponsable().getNombre(), 
                     "Debe registrar el usuario correcto");
    }
    
    @Test
    @DisplayName("Debe registrar observaciones de cada acción")
    void testRegistroObservaciones() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        assertNotNull(entrada.getObservacion(), "Las observaciones deben registrarse");
        assertEquals("Solicitud para historial", entrada.getObservacion(), 
                     "Las observaciones deben ser precisas");
    }
    
    @Test
    @DisplayName("Debe registrar el estado en el momento de la acción")
    void testRegistroEstado() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        assertNotNull(entrada.getEstado(), "El estado debe registrarse");
        assertEquals(EstadoSolicitud.REGISTRADA, entrada.getEstado(), 
                     "Debe registrar el estado en el momento de la creación");
    }
    
    @Test
    @DisplayName("Debe mantener historial completo con múltiples acciones")
    void testHistorialMultipleAcciones() throws SolicitudException {
        // Arrange
        int tamanhoInicial = solicitud.getHistorial().size();
        
        // Act - Realizar varias acciones
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        int tamanhoAlClasificar = solicitud.getHistorial().size();
        
        solicitud.asignarResponsable(usuarioDocente, "Asignada a docente");
        int tamanhoAlAsignar = solicitud.getHistorial().size();
        
        // Assert
        assertEquals(tamanhoInicial + 1, tamanhoAlClasificar, "Debe haber entrada de clasificación");
        assertEquals(tamanhoAlClasificar + 1, tamanhoAlAsignar, "Debe haber entrada de asignación");
        assertEquals(3, solicitud.getHistorial().size(), "Total de 3 entradas en historial");
    }
    
    @Test
    @DisplayName("Debe registrar acción de CLASIFICACION")
    void testAccionClasificacion() throws SolicitudException {
        // Act
        solicitud.clasificarSolicitud(TipoSolicitud.SOLICITUD_CUPOS, usuarioCoordinador, "Clasificando solicitud");
        
        // Assert
        HistorialSolicitud ultimaEntrada = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1);
        assertEquals(TipoAccion.CLASIFICADA, ultimaEntrada.getAccion(), 
                     "Debe registrar acción CLASIFICADA");
        assertEquals(EstadoSolicitud.CLASIFICADA, ultimaEntrada.getEstado(), 
                     "Debe registrar estado CLASIFICADA");
    }
    
    @Test
    @DisplayName("Debe registrar acción de ASIGNACION")
    void testAccionAsignacion() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.CANCELACION_ASIGNATURA, usuarioCoordinador, "Clasificada");
        
        // Act
        solicitud.asignarResponsable(usuarioDocente, "Asignada para atención");
        
        // Assert
        HistorialSolicitud ultimaEntrada = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1);
        assertEquals(TipoAccion.ASIGNACION, ultimaEntrada.getAccion(), 
                     "Debe registrar acción ASIGNACION");
        assertEquals(EstadoSolicitud.EN_ATENCION, ultimaEntrada.getEstado(), 
                     "Debe registrar estado EN_ATENCION");
    }
    
    @Test
    @DisplayName("Debe registrar acción de CAMBIO_ESTADO (Atención)")
    void testAccionAtencion() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.CONSULTA_ACADEMICA, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        
        // Act
        solicitud.atenderSolicitud(usuarioDocente, "Consulta resuelta");
        
        // Assert
        HistorialSolicitud ultimaEntrada = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1);
        assertEquals(TipoAccion.CAMBIO_ESTADO, ultimaEntrada.getAccion(), 
                     "Debe registrar acción CAMBIO_ESTADO");
        assertEquals(EstadoSolicitud.ATENDIDA, ultimaEntrada.getEstado(), 
                     "Debe registrar estado ATENDIDA");
    }
    
    @Test
    @DisplayName("Debe registrar acción de CIERRE")
    void testAccionCierre() throws SolicitudException {
        // Arrange
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        solicitud.asignarResponsable(usuarioDocente, "Asignada");
        solicitud.atenderSolicitud(usuarioDocente, "Atendida");
        
        // Act
        solicitud.cerrarSolicitud(usuarioCoordinador, "Solicitud cerrada exitosamente");
        
        // Assert
        HistorialSolicitud ultimaEntrada = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1);
        assertEquals(TipoAccion.CIERRE, ultimaEntrada.getAccion(), 
                     "Debe registrar acción CIERRE");
        assertEquals(EstadoSolicitud.CERRADA, ultimaEntrada.getEstado(), 
                     "Debe registrar estado CERRADA");
    }
    
    @Test
    @DisplayName("Debe mantener orden cronológico en historial")
    void testOrdenCronologicoHistorial() throws SolicitudException {
        // Arrange & Act
        solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Acción 1");
        LocalDateTime fecha1 = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1).getFechaHora();
        
        // Pequeña pausa para asegurar diferencia temporal
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignorar
        }
        
        solicitud.asignarResponsable(usuarioDocente, "Acción 2");
        LocalDateTime fecha2 = solicitud.getHistorial().get(solicitud.getHistorial().size() - 1).getFechaHora();
        
        // Assert
        assertTrue(fecha2.isAfter(fecha1), "Las fechas deben estar en orden cronológico");
    }
    
    @Test
    @DisplayName("Debe permitir consultar usuario mediante método obtenerUsuario")
    void testObtenerUsuarioDelHistorial() {
        // Assert
        HistorialSolicitud entrada = solicitud.getHistorial().get(0);
        Usuario usuarioObtenido = entrada.obtenerUsuario();
        assertNotNull(usuarioObtenido, "Debe obtener el usuario");
        assertEquals(usuarioCoordinador.getIdentificacion(), usuarioObtenido.getIdentificacion(), 
                     "Debe devolver el usuario correcto");
    }
    
    @Test
    @DisplayName("Debe registrar historial completo de ciclo de vida")
    void testHistorialCicloVidaCompleto() throws SolicitudException {
        // Act - Recorrer ciclo completo
        solicitud.clasificarSolicitud(TipoSolicitud.CANCELACION_ASIGNATURA, usuarioCoordinador, "Paso 1");
        solicitud.asignarResponsable(usuarioDocente, "Paso 2");
        solicitud.atenderSolicitud(usuarioDocente, "Paso 3");
        solicitud.cerrarSolicitud(usuarioCoordinador, "Paso 4");
        
        // Assert
        List<HistorialSolicitud> historial = solicitud.getHistorial();
        assertEquals(5, historial.size(), "Debe haber 5 entradas: creación + 4 acciones");
        
        assertEquals(TipoAccion.CREACION, historial.get(0).getAccion());
        assertEquals(TipoAccion.CLASIFICADA, historial.get(1).getAccion());
        assertEquals(TipoAccion.ASIGNACION, historial.get(2).getAccion());
        assertEquals(TipoAccion.CAMBIO_ESTADO, historial.get(3).getAccion());
        assertEquals(TipoAccion.CIERRE, historial.get(4).getAccion());
    }
}
