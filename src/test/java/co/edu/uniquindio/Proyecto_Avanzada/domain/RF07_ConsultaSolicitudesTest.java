package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
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
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-07 – Consulta de solicitudes
 *
 * Verifica que el sistema permita consultar solicitudes según diferentes criterios:
 * - Estado
 * - Tipo de solicitud
 * - Prioridad
 * - Responsable asignado
 */
@DisplayName("RF-07: Consulta de solicitudes")
class RF07_ConsultaSolicitudesTest {

    private RepositorioSolicitud repositorio;
    private ConsultaSolicitudesService servicioConsulta;
    private Usuario usuarioCoordinador;
    private Usuario usuarioDocente;
    private Usuario usuarioDocente2;
    
    @BeforeEach
    void setup() throws Exception {
        repositorio = RepositorioSolicitud.getInstancia();
        servicioConsulta = new ConsultaSolicitudesService();
        
        // Inyectar el repositorio usando reflexión
        Field field = ConsultaSolicitudesService.class.getDeclaredField("repositorioSolicitud");
        field.setAccessible(true);
        field.set(servicioConsulta, repositorio);
        
        usuarioCoordinador = new Usuario();
        usuarioCoordinador.setId(1L);
        usuarioCoordinador.setNombre("Coordinador Consultas");
        usuarioCoordinador.setIdentificacion("1001234567");
        usuarioCoordinador.setActivo(true);
        usuarioCoordinador.setRol(Rol.COORDINADOR);
        
        usuarioDocente = new Usuario();
        usuarioDocente.setId(2L);
        usuarioDocente.setNombre("Docente Uno");
        usuarioDocente.setIdentificacion("1001234568");
        usuarioDocente.setActivo(true);
        usuarioDocente.setRol(Rol.DOCENTE);
        
        usuarioDocente2 = new Usuario();
        usuarioDocente2.setId(3L);
        usuarioDocente2.setNombre("Docente Dos");
        usuarioDocente2.setIdentificacion("1001234569");
        usuarioDocente2.setActivo(true);
        usuarioDocente2.setRol(Rol.DOCENTE);
        
        // Limpiar el repositorio
        repositorio.limpiar();
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por estado REGISTRADA")
    void testConsultarPorEstadoRegistrada() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "123", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.HOMOLOGACION, "desc2", CanalOrigen.EMAIL, LocalDateTime.now(), "124", null, null, usuarioCoordinador, null);
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorEstado(EstadoSolicitud.REGISTRADA);
        
