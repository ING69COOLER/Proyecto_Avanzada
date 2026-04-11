package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una Solicitud en la base de datos.
 * Mantiene separación total con el modelo de dominio.
 */
@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoSolicitud tipo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalOrigen canalOrigen;

    @Column(nullable = false)
    private LocalDateTime fechaHoraRegistro;

    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    // Relación con el usuario solicitante (embedded por simplicidad)
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_solicitante_id")
    private UsuarioJPAEntity usuarioSolicitante;

    // Prioridad como columnas embebidas
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_nivel", length = 10)
    private NivelPrioridad prioridadNivel;

    @Column(name = "prioridad_descripcion", length = 500)
    private String prioridadDescripcion;

    // Historial como colección dependiente
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<HistorialSolicitudJPAEntity> historial = new ArrayList<>();
}
