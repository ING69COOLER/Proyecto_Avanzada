package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.application.usecase.RegistrarSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.UserException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.interfaces.IUserRule;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements IUserRule {

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

    @OneToMany(mappedBy = "usuarioResponsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes = new ArrayList<>();

    /**
     * RF-01: Registra una nueva solicitud académica.
     * 
     * @param tipo              Tipo de solicitud
     * @param descripcion       Descripción de la solicitud
     * @param canalOrigen       Canal de origen
     * @param fechaHoraRegistro Fecha y hora de registro
     * @param prioridad         Prioridad de la solicitud
     */
    @Override
    public void registrarSolicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro, Prioridad prioridad) throws UserException {

        new RegistrarSolicitud().registrar_solicitud(tipo, descripcion, canalOrigen,
                fechaHoraRegistro, prioridad, this);
    }

}
