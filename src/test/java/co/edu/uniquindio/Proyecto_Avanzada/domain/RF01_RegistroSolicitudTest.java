package co.edu.uniquindio.Proyecto_Avanzada.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uniquindio.Proyecto_Avanzada.application.services.RegistroSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;



/**
 * RF-01: Registro de solicitudes académicas
 *
 * Métodos verificadores:
 * - "Debe registrar solicitud con todos los campos requeridos"
 * - "Debe guardar solicitud en el repositorio"
 * - "Debe validar que el tipo de solicitud no sea nulo"
 * - "Debe validar que la descripción no sea nula ni vacía"
 * - "Debe validar que el canal de origen no sea nulo"
 * - "Debe validar que la fecha y hora de registro no sea nula"
 * - "Debe validar que la identificación del solicitante no sea nula ni vacía"
 * - "Debe crear entrada en historial al registrar solicitud"
 * - "Debe permitir registrar solicitudes por diferentes canales"
 */
@DisplayName("RF-01: Registro de solicitudes académicas")
class RF01_RegistroSolicitudTest {

    private Solicitud solicitud;
    private Usuario usuarioEstudiante;
    private Usuario usuarioAdministrativo;
    private RepositorioSolicitud repositorio;
    private RegistroSolicitudesApplicationService registroService;
    private EstadoSolicitud estado;
    
    @BeforeEach
    void setup() {
       
        repositorio = RepositorioSolicitud.getInstancia();
        
        // Instanciamos el servicio de aplicacion que orquestará la lógica de negocio y persistencia
        registroService = new RegistroSolicitudesApplicationService();
        
        // Usuario estudiante autorizado para registrar
        usuarioEstudiante = new Usuario(1L, "Juan Pérez", "1001234567", "juan@gmail.com", true, Rol.ESTUDIANTE);
        
        // Usuario administrativo autorizado para registrar
        usuarioAdministrativo = new Usuario(2L, "María Gómez", "1001234568", "maria@admin.com", true, Rol.ADMINISTRATIVO);

        estado = EstadoSolicitud.REGISTRADA;
    }
    
