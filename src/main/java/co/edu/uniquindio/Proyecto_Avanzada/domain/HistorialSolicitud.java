package co.edu.uniquindio.Proyecto_Avanzada.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiales_solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    private TipoAccion accion;

    private String observacion;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;
}
