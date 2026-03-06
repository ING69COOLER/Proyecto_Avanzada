package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.Data;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RF-01: Entidad que representa una solicitud académica registrada en el
 * sistema.
 * Almacena: tipo, descripción, canal de origen, fecha/hora de registro e
 * identificación del solicitante.
 */
@Data
public class Solicitud {

    private Long id;

    /** RF-01: Tipo de solicitud */
    private TipoSolicitud tipo;

    /** RF-01: Descripción de la solicitud */
    private String descripcion;

    /** RF-01: Canal de origen (CSU, correo, SAC, telefónico, etc.) */
    private CanalOrigen canalOrigen;

    /** RF-01: Fecha y hora de registro */
    private LocalDateTime fechaHoraRegistro;

    /** RF-01: Identificación del solicitante */
    private String identificacionSolicitante;

    private LocalDateTime fechaCierre;

    private EstadoSolicitud estado;

    private Usuario usuarioSolicitante;

    private Prioridad prioridad;

    private List<HistorialSolicitud> historial;

    public Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
            Usuario usuarioSolicitante, Prioridad prioridad) {
        // RF-01: validar campos obligatorios
        if (tipo == null || descripcion == null || descripcion.isBlank()
                || canalOrigen == null || fechaHoraRegistro == null
                || identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar al menos: tipo de solicitud, descripción, " +
                            "canal de origen, fecha/hora de registro e identificación del solicitante.");
        }

        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaHoraRegistro = fechaHoraRegisro;
        this.identificacionSolicitante = identificacion;
        this.id = null;
        this.fechaCierre = fechaCierre;
        this.estado = estado;
        this.usuarioSolicitante = usuarioSolicitante;
        this.prioridad = prioridad;
    }

    /**
     * Crea una entrada en el historial de la solicitud
     *
     * @param estado      Estado de la solicitud en el momento del evento
     * @param accion      Tipo de acción realizada
     * @param responsable Usuario responsable de la acción
     * @param observacion Descripción detallada de lo realizado
     */
    public void crearHistoria(EstadoSolicitud estado, TipoAccion accion, Usuario responsable, String observacion) {
        if (responsable == null || observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("El responsable y la observación no pueden ser nulos o vacíos.");
        }

        HistorialSolicitud entrada = new HistorialSolicitud();
        entrada.setFechaHora(LocalDateTime.now());
        entrada.setEstado(estado);
        entrada.setAccion(accion);
        entrada.setResponsable(responsable);
        entrada.setSolicitud(this);
        entrada.setObservacion(observacion);

        this.historial.add(entrada);
    }

}
