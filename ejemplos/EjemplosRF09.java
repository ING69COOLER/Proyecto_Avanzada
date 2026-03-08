/**
 * RF-09: EJEMPLOS DE USO - Generación de Resúmenes de Solicitudes con IA
 * 
 * Este archivo contiene ejemplos de cómo usar la funcionalidad
 * de generación de resúmenes en tu aplicación Spring Boot
 */

// ==========================================
// EJEMPLO 1: Usar en un Servicio
// ==========================================

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiServicio {
    
    @Autowired
    private ResumenSolicitudService resumenService;
    
    @Autowired
    private IRepositorioSolicitud repositorio;
    
    /**
     * Genera un resumen de una solicitud
     */
    public void generarResumenDeSolicitud(Long solicitudId) throws SolicitudException {
        // Obtener la solicitud
        var solicitud = repositorio.obtenerPorId(solicitudId);
        
        if (solicitud.isPresent()) {
            // Generar resumen (usará IA si está configurada)
            String resumen = resumenService.generarResumenSolicitud(solicitud.get());
            
            // Usar el resumen
            System.out.println("=== RESUMEN GENERADO ===");
            System.out.println(resumen);
        }
    }
    
    /**
     * Genera resúmenes para múltiples solicitudes
     */
    public void generarResumenesMasivos() throws SolicitudException {
        // Supon que tienes una lista de solicitudes
        var solicitudes = repositorio.obtenerTodas(); // método hipotético
        
        // Generar resúmenes para todas
        var resumenes = resumenService.generarResumenesMasivos(solicitudes);
        
        resumenes.forEach(System.out::println);
    }
    
    /**
     * Obtener longitud estimada
     */
    public void verificarComplejidad(Long solicitudId) throws SolicitudException {
        var solicitud = repositorio.obtenerPorId(solicitudId);
        
        if (solicitud.isPresent()) {
            int longitud = resumenService.obtenerLongitudEstimadaResumen(solicitud.get());
            System.out.println("Longitud estimada del resumen: " + longitud + " caracteres");
        }
    }
}

// ==========================================
// EJEMPLO 2: Usar en un Controller REST
// ==========================================

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/resumen")
public class MiControlador {
    
    @Autowired
    private ResumenSolicitudService resumenService;
    
    @Autowired
    private IRepositorioSolicitud repositorio;
    
