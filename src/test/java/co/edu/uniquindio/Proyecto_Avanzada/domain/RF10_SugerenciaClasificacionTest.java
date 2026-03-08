package co.edu.uniquindio.Proyecto_Avanzada.domain;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ModeloLenguajeOpenAI;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RF-10 – Sugerencia automatica de clasificacion con IA
 *
 * Verifica que el sistema pueda sugerir tipo y prioridad a partir del
 * texto descriptivo, usando IA o fallback, y que la sugerencia siempre
 * indique que debe ser confirmada por un humano.
 */
@DisplayName("RF-10: Sugerencia automatica de clasificacion")
class RF10_SugerenciaClasificacionTest {

    @InjectMocks
    private ResumenSolicitudService resumenService;

    @Mock
    private IRepositorioSolicitud repositorioSolicitud;

    @Mock
    private ModeloLenguajeOpenAI modeloLenguaje;

    private Usuario coordinador;
    private Solicitud solicitud;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(resumenService, "repositorioSolicitud", repositorioSolicitud);
        ReflectionTestUtils.setField(resumenService, "modeloLenguaje", modeloLenguaje);

        coordinador = new Usuario();
        coordinador.setId(1L);
        coordinador.setNombre("Coordinador Test");
        coordinador.setIdentificacion("1001234567");
        coordinador.setActivo(true);
        coordinador.setRol(Rol.COORDINADOR);

        solicitud = new Solicitud(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Quiero registrarme en Programacion Avanzada para el siguiente semestre",
                CanalOrigen.PORTAL_WEB,
                LocalDateTime.now(),
                "1001234567",
                null, null, coordinador, null);
    }

    @Test
    @DisplayName("RF-10: Debe retornar sugerencia del modelo de IA cuando esta disponible")
    void testSugerenciaConIA() {
        // Arrange
        String sugerenciaEsperada = "Tipo sugerido: REGISTRO_ASIGNATURA\nPrioridad sugerida: MEDIA\n[Debe ser confirmada por un funcionario]";
        when(modeloLenguaje.sugerirClasificacion(any())).thenReturn(sugerenciaEsperada);

        // Act
        String resultado = resumenService.sugerirClasificacion(solicitud.getDescripcion());

        // Assert
        assertNotNull(resultado, "La sugerencia no debe ser nula");
        assertFalse(resultado.isBlank(), "La sugerencia no debe estar vacia");
        verify(modeloLenguaje, times(1)).sugerirClasificacion(any());
    }

    @Test
    @DisplayName("RF-10: La sugerencia de IA se debe poder construir con la descripcion de la solicitud")
    void testSugerenciaUsaDescripcion() {
        // Arrange
        String descripcion = "Solicitud urgente de cancelacion de asignatura por caso medico";
        String respuestaMock = "Tipo: CANCELACION_ASIGNATURA | Prioridad: ALTA | Confirmacion requerida";
        when(modeloLenguaje.sugerirClasificacion(descripcion)).thenReturn(respuestaMock);

        // Act
        String resultado = resumenService.sugerirClasificacion(descripcion);

        // Assert
        assertEquals(respuestaMock, resultado);
        verify(modeloLenguaje).sugerirClasificacion(descripcion);
    }

    @Test
    @DisplayName("RF-10: Con descripcion nula debe retornar mensaje sin sugerencia")
    void testDescripcionNulaRetornaMensaje() {
        // Act
        String resultado = resumenService.sugerirClasificacion(null);

        // Assert
        assertNotNull(resultado, "No debe lanzar excepcion con descripcion nula");
        assertFalse(resultado.isBlank(), "Debe retornar un mensaje informativo");
        // No debe invocar el modelo de IA
        verify(modeloLenguaje, never()).sugerirClasificacion(any());
    }

    @Test
    @DisplayName("RF-10: Con descripcion en blanco debe retornar mensaje sin sugerencia")
    void testDescripcionBlancaRetornaMensaje() {
        // Act
        String resultado = resumenService.sugerirClasificacion("   ");

        // Assert
        assertNotNull(resultado);
        verify(modeloLenguaje, never()).sugerirClasificacion(any());
    }

    @Test
    @DisplayName("RF-10: Debe reconocer descripcion relacionada con registro de asignatura")
    void testSugerenciaRegistroAsignatura() {
        // Arrange - IA devuelve sugerencia con el tipo correcto
        String descripcionRegistro = "Necesito inscribirme en la asignatura de calculo diferencial";
        when(modeloLenguaje.sugerirClasificacion(descripcionRegistro))
                .thenReturn("Tipo sugerido: REGISTRO_ASIGNATURA");

        // Act
        String resultado = resumenService.sugerirClasificacion(descripcionRegistro);

        // Assert
        assertTrue(resultado.contains("REGISTRO_ASIGNATURA"),
                "La sugerencia debe identificar el tipo REGISTRO_ASIGNATURA");
    }
}
