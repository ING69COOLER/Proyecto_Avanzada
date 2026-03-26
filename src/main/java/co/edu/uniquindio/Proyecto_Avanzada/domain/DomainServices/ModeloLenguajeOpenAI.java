package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import jakarta.annotation.PostConstruct;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

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
public class ModeloLenguajeOpenAI implements ResumenSolicitudService.IModeloLenguaje {

    private static final String MODELO = "gemini-3-flash-preview";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODELO + ":generateContent?key=";

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private boolean iaDisponible = false;

    // -------------------------------------------------------------------------
    // Inicializacion (RF-11)
    // -------------------------------------------------------------------------

    /**
     * RF-11: Inicializa el cliente de IA al arrancar la aplicacion.
     *
     * Lee la API key desde la variable de entorno GOOGLE_API_KEY o desde
     * application.properties (propiedad gemini.api.key).
     *
     * Si no se encuentra la clave, el sistema queda configurado en modo sin IA
     * y operara con los fallbacks locales para RF-09 y RF-10.
     */
    @PostConstruct
    private void init() {
        String envKey = System.getenv("GOOGLE_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            this.geminiApiKey = envKey;
        }

        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            System.out.println("[IA] GOOGLE_API_KEY no configurada. El sistema operara sin IA (RF-11).");
            iaDisponible = false;
        } else {
            iaDisponible = true;
            System.out.println("[IA] Cliente Gemini configurado (modelo: " + MODELO + ").");
        }
    }

    // -------------------------------------------------------------------------
    // RF-09: Generacion de resumenes
    // -------------------------------------------------------------------------

    /**
     * RF-09: Genera un resumen textual del estado e historial de la solicitud.
     *
     * Si la IA esta disponible, envia un prompt a la API de Gemini y retorna
     * el texto generado. Si la llamada falla o la IA no esta configurada,
     * aplica el fallback local (RF-11) que construye el resumen con los datos
     * de la entidad sin necesidad de servicios externos.
     *
     * @param solicitudOpt Solicitud a resumir (puede ser vacia)
     * @return Resumen textual generado por IA o por fallback local
     */
    @Override
    public String generarResumen(Optional<Solicitud> solicitudOpt) {
        if (solicitudOpt == null || solicitudOpt.isEmpty()) {
            return "No hay informacion de solicitud disponible para generar resumen.";
        }
        Solicitud solicitud = solicitudOpt.get();

        if (iaDisponible) {
            try {
                String resultado = llamarGemini(construirPromptResumen(solicitud));
                if (resultado != null && !resultado.isBlank()) {
                    return resultado;
                }
            } catch (Exception e) {
                System.err.println("[RF-09] Error al llamar a Gemini, usando fallback: " + e.getMessage());
            }
        }
        return generarResumenFallback(solicitud);
    }

    // -------------------------------------------------------------------------
    // RF-10: Sugerencia automatica de clasificacion
    // -------------------------------------------------------------------------

    /**
     * RF-10: Sugiere el tipo de solicitud y la prioridad a partir de la
     * descripcion.
     *
     * Envia el texto descriptivo a Gemini para que analice el contexto y sugiera
     * clasificacion (REGISTRO_ASIGNATURA, HOMOLOGACION, etc.) y nivel de prioridad.
     *
     * RF-11: Si la IA no esta disponible, aplica un fallback local basado en
     * palabras clave dentro del texto (sin dependencia de servicios externos).
     *
     * IMPORTANTE (RF-10): Las sugerencias producidas deben ser confirmadas o
     * ajustadas por un funcionario humano. No reemplazan la decision del
     * responsable.
     *
     * @param descripcion Texto ingresado por el solicitante
     * @return Sugerencia de tipo y prioridad (IA o fallback local)
     */
    @Override
    public String sugerirClasificacion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return "Sin descripcion suficiente para sugerir clasificacion.";
        }

        if (iaDisponible) {
            try {
                String resultado = llamarGemini(construirPromptClasificacion(descripcion));
                if (resultado != null && !resultado.isBlank()) {
                    return resultado;
                }
            } catch (Exception e) {
                System.err.println("[RF-10] Error al llamar a Gemini, usando fallback: " + e.getMessage());
            }
        }
        return sugerirClasificacionFallback(descripcion);
    }

    // -------------------------------------------------------------------------
    // Llamada HTTP a la API REST de Gemini (solo JDK estandar)
    // -------------------------------------------------------------------------

    private String llamarGemini(String prompt) throws Exception {
        // Construir JSON manualmente: {"contents":[{"parts":[{"text":"..."}]}]}
        String promptEscapado = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String requestJson = "{\"contents\":[{\"parts\":[{\"text\":\"" + promptEscapado + "\"}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (response.statusCode() != 200) {
            throw new Exception("Gemini API error " + response.statusCode() + ": " + body);
        }

        return extraerTextoRespuesta(body);
    }

    /**
     * Extrae el texto de la respuesta JSON de Gemini.
     * Estructura esperada: {"candidates":[{"content":{"parts":[{"text":"..."}]}}]}
     */
    private String extraerTextoRespuesta(String json) throws Exception {
        // Buscar "text": (puede haber espacios antes de la comilla del valor)
        String marcador = "\"text\":";
        int inicio = json.indexOf(marcador);
        if (inicio < 0) {
            String preview = json.length() > 500 ? json.substring(0, 500) : json;
            System.err.println("[IA] Respuesta inesperada de Gemini: " + preview);
            throw new Exception("No se encontro campo 'text' en la respuesta de Gemini");
        }
        inicio += marcador.length();

        // Saltar espacios opcionales y llegar a la comilla de apertura
        while (inicio < json.length() && json.charAt(inicio) == ' ') {
            inicio++;
        }
        if (inicio >= json.length() || json.charAt(inicio) != '"') {
            throw new Exception("Formato inesperado del campo 'text' en la respuesta de Gemini");
        }
        inicio++; // saltar la comilla de apertura
        // Extraer el valor hasta la siguiente comilla no escapada
        StringBuilder texto = new StringBuilder();
        for (int i = inicio; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char siguiente = json.charAt(i + 1);
                switch (siguiente) {
                    case '"':
                        texto.append('"');
                        i++;
                        break;
                    case '\\':
                        texto.append('\\');
                        i++;
                        break;
                    case 'n':
                        texto.append('\n');
                        i++;
                        break;
                    case 'r':
                        texto.append('\r');
                        i++;
                        break;
                    case 't':
                        texto.append('\t');
                        i++;
                        break;
                    default:
                        texto.append(c);
                }
            } else if (c == '"') {
                break; // Fin del valor
            } else {
                texto.append(c);
            }
        }
        return texto.toString().trim();
    }

    // -------------------------------------------------------------------------
    // Construccion de prompts
    // -------------------------------------------------------------------------

    private String construirPromptResumen(Solicitud solicitud) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente para gestion de solicitudes academicas universitarias. ");
        prompt.append("Genera un resumen profesional y conciso (maximo 300 palabras) de la siguiente solicitud. ");
        prompt.append(
                "Incluye: que solicita el estudiante, estado actual, acciones realizadas y proximos pasos recomendados.\n\n");

        prompt.append("DATOS DE LA SOLICITUD:\n");
        prompt.append("- ID: ").append(solicitud.getId()).append("\n");
        prompt.append("- Tipo: ").append(solicitud.getTipo()).append("\n");
        prompt.append("- Estado: ").append(solicitud.getEstado()).append("\n");
        prompt.append("- Solicitante (ID): ").append(solicitud.getUsuarioSolicitante().getIdentificacion()).append("\n");
        prompt.append("- Fecha de Registro: ").append(solicitud.getFechaHoraRegistro()).append("\n");
        prompt.append("- Canal de Origen: ").append(solicitud.getCanalOrigen()).append("\n");

        if (solicitud.getPrioridad() != null) {
            prompt.append("- Prioridad: ").append(solicitud.getPrioridad().nivel()).append("\n");
            prompt.append("- Justificacion: ").append(solicitud.getPrioridad().descripcion()).append("\n");
        }

        prompt.append("\nDESCRIPCION:\n").append(solicitud.getDescripcion()).append("\n");

        prompt.append("\nHISTORIAL DE CAMBIOS:\n");
        if (solicitud.getHistorial() != null && !solicitud.getHistorial().isEmpty()) {
            solicitud.getHistorial().stream()
                    .map(h -> "[" + h.getFechaHora() + "] " + h.getAccion()
                            + " - " + h.getObservacion()
                            + " (por: " + (h.getResponsable() != null ? h.getResponsable().getNombre() : "Sistema")
                            + ")")
                    .forEach(l -> prompt.append("  ").append(l).append("\n"));
        } else {
            prompt.append("  Sin cambios registrados.\n");
        }

        return prompt.toString();
    }

    private String construirPromptClasificacion(String descripcion) {
        return "Eres un asistente para clasificar solicitudes academicas universitarias. "
                + "A partir del texto, sugiere:\n"
                + "1. TIPO DE SOLICITUD (una de: REGISTRO_ASIGNATURA, CANCELACION_ASIGNATURA, "
                + "HOMOLOGACION, SOLICITUD_CUPOS, CONSULTA_ACADEMICA)\n"
                + "2. PRIORIDAD (una de: ALTA, MEDIA, BAJA) con justificacion breve.\n"
                + "Indica que la sugerencia debe ser confirmada o ajustada por un funcionario.\n\n"
                + "DESCRIPCION:\n" + descripcion;
    }

    // -------------------------------------------------------------------------
    // RF-11: Fallbacks sin IA
    // -------------------------------------------------------------------------

    private String generarResumenFallback(Solicitud solicitud) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE SOLICITUD ===\n");
        sb.append("ID:          ").append(solicitud.getId()).append("\n");
        sb.append("Tipo:        ").append(solicitud.getTipo()).append("\n");
        sb.append("Estado:      ").append(solicitud.getEstado()).append("\n");
        sb.append("Solicitante: ").append(solicitud.getUsuarioSolicitante().getIdentificacion()).append("\n");
        sb.append("Fecha:       ").append(solicitud.getFechaHoraRegistro()).append("\n");
        sb.append("Canal:       ").append(solicitud.getCanalOrigen()).append("\n");

        if (solicitud.getPrioridad() != null) {
            sb.append("Prioridad:   ").append(solicitud.getPrioridad().nivel())
                    .append(" - ").append(solicitud.getPrioridad().descripcion()).append("\n");
        }

        sb.append("\nDescripcion:\n  ").append(solicitud.getDescripcion()).append("\n");

        sb.append("\nHistorial (ultimas entradas):\n");
        if (solicitud.getHistorial() != null && !solicitud.getHistorial().isEmpty()) {
            solicitud.getHistorial().stream()
                    .skip(Math.max(0, solicitud.getHistorial().size() - 5))
                    .forEach(h -> sb.append("  - [").append(h.getAccion()).append("] ")
                            .append(h.getObservacion()).append(" (")
                            .append(h.getResponsable() != null ? h.getResponsable().getNombre() : "Sistema")
                            .append(")\n"));
        } else {
            sb.append("  Sin cambios registrados.\n");
        }

        sb.append("============================\n");
        return sb.toString();
    }
