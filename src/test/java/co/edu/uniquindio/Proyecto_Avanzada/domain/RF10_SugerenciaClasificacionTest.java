package co.edu.uniquindio.Proyecto_Avanzada.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import co.edu.uniquindio.Proyecto_Avanzada.application.services.ResumenSolicitudApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration.IModeloLenguaje;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * RF-10: Sugerencia automática de clasificación con IA
 *
 * Métodos verificadores:
 * - "RF-10: Debe retornar sugerencia del modelo de IA cuando está disponible"
 * - "RF-10: La sugerencia de IA se debe poder construir con la descripción de la solicitud"
 * - "RF-10: Con descripción nula debe retornar mensaje sin sugerencia"
 * - "RF-10: Con descripción en blanco debe retornar mensaje sin sugerencia"
 * - "RF-10: Debe reconocer descripción relacionada con registro de asignatura"
 */
@DisplayName("RF-10: Sugerencia automatica de clasificacion")
class RF10_SugerenciaClasificacionTest {

    private ResumenSolicitudApplicationService resumenService;

    @Mock
    private IModeloLenguaje modeloLenguaje;

    private Usuario coordinador;
    private Solicitud solicitud;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Crear el servicio manualmente inyectando el mock de IModeloLenguaje
        resumenService = new ResumenSolicitudApplicationService(modeloLenguaje);
        
        // Obtener instancia del repositorio y limpiarla
        RepositorioSolicitud repositorio = RepositorioSolicitud.getInstancia();
        repositorio.limpiar();

        coordinador = new Usuario(1L, "Coordinador Test", "1001234567", null, true, Rol.COORDINADOR);

        solicitud = new Solicitud(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Quiero registrarme en Programacion Avanzada para el siguiente semestre",
                CanalOrigen.PORTAL_WEB,
                LocalDateTime.now(),
                null,
                EstadoSolicitud.REGISTRADA,
                coordinador,
                null);
        
        // Guardar la solicitud en el repositorio
        repositorio.guardarSolicitud(solicitud);
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
