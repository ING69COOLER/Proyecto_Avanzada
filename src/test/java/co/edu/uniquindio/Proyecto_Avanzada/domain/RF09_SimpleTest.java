package co.edu.uniquindio.Proyecto_Avanzada.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration.IModeloLenguaje;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
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

    @InjectMocks
    private ResumenSolicitudService resumenService;
    
    @Mock
    private IRepositorioSolicitud repositorioSolicitud;
    
    @Mock
    private IModeloLenguaje modeloLenguaje;
    
    private Solicitud solicitud;
    private Usuario usuarioCoordinador;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Inyectar mocks en el service usando Reflection
        ReflectionTestUtils.setField(resumenService, "repositorioSolicitud", repositorioSolicitud);
        ReflectionTestUtils.setField(resumenService, "modeloLenguaje", modeloLenguaje);
        
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
    }
    
    @Test
    @DisplayName("RF-09: Debe generar un resumen exitosamente")
    void testGenerarResumenConGemini() throws SolicitudException {
        // Arrange
        String resumenEsperado = "Resumen generado por Gemini: La solicitud de inscripción en Programación Avanzada ha sido registrada correctamente.";
        
        when(repositorioSolicitud.obtenerPorId(1L)).thenReturn(Optional.of(solicitud));
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

