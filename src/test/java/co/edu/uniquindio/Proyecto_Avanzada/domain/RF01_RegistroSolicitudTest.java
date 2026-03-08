package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;


import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;



/**
 * RF-01 – Registro de solicitudes académicas
 *
 * Verifica que el sistema permita registrar una solicitud almacenando:
 * - Tipo de solicitud
 * - Descripción de la solicitud
 * - Canal de origen (CSU, correo, SAC, telefónico, etc.)
 * - Fecha y hora de registro
 * - Identificación del solicitante
 */
@DisplayName("RF-01: Registro de solicitudes académicas")
class RF01_RegistroSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioEstudiante;
    private Usuario usuarioAdministrativo;
    private RepositorioSolicitud repositorio;
    private EstadoSolicitud estado;
    
    @BeforeEach
    void setup() {
        repositorio = RepositorioSolicitud.getInstancia();
        
        // Usuario estudiante autorizado para registrar
        usuarioEstudiante = new Usuario();
        usuarioEstudiante.setId(1L);
        usuarioEstudiante.setNombre("Juan Pérez");
        usuarioEstudiante.setIdentificacion("1001234567");
        usuarioEstudiante.setCorreo("juan@gmail.com");
        usuarioEstudiante.setActivo(true);
        usuarioEstudiante.setRol(Rol.ESTUDIANTE);
        
        // Usuario administrativo autorizado para registrar
        usuarioAdministrativo = new Usuario();
        usuarioAdministrativo.setId(2L);
        usuarioAdministrativo.setNombre("María Gómez");
        usuarioAdministrativo.setIdentificacion("1001234568");
        usuarioAdministrativo.setCorreo("maria@admin.com");
        usuarioAdministrativo.setActivo(true);
        usuarioAdministrativo.setRol(Rol.ADMINISTRATIVO);

        estado = EstadoSolicitud.REGISTRADA;
    }
    
    @Test
    @DisplayName("Debe registrar solicitud con todos los campos requeridos")
    void testRegistrarSolicitudCompleta() {
        // Arrange
        TipoSolicitud tipo = TipoSolicitud.REGISTRO_ASIGNATURA;
        String descripcion = "Solicito registrar 3 asignaturas para el próximo semestre";
        CanalOrigen canal = CanalOrigen.PORTAL_WEB;
        LocalDateTime fecha = LocalDateTime.now();
        String identificacion = "1001234567";
        
        // Act
        solicitud = new Solicitud(tipo, descripcion, canal, fecha, identificacion, null, null, usuarioEstudiante, null);
        
        // Assert
        assertNotNull(solicitud, "La solicitud no debe ser nula");
        assertEquals(tipo, solicitud.getTipo(), "El tipo de solicitud debe ser REGISTRO_ASIGNATURA");
        assertEquals(descripcion, solicitud.getDescripcion(), "La descripción debe coincidir");
        assertEquals(canal, solicitud.getCanalOrigen(), "El canal de origen debe ser PORTAL_WEB");
        assertNotNull(solicitud.getFechaHoraRegistro(), "La fecha y hora de registro no debe ser nula");
        assertEquals(identificacion, solicitud.getIdentificacionSolicitante(), "La identificación debe coincidir");
    }
    
    @Test
    @DisplayName("Debe guardar solicitud en el repositorio")
    void testGuardarSolicitudEnRepositorio() {
        // Arrange
        solicitud = new Solicitud(
            TipoSolicitud.HOMOLOGACION,
            "Solicito homologación de asignaturas cursadas en universidad externa",
            CanalOrigen.CORREO_CERTIFICADO,
            LocalDateTime.now(),
            "1001234567",
            null, null, usuarioEstudiante, null
        );
        int countantesDe = repositorio.listar().size();
        
        // Act
        repositorio.guardarSolicitud(solicitud);
        
        // Assert
        int contadorDespues = repositorio.listar().size();
        assertEquals(countantesDe + 1, contadorDespues, "El repositorio debe contener una solicitud más");
    }
    
    @Test
    @DisplayName("Debe validar que el tipo de solicitud no sea nulo")
    void testTipoSolicitudNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(null, "descripción", CanalOrigen.CSU, LocalDateTime.now(), "123", null, null, null, null);
        }, "Debe lanzar excepción si el tipo es nulo");
    }
    
    @Test
    @DisplayName("Debe validar que la descripción no sea nula ni vacía")
    void testDescripcionNoNulaVacia() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.CANCELACION_ASIGNATURA, "", CanalOrigen.SAC, LocalDateTime.now(), "123", null, null, null, null);
        }, "Debe lanzar excepción si la descripción está vacía");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.CANCELACION_ASIGNATURA, null, CanalOrigen.SAC, LocalDateTime.now(), "123", null, null, null, null);
        }, "Debe lanzar excepción si la descripción es nula");
    }
    
    @Test
    @DisplayName("Debe validar que el canal de origen no sea nulo")
    void testCanalOrigenNoNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.SOLICITUD_CUPOS, "descripción", null, LocalDateTime.now(), "123", null, null, null, null);
        }, "Debe lanzar excepción si el canal es nulo");
    }
    
    @Test
    @DisplayName("Debe validar que la fecha y hora de registro no sea nula")
    void testFechaHoraRegistroNoNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.CONSULTA_ACADEMICA, "descripción", CanalOrigen.TELEFONO, null, "123", null, null, null, null);
        }, "Debe lanzar excepción si la fecha es nula");
    }
    
    @Test
    @DisplayName("Debe validar que la identificación del solicitante no sea nula ni vacía")
    void testIdentificacionNoNulaVacia() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "descripción", CanalOrigen.EMAIL, LocalDateTime.now(), "", null, null, null, null);
        }, "Debe lanzar excepción si la identificación está vacía");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "descripción", CanalOrigen.EMAIL, LocalDateTime.now(), null, null, null, null, null);
        }, "Debe lanzar excepción si la identificación es nula");
    }
    
    @Test
    @DisplayName("Debe crear entrada en historial al registrar solicitud")
    void testHistorialInicial() {
        // Act
        solicitud = new Solicitud(
            TipoSolicitud.HOMOLOGACION,
            "Solicito homologación",
            CanalOrigen.CSU,
            LocalDateTime.now(),
            "1001234567",
            null, null, usuarioEstudiante, null
        );
        
        // Assert
        assertNotNull(solicitud.getHistorial(), "El historial no debe ser nulo");
        assertFalse(solicitud.getHistorial().isEmpty(), "El historial debe contener la entrada inicial");
        assertEquals(1, solicitud.getHistorial().size(), "Debe haber una entrada en el historial");
    }
    
    @Test
    @DisplayName("Debe permitir registrar solicitudes por diferentes canales")
    void testDiferentesCanalesOrigen() {
        // Test CSU
        solicitud = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.CSU, LocalDateTime.now(), "123", null, estado, usuarioEstudiante, null);
        assertEquals(CanalOrigen.CSU, solicitud.getCanalOrigen());
        
        // Test EMAIL
        solicitud = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.EMAIL, LocalDateTime.now(), "123", null, estado, usuarioEstudiante, null);
        assertEquals(CanalOrigen.EMAIL, solicitud.getCanalOrigen());
        
        // Test PRESENCIAL
        solicitud = new Solicitud(TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.PRESENCIAL, LocalDateTime.now(), "123", null, estado, usuarioEstudiante, null);
        assertEquals(CanalOrigen.PRESENCIAL, solicitud.getCanalOrigen());
    }
}
