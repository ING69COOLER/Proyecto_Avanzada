package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import jakarta.annotation.PostConstruct;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RF-09: Implementación de generación de resúmenes con Google Gemini
 * 
 * Utiliza el modelo de lenguaje Gemini de Google para generar resúmenes 
 * inteligentes del estado y historial de solicitudes.
 */
@Component
public class ModeloLenguajeOpenAI implements ResumenSolicitudService.IModeloLenguaje {
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta2/models/gemini-3-flash-preview:generateMessage}")
    private String geminiApiUrl;

    @Value("${gemini.api.key:AIzaSyAjZSPxL9F3RvgGRsINUkIydi8teDoRcIM}")
    private String geminiApiKey;

    private static final String LIST_MODELS_URL = "https://generativelanguage.googleapis.com/v1beta/models";
    
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private Client genaiClient;
    
    /**
     * RF-09: Genera un resumen inteligente usando Google Gemini
     * 
     * @param solicitudCompleta Solicitud a resumir
     * @return Resumen textual generado por IA
     */
    @Override
    public String generarResumen(Optional<Solicitud> solicitudCompleta) {
        if (solicitudCompleta == null || solicitudCompleta.isEmpty()) {
            return "No hay información de solicitud disponible para generar resumen.";
        }
        
        Solicitud solicitud = solicitudCompleta.get();
        
        // Construir el prompt para el modelo de lenguaje
        String prompt = construirPromptParaResumen(solicitud);
        
        try {
            // Use official GenAI client if available
            if (genaiClient != null) {
                try {
                    String model = System.getenv("GEMINI_MODEL");
                    if (model == null || model.isBlank()) {
                        model = "gemini-3-flash-preview"; // sensible default (may need change per account)
                    }
                    try {
                        // Use reflection to find a 'models' accessor or field
                        Object modelsObj = null;
                        // try getter method
                        for (java.lang.reflect.Method m : genaiClient.getClass().getMethods()) {
                            if (m.getName().equalsIgnoreCase("models") && m.getParameterCount() == 0) {
                                try { modelsObj = m.invoke(genaiClient); break; } catch (Exception ignored) {}
                            }
                        }
                        // try field fallback
                        if (modelsObj == null) {
                            for (java.lang.reflect.Field f : genaiClient.getClass().getDeclaredFields()) {
                                if (f.getName().toLowerCase().contains("model")) {
                                    f.setAccessible(true);
                                    try { modelsObj = f.get(genaiClient); break; } catch (Exception ignored) {}
                                }
                            }
                        }

                        if (modelsObj != null) {
                            // try several possible method names and signatures
                            String[] methodNames = new String[]{"generateContent", "generateMessage", "generate"};
                            Object resp = null;
                            for (String mName : methodNames) {
                                for (java.lang.reflect.Method mm : modelsObj.getClass().getMethods()) {
                                    if (!mm.getName().equals(mName)) continue;
                                    Class<?>[] params = mm.getParameterTypes();
                                    try {
                                        if (params.length == 3 && params[0] == String.class) {
                                            resp = mm.invoke(modelsObj, model, prompt, null);
                                        } else if (params.length == 2 && params[0] == String.class) {
                                            resp = mm.invoke(modelsObj, model, prompt);
                                        } else if (params.length == 1 && params[0] == String.class) {
                                            // some variants may require only the prompt/model combined
                                            resp = mm.invoke(modelsObj, prompt);
                                        }
                                    } catch (IllegalArgumentException ia) {
                                        // try next
                                    }
                                    if (resp != null) break;
                                }
                                if (resp != null) break;
                            }

                            if (resp != null) {
                                // try to extract text via common methods
                                try {
                                    for (java.lang.reflect.Method rm : resp.getClass().getMethods()) {
                                        if (rm.getName().equalsIgnoreCase("text") || rm.getName().equalsIgnoreCase("getText") || rm.getName().equalsIgnoreCase("getResponse")) {
                                            Object text = rm.invoke(resp);
                                            if (text != null) return text.toString();
                                        }
                                    }
                                } catch (Exception ex) {
                                    // ignore and continue to fallback
                                }
                            }
                        }
                    } catch (Exception re) {
                        System.err.println("[RF-09] GenAI client reflection error: " + re.getMessage());
                    }
                } catch (Exception clientEx) {
                    System.err.println("[RF-09] GenAI client error: " + clientEx.getMessage());
                }
            }

            // Fallback to HTTP approach if client not available or failed
            String resumen = llamarGemini(prompt);
            return resumen != null ? resumen : generarResumenFallback(solicitud);
        } catch (Exception e) {
            System.err.println("[RF-09] Error llamando Gemini: " + e.getMessage());
            // Si falla Gemini, retornar resumen básico como fallback
            return generarResumenFallback(solicitud);
        }
    }
    
    /**
     * RF-09: Llamar a la API de Google Gemini
     * 
     * @param prompt Prompt para enviar a Gemini
     * @return Respuesta de Gemini
     * @throws IOException Si hay error de conexión
     */
    private String llamarGemini(String prompt) throws IOException {
        // Determine endpoint type: generateMessage (v1beta2) expects `messages` payload,
        // while older generateContent/generate endpoints expect `contents` + generationConfig.
        String lowerUrl = geminiApiUrl != null ? geminiApiUrl.toLowerCase() : "";
        if (lowerUrl.contains("generatemessage") || lowerUrl.contains("/v1beta2/")) {
            // If the configured URL already includes a model (e.g. .../models/<model>:generateMessage)
            if (lowerUrl.contains("/models/")) {
                // try to extract model from URL
                try {
                    int idx = geminiApiUrl.indexOf("/models/");
                    int colon = geminiApiUrl.indexOf(":", idx);
                    String model = null;
                    if (idx >= 0 && colon > idx) {
                        model = geminiApiUrl.substring(idx + "/models/".length(), colon);
                    }
                    if (model == null || model.isBlank()) model = System.getenv("GEMINI_MODEL");
                    if (model == null || model.isBlank()) model = "gemini-3-flash-preview";
                    return llamarGeminiGenerateMessage(model, prompt);
                } catch (Exception e) {
                    // fallback to calling generateMessage with env model
                    String model = System.getenv("GEMINI_MODEL");
                    if (model == null || model.isBlank()) model = "gemini-3-flash-preview";
                    return llamarGeminiGenerateMessage(model, prompt);
                }
            } else {
                String model = System.getenv("GEMINI_MODEL");
                if (model == null || model.isBlank()) model = "gemini-3-flash-preview";
                return llamarGeminiGenerateMessage(model, prompt);
            }
        }

        // Default: call generic v1 generate endpoint using `prompt` + top-level generation params
        JsonObject promptObj = new JsonObject();
        promptObj.addProperty("text", prompt);

        JsonObject requestBody = new JsonObject();
        requestBody.add("prompt", promptObj);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("maxOutputTokens", 500);

        // Construir URL con API key
        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

        // Crear request HTTP
        Request request = new Request.Builder()
            .url(urlWithKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                requestBody.toString(),
                MediaType.get("application/json; charset=utf-8")
            ))
            .build();

        // Ejecutar request
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                String msg = String.format("Gemini API error: %d - %s. Body: %s", response.code(), response.message(), responseBody);
                throw new IOException(msg);
            }

            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // Extraer texto de varias formas que usa la API
            try {
                if (jsonResponse.has("candidates")) {
                    return jsonResponse
                        .getAsJsonArray("candidates")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0)
                        .getAsJsonObject()
                        .get("text")
                        .getAsString();
                }
                if (jsonResponse.has("output") && jsonResponse.get("output").isJsonPrimitive()) {
                    return jsonResponse.get("output").getAsString();
                }
                if (jsonResponse.has("response") && jsonResponse.get("response").isJsonPrimitive()) {
                    return jsonResponse.get("response").getAsString();
                }
                // Fallback: return full body
                return responseBody;
            } catch (Exception e) {
                System.err.println("[RF-09] Error procesando respuesta Gemini: " + e.getMessage());
                System.err.println("[RF-09] Respuesta completa de Gemini: " + jsonResponse);
                throw new IOException("Error al procesar respuesta de Gemini", e);
            }
        }
    }

    /**
     * Reintento usando el endpoint v1beta2 `generateMessage` para un modelo específico.
     */
    private String llamarGeminiGenerateMessage(String model, String prompt) throws IOException {
        String urlWithKey = "https://generativelanguage.googleapis.com/v1beta2/models/" + model + ":generateMessage?key=" + geminiApiKey;

        // v1beta2 generateMessage expects a single `message` object and top-level
        // generation parameters (temperature, maxOutputTokens)
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("author", "user");
        JsonArray content = new JsonArray();
        JsonObject textPart = new JsonObject();
        textPart.addProperty("type", "text");
        textPart.addProperty("text", prompt);
        content.add(textPart);
        userMsg.add("content", content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("message", userMsg);
        // Top-level generation params
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("maxOutputTokens", 500);

        Request request = new Request.Builder()
            .url(urlWithKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json; charset=utf-8")))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("Gemini generateMessage failed: " + response.code() + " - " + response.message() + ". Body: " + responseBody);
            }

            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            try {
                // Try to extract text from common response shapes
                if (jsonResponse.has("candidates")) {
                    return jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                        .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();
                }
                if (jsonResponse.has("output")) {
                    return jsonResponse.get("output").getAsString();
                }
                // Fallback: return whole body
                return responseBody;
            } catch (Exception e) {
                throw new IOException("Error procesando respuesta generateMessage: " + e.getMessage());
            }
        }
    }

    @PostConstruct
    private void init() {
        // Prefer environment variable if provided (avoid committing keys)
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            this.geminiApiKey = envKey;
        }
        String envUrl = System.getenv("GEMINI_API_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            this.geminiApiUrl = envUrl;
        }

        // Initialize official GenAI client if API key is present
        try {
            String key = System.getenv("GEMINI_API_KEY");
            if (key == null || key.isBlank()) key = this.geminiApiKey;
            if (key != null && !key.isBlank()) {
                // The Client constructor reads GEMINI_API_KEY env var by default,
                // but to be explicit, set the env var if not already set.
                if (System.getenv("GEMINI_API_KEY") == null) {
                    // best-effort: set as system property for the client library
                    System.setProperty("GEMINI_API_KEY", key);
                }
                genaiClient = new Client();
            }
        } catch (Exception e) {
            System.err.println("[RF-09] No se pudo inicializar GenAI client: " + e.getMessage());
            genaiClient = null;
        }
    }

    private String listarModelosDisponibles() throws IOException {
        String url = LIST_MODELS_URL + "?key=" + geminiApiKey;
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("ListModels failed: " + response.code() + " - " + response.message() + ". Body: " + body);
            }

            JsonObject json = gson.fromJson(body, JsonObject.class);
            if (json == null || !json.has("models")) return "(no models returned)";
            JsonArray models = json.getAsJsonArray("models");
            if (models.size() == 0) return "(no models)";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < models.size(); i++) {
                JsonObject m = models.get(i).getAsJsonObject();
                String name = m.has("name") ? m.get("name").getAsString() : "(unnamed)";
                if (i > 0) sb.append(", ");
                sb.append(name);
            }
            return sb.toString();
        }
    }
    
    /**
     * RF-09: Construye el prompt para el modelo de lenguaje
     * Proporciona toda la información relevante de la solicitud
     * 
     * @param solicitud Solicitud a procesar
     * @return Prompt formateado para Gemini
     */
    private String construirPromptParaResumen(Solicitud solicitud) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Analiza la siguiente solicitud académica y proporciona un resumen profesional. ")
              .append("El resumen debe incluir: (1) El problema o solicitud principal, (2) El estado actual y clasificación, ")
              .append("(3) Las acciones tomadas hasta el momento, (4) La prioridad e importancia, (5) Los próximos pasos recomendados. ")
              .append("Sé conciso pero completo (150-300 palabras). Usa un tono profesional.\n\n");
        
        prompt.append("DATOS DE LA SOLICITUD:\n");
        prompt.append("- ID: ").append(solicitud.getId()).append("\n");
        prompt.append("- Tipo: ").append(solicitud.getTipo()).append("\n");
        prompt.append("- Solicitante (Identificación): ").append(solicitud.getIdentificacionSolicitante()).append("\n");
        prompt.append("- Fecha de Registro: ").append(solicitud.getFechaHoraRegistro()).append("\n");
        prompt.append("- Estado Actual: ").append(solicitud.getEstado()).append("\n");
        
        if (solicitud.getPrioridad() != null) {
            prompt.append("- Nivel de Prioridad: ").append(solicitud.getPrioridad().getNivel()).append("\n");
            prompt.append("- Justificación de Prioridad: ").append(solicitud.getPrioridad().getDescripcion()).append("\n");
        }
        
        prompt.append("- Canal de Origen: ").append(solicitud.getCanalOrigen()).append("\n");
        
        prompt.append("\nDESCRIPCIÓN DE LA SOLICITUD:\n");
        prompt.append(solicitud.getDescripcion()).append("\n");
        
        prompt.append("\nHISTORIAL DE CAMBIOS:\n");
        if (solicitud.getHistorial() != null && !solicitud.getHistorial().isEmpty()) {
            String historialFormato = solicitud.getHistorial().stream()
                .map(h -> String.format("  [%s] Acción: %s | Usuario: %s | Observación: %s",
                    h.getFechaHora(),
                    h.getAccion(),
                    h.getResponsable() != null ? h.getResponsable().getNombre() : "Sistema",
                    h.getObservacion()))
                .collect(Collectors.joining("\n"));
            prompt.append(historialFormato).append("\n");
        } else {
            prompt.append("  No hay historial de cambios registrados.\n");
        }
        
        prompt.append("INSTRUCCIONES CLAVE:\n");
        prompt.append("1. Comienza identificando específicamente qué solicita el usuario.\n");
        prompt.append("2. Resume el contexto: por qué se presenta la solicitud, qué necesidad aborda.\n");
        prompt.append("3. Detalla el progreso: qué se ha hecho, quién ha intervenido, en qué estado está.\n");
        prompt.append("4. Evalúa la prioridad: es urgente? hay riesgos? qué tan importante es?.\n");
        prompt.append("5. Sugiere qué hacer a continuación de forma clara y accionable.\n");
        prompt.append("6. Mantén 150-300 palabras. Sé directo y evita información redundante.\n");
        
        return prompt.toString();
    }
    
    /**
     * RF-09: Fallback cuando el modelo de IA no está disponible
     * Genera un resumen básico bien estructurado
     * 
     * @param solicitud Solicitud a resumir
     * @return Resumen textual de fallback
     */
    private String generarResumenFallback(Solicitud solicitud) {
        StringBuilder resumen = new StringBuilder();
        
        resumen.append("[RESUMEN DE SOLICITUD ACADEMICA]\n");
        resumen.append("=".repeat(50)).append("\n\n");
        
        resumen.append("[INFORMACION GENERAL]");
        resumen.append("\n  ID Solicitud: ").append(solicitud.getId());
        resumen.append("\n  Tipo: ").append(solicitud.getTipo());
        resumen.append("\n  Estado: ").append(solicitud.getEstado());
        resumen.append("\n  Solicitante: ").append(solicitud.getIdentificacionSolicitante());
        resumen.append("\n  Cambios registrados: ").append(
            solicitud.getHistorial() != null ? solicitud.getHistorial().size() : 0).append("\n");
        
        if (solicitud.getPrioridad() != null) {
            resumen.append("\n[PRIORIDAD]\n");
            resumen.append("  Nivel: ").append(solicitud.getPrioridad().getNivel()).append("\n");
            resumen.append("  Justificacion: ").append(solicitud.getPrioridad().getDescripcion()).append("\n");
        }
        
        resumen.append("\n[DESCRIPCION]\n");
        resumen.append(solicitud.getDescripcion()).append("\n");
        
        resumen.append("\n[HISTORIAL - ULTIMOS CAMBIOS]\n");
        if (solicitud.getHistorial() != null && !solicitud.getHistorial().isEmpty()) {
            solicitud.getHistorial().stream()
                .skip(Math.max(0, solicitud.getHistorial().size() - 5)) // Últimos 5 cambios
                .forEach(h -> resumen.append("  → ")
                    .append(h.getAccion())
                    .append(" - ")
                    .append(h.getObservacion())
                    .append(" (")
                    .append(h.getResponsable() != null ? h.getResponsable().getNombre() : "Sistema")
                    .append(")\n"));
        } else {
            resumen.append("  → Sin cambios registrados aún.\n");
        }
        
        resumen.append("\n═══════════════════════════════════════════\n");
        
        return resumen.toString();
    }
}


