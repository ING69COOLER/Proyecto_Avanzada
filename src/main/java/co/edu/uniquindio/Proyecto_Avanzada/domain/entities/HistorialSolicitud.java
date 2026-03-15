package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.Data;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;

@Data

// no se porque una observacion
public class HistorialSolicitud {

    private Long id;

    private LocalDateTime fechaHora;

    private String observacion;

    private EstadoSolicitud estado;

    private TipoAccion accion;

    private Usuario responsable;

    private Solicitud solicitud;

    public HistorialSolicitud(EstadoSolicitud estado, TipoAccion accion,
            Usuario responsable, String observacion, Solicitud solicitud) {
        if (responsable == null || observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException(
                    "El responsable y la observacion no pueden ser nulos o vacios.");
        }
        this.fechaHora = LocalDateTime.now();
        this.estado = estado;
        this.accion = accion;
        this.responsable = responsable;
        this.solicitud = solicitud;
        this.observacion = observacion;
    }

    public Usuario obtenerUsuario() {
        return responsable;
    }

}
