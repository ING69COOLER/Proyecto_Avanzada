package co.edu.uniquindio.Proyecto_Avanzada.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private String nombre;

    private String identificacion;

    private String correo;

    private Boolean activo;

    @OneToMany(mappedBy = "creador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes;

    @OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitud> historiales;
}
