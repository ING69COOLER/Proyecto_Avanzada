package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
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
@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** RF-01: Tipo de solicitud */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoSolicitud tipo;

    /** RF-01: Descripción de la solicitud */
    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    /** RF-01: Canal de origen (CSU, correo, SAC, telefónico, etc.) */
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origen", nullable = false)
    private CanalOrigen canalOrigen;

    /** RF-01: Fecha y hora de registro */
    @Column(name = "fecha_hora_registro", nullable = false)
    private LocalDateTime fechaHoraRegistro;

    /** RF-01: Identificación del solicitante */
    @Column(name = "identificacion_solicitante", nullable = false)
    private String identificacionSolicitante;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioResponsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioridad_id")
    private Prioridad prioridad;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitud> historial;

    /**
     * Crea una entrada en el historial de la solicitud
     *
     * @param estado       Estado de la solicitud en el momento del evento
     * @param accion       Tipo de acción realizada
     * @param responsable  Usuario responsable de la acción
     * @param observacion  Descripción detallada de lo realizado
     */
    public void crearHistorial(EstadoSolicitud estado, TipoAccion accion, Usuario responsable, String observacion) {
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
