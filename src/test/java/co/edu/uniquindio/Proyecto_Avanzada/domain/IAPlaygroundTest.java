package co.edu.uniquindio.Proyecto_Avanzada.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration.ModeloLenguajeGemini;

/**
 * Playground para probar de manera real la integración con la IA (Gemini).
 * Este test ejecuta peticiones reales a la API de Google Gemini.
 */
@DisplayName("Playground Real de IA - Gemini")
class IAPlaygroundTest {

    @Test
    @DisplayName("Prueba real: Generar un resumen usando Gemini")
    void probarResumenReal() {
        ModeloLenguajeGemini gemini = new ModeloLenguajeGemini();
        
        String prompt = "Resume en una sola frase el concepto de Programación Orientada a Objetos.";
        
        System.out.println("===============================");
        System.out.println("Enviando prompt a Gemini:");
        System.out.println(prompt);
        System.out.println("===============================");
        
        String respuesta = gemini.generarResumenIA(prompt);
        
        System.out.println("===============================");
        System.out.println("Respuesta recibida de Gemini:");
        System.out.println(respuesta);
        System.out.println("===============================");
    }

    @Test
    @DisplayName("Prueba real: Sugerir clasificación usando Gemini")
    void probarClasificacionReal() {
        ModeloLenguajeGemini gemini = new ModeloLenguajeGemini();
        
        String descripcionDefecto = "Hola, necesito cancelar la materia de Matemáticas Especiales porque se cruza con mi horario de trabajo y no puedo asistir.";
        
        System.out.println("===============================");
        System.out.println("Enviando descripción para clasificar a Gemini:");
        System.out.println(descripcionDefecto);
        System.out.println("===============================");
        
        String respuesta = gemini.sugerirClasificacionIA(descripcionDefecto);
        
        System.out.println("===============================");
        System.out.println("Sugerencia de clasificación de Gemini:");
        System.out.println(respuesta);
        System.out.println("===============================");
    }
}
