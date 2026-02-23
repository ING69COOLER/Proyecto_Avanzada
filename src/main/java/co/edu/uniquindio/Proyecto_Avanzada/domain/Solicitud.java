package co.edu.uniquindio.Proyecto_Avanzada.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private LocalDateTime fechaHoraRegistro;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    private String justificacionPrioridad;

    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario creador;

    @ManyToOne
    @JoinColumn(name = "prioridad_id")
    private Prioridad prioridad;

    @ManyToOne
    @JoinColumn(name = "canal_origen_id")
    private CanalOrigen canalOrigen;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitud> historiales;
}
