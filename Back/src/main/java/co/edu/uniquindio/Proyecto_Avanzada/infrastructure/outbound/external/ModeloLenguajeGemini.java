package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.external;

import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * RF-09: Generacion de resumenes de solicitudes con Google Gemini.
 * RF-10: Sugerencia automatica de clasificacion y prioridad.
 * RF-11: Si la API key no esta configurada o Gemini falla, el sistema
 * opera correctamente usando resumenes/sugerencias locales (fallback).
 *
 * Implementacion usando unicamente java.net.http (JDK 11+), sin dependencias
 * externas.
 */
@Component
public class ModeloLenguajeGemini implements IModelo {

    @org.springframework.beans.factory.annotation.Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public String generarResumenIA(String resumen) {
        try {
            return extraerTextoRespuesta(llamarGemini(resumen, apiKey));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al generar resumen";
        }
    }

    @Override
    public String sugerirClasificacionIA(String descripcion) {
        try {
            return extraerTextoRespuesta(llamarGemini(descripcion, apiKey));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al sugerir clasificacion";
        }
    }

    private String llamarGemini(String prompt, String apiKey) throws Exception {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key="
                + apiKey;

        URL url = java.net.URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        // Cuerpo de la petición
        String body = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(prompt.replace("\"", "\\\""));

        // Enviar request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Leer respuesta
        InputStream is = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }

    private String extraerTextoRespuesta(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        JsonNode candidates = root.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            System.out.println(root);
            throw new Exception("No hay 'candidates' en la respuesta");
        }

        JsonNode parts = candidates.get(0)
                .path("content")
                .path("parts");

        if (!parts.isArray() || parts.isEmpty()) {
            throw new Exception("No hay 'parts' en la respuesta");
        }

        String texto = parts.get(0)
                .path("text")
                .asString();

        if (texto == null || texto.isEmpty()) {
            throw new Exception("El campo 'text' está vacío");
        }

        return texto.trim();
    }

}
