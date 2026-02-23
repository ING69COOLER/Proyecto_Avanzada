package co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prioridades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prioridad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "descripcion", nullable = false)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false)
    private NivelPrioridad nivel;
}