//SE buscan algunas palabras clave para sugerir la clasificacion sin IA
    private String sugerirClasificacionFallback(String descripcion) {
        String desc = descripcion.toLowerCase();

        TipoSolicitud tipo = TipoSolicitud.CONSULTA_ACADEMICA;
        if (desc.contains("inscripci") || desc.contains("registro") || desc.contains("materia")
                || desc.contains("asignatura")) {
            tipo = TipoSolicitud.REGISTRO_ASIGNATURA;
        } else if (desc.contains("cancelaci") || desc.contains("cancelar")) {
            tipo = TipoSolicitud.CANCELACION_ASIGNATURA;
        } else if (desc.contains("homologaci") || desc.contains("convalidaci")) {
            tipo = TipoSolicitud.HOMOLOGACION;
        } else if (desc.contains("cupo") || desc.contains("cupos")) {
            tipo = TipoSolicitud.SOLICITUD_CUPOS;
        }

        NivelPrioridad prioridad = NivelPrioridad.MEDIA;
        if (desc.contains("urgent") || desc.contains("inmediat") || desc.contains("plazo")) {
            prioridad = NivelPrioridad.ALTA;
        } else if (desc.contains("informaci") || desc.contains("consulta")) {
            prioridad = NivelPrioridad.BAJA;
        }

        return "[SUGERENCIA AUTOMATICA - Debe ser confirmada por un funcionario]\n"
                + "Tipo sugerido:     " + tipo + "\n"
                + "Prioridad sugerida: " + prioridad + "\n"
                + "Nota: Sugerencia generada localmente (IA no disponible).";
    }
}