    @Test
    @DisplayName("Debe registrar solicitud con todos los campos requeridos")
    void testRegistrarSolicitudCompleta() {
        // Arrange
        TipoSolicitud tipo = TipoSolicitud.REGISTRO_ASIGNATURA;
        String descripcion = "Solicito registrar 3 asignaturas para el próximo semestre";
        CanalOrigen canal = CanalOrigen.PORTAL_WEB;
        LocalDateTime fechaCierre = LocalDateTime.now().plusDays(10);

        // Act - El servicio de dominio orquesta la creación y validación
        registroService.registrarSolicitudCompleta(
            tipo,
            descripcion,
            canal,
            fechaCierre,
            EstadoSolicitud.REGISTRADA,
            usuarioEstudiante,
            new Prioridad(NivelPrioridad.MEDIA, "Reemplazo por motivo académico")
        );

        // Assert - Verificamos que la solicitud fue creada correctamente en el repositorio
        assertFalse(repositorio.listar().isEmpty(), "Debe existir al menos una solicitud registrada");
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertNotNull(solicitud, "La solicitud no debe ser nula");
        assertEquals(tipo, solicitud.getTipo(), "El tipo de solicitud debe ser REGISTRO_ASIGNATURA");
        assertEquals(descripcion, solicitud.getDescripcion(), "La descripción debe coincidir");
        assertEquals(canal, solicitud.getCanalOrigen(), "El canal de origen debe ser PORTAL_WEB");
        assertNotNull(solicitud.getFechaHoraRegistro(), "La fecha y hora de registro no debe ser nula");
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado(), "El estado debe ser REGISTRADA");
    }

    @Test
    @DisplayName("Debe guardar solicitud en el repositorio")
    void testGuardarSolicitudEnRepositorio() {
        // Arrange
        int contantesDe = repositorio.listar().size();
        
        // Act - El servicio de dominio guarda la solicitud en el repositorio
        registroService.registrarSolicitudBasica(usuarioEstudiante, TipoSolicitud.HOMOLOGACION, "Solicito homologación de asignaturas cursadas en universidad externa", CanalOrigen.CORREO_CERTIFICADO);
        
        // Assert - Las verificaciones bajan desde el servicio hasta el agregado
        int contadorDespues = repositorio.listar().size();
        assertEquals(contantesDe + 1, contadorDespues, "El repositorio debe contener una solicitud más");
        
        // Verificamos que el agregado (Solicitud) tiene los datos correctos
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertNotNull(solicitud, "La solicitud no debe ser nula");
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo(), "El tipo debe ser HOMOLOGACION");
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado(), "El estado debe ser REGISTRADA");
    }
    
    @Test
    @DisplayName("Debe validar que el tipo de solicitud no sea nulo")
    void testTipoSolicitudNoNulo() {
        // Act & Assert
        // El servicio de dominio valida junto al agregado que el tipo no sea nulo
        assertThrows(IllegalArgumentException.class, () -> {
            registroService.registrarSolicitudBasica(
                usuarioEstudiante,
                null,  // tipo nulo
                "descripción",
                CanalOrigen.CSU
            );
        }, "Debe lanzar excepción si el tipo es nulo");
    }
    
    @Test
    @DisplayName("Debe validar que la descripción no sea nula ni vacía")
    void testDescripcionNoNulaVacia() {
        // Act & Assert
        // El agregado valida que la descripción no esté vacía
        assertThrows(IllegalArgumentException.class, () -> {
            registroService.registrarSolicitudBasica(
                usuarioEstudiante,
                TipoSolicitud.CANCELACION_ASIGNATURA,
                "",  // descripción vacía
                CanalOrigen.SAC
            );
        }, "Debe lanzar excepción si la descripción está vacía");
        
        // Test descripción nula
        assertThrows(IllegalArgumentException.class, () -> {
            registroService.registrarSolicitudBasica(
                usuarioEstudiante,
                TipoSolicitud.CANCELACION_ASIGNATURA,
                null,  // descripción nula
                CanalOrigen.SAC
            );
        }, "Debe lanzar excepción si la descripción es nula");
    }
    
    @Test
    @DisplayName("Debe validar que el canal de origen no sea nulo")
    void testCanalOrigenNoNulo() {
        // Act & Assert
        // El agregado valida que el canal no sea nulo
        assertThrows(IllegalArgumentException.class, () -> {
            registroService.registrarSolicitudBasica(
                usuarioEstudiante,
                TipoSolicitud.SOLICITUD_CUPOS,
                "descripción",
                null  // canal nulo
            );
        }, "Debe lanzar excepción si el canal es nulo");
    }
    
    @Test
    @DisplayName("Debe validar que la fecha y hora de registro no sea nula")
    void testFechaHoraRegistroNoNula() {
        // Act
        registroService.registrarSolicitudBasica(
            usuarioEstudiante,
            TipoSolicitud.CONSULTA_ACADEMICA,
            "descripción",
            CanalOrigen.TELEFONO
        );

        // Assert
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertNotNull(solicitud.getFechaHoraRegistro(), "La fecha y hora de registro no debe ser nula");
    }
    
    @Test
    @DisplayName("Debe validar que la identificación del solicitante no sea nula ni vacía")
    void testIdentificacionNoNulaVacia() {
        // Act & Assert
        // El constructor de Usuario valida que la identificación no sea nula ni vacía
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(3L, "Usuario Inválido", "", "invalido@email.com", true, Rol.ESTUDIANTE);
        }, "Debe lanzar excepción si la identificación del usuario es nula o vacía");
    }
    
    @Test
    @DisplayName("Debe crear entrada en historial al registrar solicitud")
    void testHistorialInicial() {
        // Act - El servicio de dominio registra la solicitud
        registroService.registrarSolicitudBasica(usuarioEstudiante, TipoSolicitud.HOMOLOGACION, "Solicito homologación", CanalOrigen.CSU);
        
        // Assert - Verificamos que el agregado (Solicitud) tiene un historial
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertNotNull(solicitud.getHistorial(), "El historial no debe ser nulo");
        assertFalse(solicitud.getHistorial().isEmpty(), "El historial debe contener la entrada inicial");
        assertEquals(1, solicitud.getHistorial().size(), "Debe haber una entrada en el historial");
    }
    
    @Test
    @DisplayName("Debe permitir registrar solicitudes por diferentes canales")
    void testDiferentesCanalesOrigen() {
        // Test CSU - El servicio de dominio registra a través del agregado
        registroService.registrarSolicitudBasica(usuarioEstudiante, TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.CSU);
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertEquals(CanalOrigen.CSU, solicitud.getCanalOrigen(), "El canal debe ser CSU");
        
        // Test EMAIL
        registroService.registrarSolicitudBasica(usuarioEstudiante, TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.EMAIL);
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertEquals(CanalOrigen.EMAIL, solicitud.getCanalOrigen(), "El canal debe ser EMAIL");
        
        // Test PRESENCIAL
        registroService.registrarSolicitudBasica(usuarioEstudiante, TipoSolicitud.REGISTRO_ASIGNATURA, "desc", CanalOrigen.PRESENCIAL);
        solicitud = repositorio.listar().get(repositorio.listar().size() - 1);
        assertEquals(CanalOrigen.PRESENCIAL, solicitud.getCanalOrigen(), "El canal debe ser PRESENCIAL");
    }
}
