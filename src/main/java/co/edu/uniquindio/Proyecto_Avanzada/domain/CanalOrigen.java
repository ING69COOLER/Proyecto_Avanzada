package co.edu.uniquindio.Proyecto_Avanzada.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "canales_origen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanalOrigen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "canalOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes;
}
