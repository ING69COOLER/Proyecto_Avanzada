package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "usuarioResponsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes = new ArrayList<>();

    /**
     * RF-01: Registra una nueva solicitud académica para este usuario.
     * Campos obligatorios: tipo, descripción, canal de origen, fecha/hora de
     * registro
     * e identificación del solicitante (tomada del propio usuario).
     *
     * @param tipo              Tipo de solicitud académica
     * @param descripcion       Descripción de la solicitud
     * @param canalOrigen       Canal por el que ingresó la solicitud
     * @param fechaHoraRegistro Fecha y hora exacta del registro
     * @param prioridad         Prioridad asignada (opcional)
     * @return La solicitud creada, o null si faltan datos obligatorios
     */
    public Solicitud registrarSolicitud(TipoSolicitud tipo,
            String descripcion,
            CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro,
            Prioridad prioridad) {

        // RF-01: validar campos obligatorios
        if (tipo == null || descripcion == null || descripcion.isBlank()
                || canalOrigen == null || fechaHoraRegistro == null
                || this.identificacion == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar al menos: tipo de solicitud, descripción, " +
                            "canal de origen, fecha/hora de registro e identificación del solicitante.");
        }

        Solicitud nuevaSolicitud = Solicitud.builder()
                .tipo(tipo)
                .descripcion(descripcion)
                .canalOrigen(canalOrigen)
                .fechaHoraRegistro(fechaHoraRegistro)
                .identificacionSolicitante(this.identificacion)
                .estado(EstadoSolicitud.REGISTRADA)
                .usuarioResponsable(this)
                .prioridad(prioridad)
                .build();

        solicitudes.add(nuevaSolicitud);
        return nuevaSolicitud;
    }

    /**
     * Método original conservado para compatibilidad — usa registrarSolicitud
     * internamente
     */
    public void crearSolicitud(String descripcion,
            LocalDateTime fechaHoraRegistro,
            LocalDateTime fechaCierre,
            TipoSolicitud tipo,
            Usuario usuarioResponsable,
            Prioridad prioridad,
            CanalOrigen canalOrigen) {
        registrarSolicitud(tipo, descripcion, canalOrigen, fechaHoraRegistro, prioridad);
    }

    /**
     * Establece el nivel de prioridad según tipo de solicitud y fecha límite.
     */
    public NivelPrioridad establecePrioridad(TipoSolicitud tipo, LocalDateTime fechaCierre) {

        double razonPrioridadTipo = 1.0 / ((double) (tipo.ordinal() + 1) / TipoSolicitud.values().length);
        double razonPrioridadTiempo = 10.0 / ChronoUnit.DAYS.between(LocalDateTime.now(), fechaCierre);
        double razonTotal = razonPrioridadTiempo + razonPrioridadTipo;

        if (razonTotal >= 8)
            return NivelPrioridad.ALTA;
        else if (razonTotal >= 3)
            return NivelPrioridad.MEDIA;
        else
            return NivelPrioridad.BAJA;
    }
}
