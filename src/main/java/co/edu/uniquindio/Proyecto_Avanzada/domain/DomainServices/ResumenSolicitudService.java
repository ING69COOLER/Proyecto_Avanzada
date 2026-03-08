package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RF-09: Servicio de Dominio para generación de resúmenes de solicitudes
 * 
 * Genera un resumen textual del estado y el historial de una solicitud
 * utilizando
 * un modelo de lenguaje externo.
 * 
 * Justificación: Mejorar la comprensión rápida del caso por parte de los
 * responsables.
 */
@Service
public class ResumenSolicitudService {

    @Autowired
    private IRepositorioSolicitud repositorioSolicitud;

    @Autowired(required = false)
    private IModeloLenguaje modeloLenguaje;

    /**
     * RF-09: Genera un resumen textual de una solicitud
     * Incluye: tipo, descripción, estado actual, prioridad e historial resumido
     * 
     * Si hay modelo de lenguaje configurado, genera un resumen inteligente.
     * Si no, genera un resumen estructurado básico.
     * 
     * @param solicitud Solicitud para la cual generar el resumen
     * @return Resumen textual de la solicitud
     * @throws SolicitudException Si la solicitud es nula
     */
    public String generarResumenSolicitud(Solicitud solicitud) throws SolicitudException {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        // If the solicitud has no ID (not persisted), allow generating a summary
        // using the provided in-memory object. Otherwise, load the complete
        // entity from the repository to include any persisted history.
        Optional<Solicitud> solicitudCompleta;
        if (solicitud.getId() == null) {
            solicitudCompleta = Optional.of(solicitud);
        } else {
            solicitudCompleta = repositorioSolicitud.obtenerPorId(solicitud.getId());
            if (!solicitudCompleta.isPresent()) {
                throw new SolicitudException("No se encontró la solicitud con ID: " + solicitud.getId());
            }
        }

        // Si hay modelo de lenguaje disponible, usarlo
        if (modeloLenguaje != null) {
            System.out.println("------- Resumen generado con IA-------");
            return modeloLenguaje.generarResumen(solicitudCompleta);
        }

        // Si no, generar resumen básico
        return generarResumenBasico(solicitudCompleta.get());
    }

    /**
     * RF-09: Genera un resumen básico cuando no hay modelo de IA disponible
     * Estructura: Encabezado + Descripción + Estado + Historial resumido
     * 
     * @param solicitud Solicitud a resumir
     * @return Resumen textual básico
     */
    private String generarResumenBasico(Solicitud solicitud) {
        StringBuilder resumen = new StringBuilder();

        // Encabezado
        resumen.append("=== RESUMEN DE SOLICITUD ===\n");
        resumen.append("ID: ").append(solicitud.getId()).append("\n");
        resumen.append("Tipo: ").append(solicitud.getTipo()).append("\n");
        resumen.append("Solicitante: ").append(solicitud.getIdentificacionSolicitante()).append("\n");
        resumen.append("Fecha de Registro: ").append(solicitud.getFechaHoraRegistro()).append("\n");
        resumen.append("Estado Actual: ").append(solicitud.getEstado()).append("\n");

        // Prioridad si está disponible
        if (solicitud.getPrioridad() != null) {
            resumen.append("Prioridad: ").append(solicitud.getPrioridad().getNivel()).append("\n");
        }

        // Descripción
        resumen.append("\nDescripción:\n");
        resumen.append(solicitud.getDescripcion()).append("\n");

        // Historial resumido
        resumen.append("\nHistorial de cambios:\n");
        if (solicitud.getHistorial() != null && !solicitud.getHistorial().isEmpty()) {
            List<String> historialResumido = solicitud.getHistorial().stream()
                    .map(h -> String.format("- [%s] %s: %s (por %s)",
                            h.getFechaHora(),
                            h.getAccion(),
                            h.getObservacion(),
                            h.getResponsable() != null ? h.getResponsable().getNombre() : "Sistema"))
                    .collect(Collectors.toList());

            historialResumido.forEach(resumen::append);
        } else {
            resumen.append("- Sin cambios registrados\n");
        }

        resumen.append("\n=============================\n");
        return resumen.toString();
    }

    /**
     * RF-09: Genera resúmenes para múltiples solicitudes
     * Útil para reportes o análisis batch
     * 
     * @param solicitudes Lista de solicitudes a resumir
     * @return Lista de resúmenes en el mismo orden
     * @throws SolicitudException Si alguna solicitud es inválida
     */
    public List<String> generarResumenesMasivos(List<Solicitud> solicitudes) throws SolicitudException {
        if (solicitudes == null || solicitudes.isEmpty()) {
            throw new IllegalArgumentException("La lista de solicitudes no puede estar vacía");
        }

        return solicitudes.stream()
                .map(s -> {
                    try {
                        return generarResumenSolicitud(s);
                    } catch (SolicitudException e) {
                        return "Error al generar resumen: " + e.getMessage();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * RF-09: Obtiene la longitud estimada del resumen para optimización
     * 
     * @param solicitud Solicitud para calcular complejidad del resumen
     * @return Aproximación de caracteres que tendrá el resumen
     */
    public int obtenerLongitudEstimadaResumen(Solicitud solicitud) {
        if (solicitud == null) {
            return 0;
        }

        int longitud = 0;
        longitud += solicitud.getDescripcion() != null ? solicitud.getDescripcion().length() : 0;
        longitud += solicitud.getHistorial() != null ? solicitud.getHistorial().size() * 100 : 0;

        return longitud;
    }

    /**
     * RF-10: Sugiere tipo de solicitud y prioridad basándose en la descripción.
     * La sugerencia debe ser confirmada o ajustada por un usuario .
     *
     * @param descripcion Texto descriptivo ingresado
     * @return Sugerencia de tipo y prioridad
     */
    public String sugerirClasificacion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return "Sin descripción suficiente para sugerir clasificación.";
        }
        if (modeloLenguaje != null) {
            return modeloLenguaje.sugerirClasificacion(descripcion);
        }
        return "[Sugerencia no disponible: IA no configurada. El funcionario debe clasificar manualmente.]";
    }

    /**
     * Interfaz para inyectar diferentes modelos de lenguaje
     */
    public interface IModeloLenguaje {
        /**
         * RF-09: Genera un resumen de una solicitud usando un modelo de IA
         * 
         * @param solicitud Solicitud a resumir
         * @return Resumen generado por el modelo de IA
         */
        String generarResumen(Optional<Solicitud> solicitud);

        /**
         * RF-10: Sugiere tipo y prioridad a partir del texto descriptivo
         * 
         * @param descripcion Texto de la solicitud
         * @return Sugerencia de clasificación (debe ser confirmada por un humano)
         */
        String sugerirClasificacion(String descripcion);
    }
}