        // Assert
        assertEquals(2, resultado.size(), "Debe encontrar 2 solicitudes en estado REGISTRADA");
        assertTrue(resultado.stream().allMatch(s -> s.getEstado() == EstadoSolicitud.REGISTRADA));
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por estado CLASIFICADA")
    void testConsultarPorEstadoClasificada() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "123", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.HOMOLOGACION, "desc2", CanalOrigen.EMAIL, LocalDateTime.now(), "124", null, null, usuarioCoordinador, null);
        
        sol1.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorEstado(EstadoSolicitud.CLASIFICADA);
        
        // Assert
        assertEquals(1, resultado.size(), "Debe encontrar 1 solicitud clasificada");
        assertEquals(TipoSolicitud.REGISTRO_ASIGNATURA, resultado.get(0).getTipo());
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por tipo REGISTRO_ASIGNATURA")
    void testConsultarPorTipoRegistroAsignatura() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "Registro 1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "123", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "Registro 2", CanalOrigen.CSU, LocalDateTime.now(), "124", null, null, usuarioCoordinador, null);
        Solicitud sol3 = new Solicitud(TipoSolicitud.HOMOLOGACION, "Homologación", CanalOrigen.EMAIL, LocalDateTime.now(), "125", null, null, usuarioCoordinador, null);
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        repositorio.guardarSolicitud(sol3);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorTipo(TipoSolicitud.REGISTRO_ASIGNATURA);
        
        // Assert
        assertEquals(2, resultado.size(), "Debe encontrar 2 solicitudes de tipo REGISTRO_ASIGNATURA");
        assertTrue(resultado.stream().allMatch(s -> s.getTipo() == TipoSolicitud.REGISTRO_ASIGNATURA));
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por tipo HOMOLOGACION")
    void testConsultarPorTipoHomologacion() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.HOMOLOGACION, "Homo 1", CanalOrigen.CORREO_CERTIFICADO, LocalDateTime.now(), "126", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.CANCELACION_ASIGNATURA, "Cancelación", CanalOrigen.PRESENCIAL, LocalDateTime.now(), "127", null, null, usuarioCoordinador, null);
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorTipo(TipoSolicitud.HOMOLOGACION);
        
        // Assert
        assertEquals(1, resultado.size());
        assertEquals(TipoSolicitud.HOMOLOGACION, resultado.get(0).getTipo());
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por prioridad ALTA")
    void testConsultarPorPrioridadAlta() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.CANCELACION_ASIGNATURA, "desc", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "128", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.SOLICITUD_CUPOS, "desc", CanalOrigen.EMAIL, LocalDateTime.now(), "129", null, null, usuarioCoordinador, null);
        
        Prioridad prioridadAlta = new Prioridad(NivelPrioridad.ALTA, "Urgente");
        Prioridad prioridadBaja = new Prioridad(NivelPrioridad.BAJA, "Normal");
        
        sol1.setPrioridad(prioridadAlta);
        sol2.setPrioridad(prioridadBaja);
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorPrioridad(prioridadAlta);
        
        // Assert
        assertEquals(1, resultado.size());
        assertEquals(NivelPrioridad.ALTA, resultado.get(0).getPrioridad().getNivel());
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes por responsable asignado")
    void testConsultarPorResponsable() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "130", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.HOMOLOGACION, "desc2", CanalOrigen.EMAIL, LocalDateTime.now(), "131", null, null, usuarioCoordinador, null);
        
        sol1.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        sol1.asignarResponsable(usuarioDocente, "Asignada a docente 1");
        
        sol2.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, usuarioCoordinador, "Clasificada");
        sol2.asignarResponsable(usuarioDocente2, "Asignada a docente 2");
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorResponsable(usuarioDocente);
        
        // Assert
        assertFalse(resultado.isEmpty(), "Debe encontrar solicitudes del responsable");
        assertTrue(resultado.stream().anyMatch(s -> s.getHistorial().stream()
                .anyMatch(h -> h.getResponsable().equals(usuarioDocente))));
    }
    
    @Test
    @DisplayName("Debe validar que el estado no sea nulo en consulta")
    void testValidarEstadoNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioConsulta.consultarPorEstado(null);
        });
    }
    
    @Test
    @DisplayName("Debe validar que el tipo no sea nulo en consulta")
    void testValidarTipoNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioConsulta.consultarPorTipo(null);
        });
    }
    
    @Test
    @DisplayName("Debe validar que la prioridad no sea nula en consulta")
    void testValidarPrioridadNoNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioConsulta.consultarPorPrioridad(null);
        });
    }
    
    @Test
    @DisplayName("Debe validar que el responsable no sea nulo en consulta")
    void testValidarResponsableNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioConsulta.consultarPorResponsable(null);
        });
    }
    
    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay coincidencias")
    void testConsultaSinResultados() {
        // Arrange
        Solicitud sol = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "132", null, null, usuarioCoordinador, null);
        repositorio.guardarSolicitud(sol);
        
        // Act
        List<Solicitud> resultado = servicioConsulta.consultarPorEstado(EstadoSolicitud.CERRADA);
        
        // Assert
        assertTrue(resultado.isEmpty(), "Debe retornar lista vacía cuando no hay resultados");
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes pendientes en estado REGISTRADA")
    void testConsultarSolicitudesPendientes() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.HOMOLOGACION, "desc1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "133", null, null, usuarioCoordinador, null);
        Solicitud sol2 = new Solicitud(TipoSolicitud.HOMOLOGACION, "desc2", CanalOrigen.EMAIL, LocalDateTime.now(), "134", null, null, usuarioCoordinador, null);
        
        sol1.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, usuarioCoordinador, "Clasificada");
        
        repositorio.guardarSolicitud(sol1);
        repositorio.guardarSolicitud(sol2);
        
        // Act
        List<Solicitud> pendientes = servicioConsulta.consultarSolicitudesPendientes();
        
        // Assert
        assertEquals(1, pendientes.size(), "Solo una solicitud debe estar registrada sin clasificar");
        assertEquals(EstadoSolicitud.REGISTRADA, pendientes.get(0).getEstado());
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes en atención")
    void testConsultarSolicitudesEnAtencion() {
        // Arrange
        Solicitud sol1 = new Solicitud(TipoSolicitud.CANCELACION_ASIGNATURA, "desc1", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "135", null, null, usuarioCoordinador, null);
        sol1.clasificarSolicitud(TipoSolicitud.CANCELACION_ASIGNATURA, usuarioCoordinador, "Clasificada");
        sol1.asignarResponsable(usuarioDocente, "En atención");
        
        repositorio.guardarSolicitud(sol1);
        
        // Act
        List<Solicitud> enAtencion = servicioConsulta.consultarSolicitudesEnAtencion();
        
        // Assert
        assertEquals(1, enAtencion.size());
        assertEquals(EstadoSolicitud.EN_ATENCION, enAtencion.get(0).getEstado());
    }
    
    @Test
    @DisplayName("Debe consultar solicitudes cerradas")
    void testConsultarSolicitudesCerradas() throws SolicitudException {
        // Arrange
        Solicitud sol = new Solicitud(TipoSolicitud.SOLICITUD_CUPOS, "desc", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), "136", null, null, usuarioCoordinador, null);
        sol.clasificarSolicitud(TipoSolicitud.SOLICITUD_CUPOS, usuarioCoordinador, "Clasificada");
        sol.asignarResponsable(usuarioDocente, "Asignada");
        sol.atenderSolicitud(usuarioDocente, "Atendida");
        sol.cerrarSolicitud(usuarioCoordinador, "Cerrada");
        
        repositorio.guardarSolicitud(sol);
        
        // Act
        List<Solicitud> cerradas = servicioConsulta.consultarSolicitudesCerradas();
        
        // Assert
        assertEquals(1, cerradas.size());
        assertEquals(EstadoSolicitud.CERRADA, cerradas.get(0).getEstado());
    }
    
    @Test
    @DisplayName("Debe validar permisos del usuario para consultar")
    void testValidarPermisosConsulta() {
        // Arrange
        Usuario usuarioInvalido = new Usuario();
        usuarioInvalido.setId(99L);
        usuarioInvalido.setActivo(true);
        usuarioInvalido.setRol(Rol.ESTUDIANTE);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioConsulta.consultarPorResponsable(usuarioInvalido);
        }, "Solo usuarios con rol COORDINADOR o DOCENTE deben poder consultar");
    }
}
