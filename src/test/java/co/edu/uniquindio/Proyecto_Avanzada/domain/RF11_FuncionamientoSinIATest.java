package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RF-11: Funcionamiento independiente de IA
 *
 * Métodos verificadores:
 * - "RF-11: Debe generar resumen básico sin IA disponible"
 * - "RF-11: El resumen fallback debe incluir el estado de la solicitud"
 * - "RF-11: El resumen fallback debe incluir el solicitante"
 * - "RF-11: Sugerencia de clasificación funciona sin IA (fallback por palabras clave)"
 * - "RF-11: No debe lanzar excepción cuando la IA no está configurada"
 * - "RF-11: La solicitud nula debe lanzar excepción controlada, no un NullPointerException"
 * - "RF-11: El sistema genera resumen para solicitud sin ID (no persistida)"
 */
@DisplayName("RF-11: Funcionamiento independiente de IA")
class RF11_FuncionamientoSinIATest {

    @InjectMocks
    private ResumenSolicitudService resumenService;

    @Mock
    private IRepositorioSolicitud repositorioSolicitud;

    private Usuario coordinador;
    private Solicitud solicitud;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // NO se inyecta modelo de IA — simula que no esta configurado
        ReflectionTestUtils.setField(resumenService, "repositorioSolicitud", repositorioSolicitud);
        ReflectionTestUtils.setField(resumenService, "modeloLenguaje", null);

        coordinador = new Usuario(1L, "Coordinador Test", "1001234567", null, true, Rol.COORDINADOR);

        solicitud = new Solicitud(
                TipoSolicitud.REGISTRO_ASIGNATURA,
                "Solicitud de inscripcion en Programacion Avanzada",
                CanalOrigen.PORTAL_WEB,
                LocalDateTime.now(),
                null,
                EstadoSolicitud.REGISTRADA,
                coordinador,
                null);
        solicitud.setId(1L);
    }

    @Test
    @DisplayName("RF-11: Debe generar resumen basico sin IA disponible")
    void testResumenSinIA() throws SolicitudException {
        // Arrange
        when(repositorioSolicitud.obtenerPorId(1L)).thenReturn(Optional.of(solicitud));

        // Act
        String resumen = resumenService.generarResumenSolicitud(solicitud);

        // Assert
        assertNotNull(resumen, "El resumen no debe ser nulo aunque no haya IA");
        assertFalse(resumen.isBlank(), "El resumen no debe estar vacio");
        // El fallback genera un resumen con los datos de la solicitud
        assertTrue(resumen.contains("REGISTRO_ASIGNATURA") || resumen.contains("Tipo"),
                "El resumen basico debe incluir informacion del tipo de solicitud");
    }

    @Test
    @DisplayName("RF-11: El resumen fallback debe incluir el estado de la solicitud")
    void testResumenFallbackIncluyeEstado() throws SolicitudException {
        // Arrange
        when(repositorioSolicitud.obtenerPorId(1L)).thenReturn(Optional.of(solicitud));

        // Act
        String resumen = resumenService.generarResumenSolicitud(solicitud);

        // Assert
        assertNotNull(resumen);
        assertTrue(resumen.contains("REGISTRADA") || resumen.contains("Estado"),
                "El resumen debe mostrar el estado de la solicitud");
    }

    @Test
    @DisplayName("RF-11: El resumen fallback debe incluir el solicitante")
    void testResumenFallbackIncluyeSolicitante() throws SolicitudException {
        // Arrange
        when(repositorioSolicitud.obtenerPorId(1L)).thenReturn(Optional.of(solicitud));

        // Act
        String resumen = resumenService.generarResumenSolicitud(solicitud);

        // Assert
        assertTrue(resumen.contains("1001234567") || resumen.contains("Solicitante"),
                "El resumen debe incluir la identificacion del solicitante");
    }

    @Test
    @DisplayName("RF-11: Sugerencia de clasificacion funciona sin IA (fallback por palabras clave)")
    void testSugerenciaClasificacionSinIA() {
        // Act — no hay modelo de IA configurado
        String resultado = resumenService.sugerirClasificacion(
                "Necesito cancelar mi materia de calculo");

        // Assert
        assertNotNull(resultado, "El sistema debe responder aun sin IA");
        assertFalse(resultado.isBlank(), "La respuesta no debe estar vacia");
    }

    @Test
    @DisplayName("RF-11: No debe lanzar excepcion cuando la IA no esta configurada")
    void testNoExcepcionSinIA() {
        // Act & Assert — no debe explotar aunque no haya IA
        assertDoesNotThrow(() -> {
            resumenService.sugerirClasificacion("cualquier descripcion de solicitud");
        }, "El sistema no debe lanzar excepcion cuando la IA no esta disponible");
    }

    @Test
    @DisplayName("RF-11: La solicitud nula debe lanzar excepcion controlada, no un NullPointerException")
    void testSolicitudNulaLanzaExcepcionControlada() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> resumenService.generarResumenSolicitud(null),
                "Debe lanzar IllegalArgumentException, no NullPointerException");
    }

    @Test
    @DisplayName("RF-11: El sistema genera resumen para solicitud sin ID (no persistida)")
    void testResumenSolicitudSinId() throws SolicitudException {
        // Arrange — solicitud sin ID (no persistida en repo)
        Solicitud sinId = new Solicitud(
                TipoSolicitud.HOMOLOGACION,
                "Solicitud de homologacion de materia externa",
                CanalOrigen.CSU,
                LocalDateTime.now(),
                null,
                EstadoSolicitud.REGISTRADA,
                coordinador,
                null);
        // id es null, no se consulta al repo

        // Act
        String resumen = resumenService.generarResumenSolicitud(sinId);

        // Assert
        assertNotNull(resumen);
        // No se debe llamar al repositorio cuando no hay ID
        verify(repositorioSolicitud, never()).obtenerPorId(any());
    }
}
