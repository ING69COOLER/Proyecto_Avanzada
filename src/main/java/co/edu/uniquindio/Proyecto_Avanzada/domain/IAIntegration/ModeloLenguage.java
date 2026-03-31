package co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration;

import java.util.Optional;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ModeloLenguage implements IModeloLenguaje {

    @Autowired
    private IModelo modeloLenguage;

    public String generarResumenFallback(Solicitud solicitud) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE SOLICITUD ===\n");
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

    // SE buscan algunas palabras clave para sugerir la clasificacion sin IA
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

    private String construirPromptClasificacion(String descripcion) {
        return "Eres un asistente para clasificar solicitudes academicas universitarias. "
                + "A partir del texto, sugiere:\n"
                + "1. TIPO DE SOLICITUD (una de: REGISTRO_ASIGNATURA, CANCELACION_ASIGNATURA, "
                + "HOMOLOGACION, SOLICITUD_CUPOS, CONSULTA_ACADEMICA)\n"
                + "2. PRIORIDAD (una de: ALTA, MEDIA, BAJA) con justificacion breve.\n"
                + "Indica que la sugerencia debe ser confirmada o ajustada por un funcionario.\n\n"
                + "DESCRIPCION:\n" + descripcion;
    }

    private String construirPromptResumen(Solicitud solicitud) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente para gestion de solicitudes academicas universitarias. ");
        prompt.append("Genera un resumen profesional y conciso (maximo 300 palabras) de la siguiente solicitud. ");
        prompt.append(
                "Incluye: que solicita el estudiante, estado actual, acciones realizadas y proximos pasos recomendados.\n\n");

        prompt.append("DATOS DE LA SOLICITUD:\n");
        prompt.append("- Tipo: ").append(solicitud.getTipo()).append("\n");
        prompt.append("- Estado: ").append(solicitud.getEstado()).append("\n");
        prompt.append("- Solicitante (ID): ").append(solicitud.getUsuarioSolicitante().getIdentificacion())
                .append("\n");
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

    @Override
    public String generarResumen(Optional<Solicitud> solicitudOpt) {
        return this.modeloLenguage.generarResumenIA(construirPromptResumen(solicitudOpt.get()))
                + generarResumenFallback(solicitudOpt.get());
    }

    @Override
    public String sugerirClasificacion(String descripcion) {
        return this.modeloLenguage.sugerirClasificacionIA(construirPromptClasificacion(descripcion))
                + sugerirClasificacionFallback(descripcion);
    }
}
