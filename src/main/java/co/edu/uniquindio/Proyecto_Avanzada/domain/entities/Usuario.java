package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "identificacion", nullable = false, unique = true)
    private String identificacion;
    
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;
}
