package co.edu.uniquindio.Proyecto_Avanzada.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import co.edu.uniquindio.Proyecto_Avanzada.application.services.ResumenSolicitudApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration.IModeloLenguaje;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * RF-09: Generación de resúmenes de solicitudes con IA (Gemini)
 *
 * Métodos verificadores:
 * - "RF-09: Debe generar un resumen exitosamente"
 */
@DisplayName("RF-09: Test de Generación de Resúmenes con Gemini")
class RF09_SimpleTest {

    private ResumenSolicitudApplicationService resumenService;
    
    @Mock
    private IModeloLenguaje modeloLenguaje;
    
    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    
    @BeforeEach
    void setup() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this);
        
        // Crear el servicio manualmente inyectando el mock de IModeloLenguaje
        resumenService = new ResumenSolicitudApplicationService(modeloLenguaje);
        
        // Obtener instancia del repositorio y limpiarla
        RepositorioSolicitud repositorio = RepositorioSolicitud.getInstancia();
        repositorio.limpiar();
        
        // Crear usuario coordinador
        usuarioCoordinador = new Usuario(1L, "Coordinador", "1001234567", null, true, Rol.COORDINADOR);
        
        // Crear solicitud de prueba
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud de inscripción de materia Programación Avanzada",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            null,
            null,
            usuarioCoordinador,
            null
        );
        solicitud.setCodigo(1L);
        
        // Guardar la solicitud en el repositorio
        repositorio.guardarSolicitud(solicitud);
    }
    
    @Test
    @DisplayName("RF-09: Debe generar un resumen exitosamente")
    void testGenerarResumenConGemini() throws SolicitudException {
        // Arrange
        String resumenEsperado = "Resumen generado por Gemini: La solicitud de inscripción en Programación Avanzada ha sido registrada correctamente.";
        
        when(modeloLenguaje.generarResumen(any())).thenReturn(resumenEsperado);
        
        // Act
        String resumen = resumenService.generarResumenSolicitud(solicitud);
        
        // Assert
        assertNotNull(resumen, "El resumen no debe ser nulo");
        assertFalse(resumen.isEmpty(), "El resumen no debe estar vacío");
        assertTrue(resumen.contains("Gemini") || resumen.contains("inscripción") || resumen.contains("Programación"),
                   "El resumen debe contener información relevante de la solicitud");
    }
}

