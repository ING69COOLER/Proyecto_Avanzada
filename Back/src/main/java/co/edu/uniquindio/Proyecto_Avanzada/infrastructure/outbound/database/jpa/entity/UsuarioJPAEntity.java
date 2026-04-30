package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa un Usuario en la base de datos.
 * Solo existe en la capa de infraestructura. El dominio nunca la ve.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;

    @Column(nullable = false, length = 200)
    private String correo;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Rol rol;
}
