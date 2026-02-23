package co.edu.uniquindio.Proyecto_Avanzada.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "prioridades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prioridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NivelPrioridad nivel;

    private String descripcion;

    @OneToMany(mappedBy = "prioridad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes;
}