    /**
     * GET /api/resumen/solicitud/1
     * 
     * Obtener resumen de una solicitud
     */
    @GetMapping("/solicitud/{id}")
    public ResponseEntity<String> obtenerResumen(@PathVariable Long id) {
        try {
            var solicitud = repositorio.obtenerPorId(id);
            
            if (solicitud.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            String resumen = resumenService.generarResumenSolicitud(solicitud.get());
            
            return ResponseEntity.ok(resumen);
        } catch (SolicitudException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/resumen/masivo
     * 
     * Obtener resúmenes de múltiples solicitudes
     * Body: [1, 2, 3, 4, 5]
     */
    @PostMapping("/masivo")
    public ResponseEntity<?> obtenerResumenesMasivos(@RequestBody List<Long> ids) {
        try {
            var solicitudes = ids.stream()
                .map(id -> repositorio.obtenerPorId(id))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .toList();
            
            var resumenes = resumenService.generarResumenesMasivos(solicitudes);
            
            return ResponseEntity.ok(resumenes);
        } catch (SolicitudException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

// ==========================================
// EJEMPLO 3: Usar en un Scheduled Task
// ==========================================

import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

@Service
public class GeneradorResumenesAutomatico {
    
    @Autowired
    private ResumenSolicitudService resumenService;
    
    @Autowired
    private IRepositorioSolicitud repositorio;
    
    /**
     * Ejecutar cada 30 minutos para generar resúmenes
     * de solicitudes nuevas (útil para reportes)
     */
    @Scheduled(fixedDelay = 1800000) // 30 minutos en ms
    public void generarResumenesPeriodicamente() {
        try {
            // Obtener todas las solicitudes sin resumen (hipotético)
            List<Solicitud> solicitudesSinResumen = repositorio.obtenerSinResumen();
            
            if (!solicitudesSinResumen.isEmpty()) {
                List<String> resumenes = resumenService.generarResumenesMasivos(
                    solicitudesSinResumen
                );
                
                System.out.println("Generados " + resumenes.size() + " resúmenes");
            }
        } catch (SolicitudException e) {
            System.err.println("Error generando resúmenes periódicos: " + e.getMessage());
        }
    }
}

// ==========================================
// EJEMPLO 4: Con Manejo Avanzado de Errores
// ==========================================

@Service
public class ServicioConManejodeErrores {
    
    @Autowired
    private ResumenSolicitudService resumenService;
    
    /**
     * Generar resumen con retry automático
     */
    public String generarResumenConReintentos(Long solicitudId, int maxReintentos) 
            throws SolicitudException {
        
        for (int intento = 1; intento <= maxReintentos; intento++) {
            try {
                var solicitud = repositorio.obtenerPorId(solicitudId)
                    .orElseThrow(() -> new SolicitudException("Solicitud no encontrada"));
                
                return resumenService.generarResumenSolicitud(solicitud);
                
            } catch (Exception e) {
                System.err.println(String.format(
                    "Intento %d/%d falló: %s",
                    intento, maxReintentos, e.getMessage()
                ));
                
                if (intento < maxReintentos) {
                    // Esperar antes de reintentar
                    try {
                        Thread.sleep(2000 * intento);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }
        
        throw new SolicitudException("No se pudo generar el resumen después de " + maxReintentos + " intentos");
    }
    
    /**
     * Generar resumen con logging detallado
     */
    public String generarResumenConLog(Long solicitudId) throws SolicitudException {
        System.out.println("[RF-09] Iniciando generación de resumen para solicitud: " + solicitudId);
        
        try {
            var solicitud = repositorio.obtenerPorId(solicitudId)
                .orElseThrow(() -> new SolicitudException("Solicitud no encontrada"));
            
            long tiempoInicio = System.currentTimeMillis();
            
            String resumen = resumenService.generarResumenSolicitud(solicitud);
            
            long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
            
            System.out.println(String.format(
                "[RF-09] Resumen generado en %d ms, longitud: %d caracteres",
                tiempoTotal,
                resumen.length()
            ));
            
            return resumen;
            
        } catch (Exception e) {
            System.err.println("[RF-09] Error: " + e.getMessage());
            throw new SolicitudException("Error generando resumen: " + e.getMessage(), e);
        }
    }
}

// ==========================================
// EJEMPLO 5: Requests CURL para Testing
// ==========================================

/*
# 1. Generar resumen de una solicitud
curl -X GET "http://localhost:8082/api/v1/solicitudes/1/resumen"

# 2. Generar resúmenes masivos
curl -X POST "http://localhost:8082/api/v1/solicitudes/resumenes-masivos" \
  -H "Content-Type: application/json" \
  -d "[1, 2, 3]"

# 3. Obtener longitud estimada
curl -X GET "http://localhost:8082/api/v1/solicitudes/1/longitud-resumen"

# 4. Con verbose para ver headers
curl -v -X GET "http://localhost:8082/api/v1/solicitudes/1/resumen"
*/

// ==========================================
// EJEMPLO 6: Test Unitario
// ==========================================

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResumenSolicitudServiceTest {
    
    @Mock
    private IRepositorioSolicitud repositorioSolicitud;
    
    private ResumenSolicitudService resumenService;
    private Solicitud solicitud;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        resumenService = new ResumenSolicitudService();
        resumenService.repositorioSolicitud = repositorioSolicitud;
        
        // Crear solicitud de prueba
        solicitud = new Solicitud(
            TipoSolicitud.REGISTRO_ASIGNATURA,
            "Solicitud de prueba",
            CanalOrigen.PORTAL_WEB,
            LocalDateTime.now(),
            "1001234567",
            null, null, null, null
        );
        solicitud.setId(1L);
    }
    
    @Test
    void testGenerarResumenExitosamente() throws SolicitudException {
        // Arrange
        when(repositorioSolicitud.obtenerPorId(1L))
            .thenReturn(Optional.of(solicitud));
        
        // Act
        String resumen = resumenService.generarResumenSolicitud(solicitud);
        
        // Assert
        assertNotNull(resumen);
        assertFalse(resumen.isEmpty());
        assertTrue(resumen.contains("SOLICITUD") || resumen.contains("solicitud"));
    }
    
    @Test
    void testGenerarResumenParaSolicitudNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resumenService.generarResumenSolicitud(null);
        });
    }
}

// ==========================================
// CONFIGURACIÓN NECESARIA EN application.properties
// ==========================================

/*
# Agregar a tu application.properties o usar variables de entorno

# OpenAI Configuration
spring.ai.openai.api-key=${SPRING_AI_OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.chat.options.temperature=0.7

# O si usas otro modelo:
# spring.ai.openai.chat.options.model=gpt-4
# spring.ai.openai.chat.options.model=gpt-4-turbo
*/

// ==========================================
// NOTAS IMPORTANTES
// ==========================================

/*
1. CONFIGURACIÓN:
   - Configura SPRING_AI_OPENAI_API_KEY como variable de entorno
   - NUNCA commites tu API key en el repositorio
   
2. COSTOS:
   - gpt-4o-mini es más económico y rápido para resúmenes
   - Estima ~$0.50 por 1,000 resúmenes
   
3. PERFORMANCE:
   - Los resúmenes pueden tomar 1-5 segundos
   - Considera usar async/threading para generación masiva
   
4. FALLBACK:
   - Si OpenAI no está disponible, el sistema usa resumen básico
   - No se pierde funcionalidad, solo se degrada la calidad
   
5. RATE LIMITING:
   - OpenAI tiene límites de rate (depende del plan)
   - Implementa backoff exponencial si es necesario
   
6. VALIDACIÓN:
   - Valida siempre que la solicitud tenga historial
   - Una solicitud sin cambios generará un resumen simple
*/
