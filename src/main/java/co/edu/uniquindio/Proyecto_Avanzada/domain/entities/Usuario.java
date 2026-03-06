package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.UserException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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


    public boolean puedeRegistrarSolicitud() {
        return activo && (rol.equals(Rol.ESTUDIANTE) || rol.equals(Rol.ADMINISTRATIVO));
    }

    public boolean puedeClasificarSolicitud() {
        return activo && (rol.equals(Rol.COORDINADOR) || rol.equals(Rol.ADMINISTRATIVO));
    }

    public boolean puedeCerrarSolicitud() {
        return activo && rol.equals(Rol.COORDINADOR);
    }

}
