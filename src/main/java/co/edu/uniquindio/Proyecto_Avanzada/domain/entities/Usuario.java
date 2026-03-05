package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
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
     * 
     * 
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
     * 
     * RF-03. Priorización de solicitudes
     * El sistema debe asignar una prioridad a cada solicitud con base en reglas
     * definidas,
     *
     * @param nivel         Nivel de prioridad (NivelPrioridad)
     * @param justificacion Justificación o descripción de la prioridad asignada
     * @return El objeto Prioridad creado
     */
    public Prioridad asignarPrioridad(NivelPrioridad nivel, String justificacion) {
        if (nivel == null || justificacion == null || justificacion.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar el nivel de prioridad y una justificación.");
        }

        Prioridad prioridad = new Prioridad();
        prioridad.setNivel(nivel);
        prioridad.setDescripcion(justificacion);

        return prioridad;
    }

    /**
     * RF-02 : El sistema debe permitir clasificar una solicitud académica según su
     * tipo
     *
     * @param solicitud Solicitud a clasificar
     * @param tipo      Tipo de solicitud a asignar
     */
    public void clasificarSolicitud(Solicitud solicitud, TipoSolicitud tipo, String observacion) {

        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo.");
        }
        if (!Boolean.TRUE.equals(this.activo)) {
            throw new IllegalStateException("El usuario no está activo y no puede clasificar solicitudes.");
        }
        if (this.rol == Rol.ESTUDIANTE) {
            throw new IllegalStateException("Un estudiante no tiene permisos para clasificar solicitudes.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.REGISTRADA) {
            throw new IllegalStateException(
                    "Solo se pueden clasificar solicitudes registradas.");
        }

        solicitud.setTipo(tipo);
        solicitud.setEstado(EstadoSolicitud.CLASIFICADA);
        solicitud.crearHistorial(EstadoSolicitud.CLASIFICADA, TipoAccion.CAMBIO_ESTADO, this, observacion);
    }

    /**
     * Atiende una solicitud académica cambiando su estado a EN_ATENCION
     * Requiere que el usuario esté activo y que no sea estudiante
     *
     * @param solicitud   Solicitud a atender
     * @param observacion Descripción detallada de la acción
     */
    public void inicioAtencionSolicitud(Solicitud solicitud, String observacion) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (!Boolean.TRUE.equals(this.activo)) {
            throw new IllegalStateException("El usuario no está activo y no puede atender solicitudes.");
        }
        if (this.rol == Rol.ESTUDIANTE) {
            throw new IllegalStateException("Un estudiante no tiene permisos para atender solicitudes.");
        }
        if (!solicitud.getHistorial().stream()
                .anyMatch(h -> h.getEstado() == EstadoSolicitud.CLASIFICADA)) {
            throw new IllegalStateException("La solicitud no ha sido clasificada.");
        }
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);
        solicitud.crearHistorial(EstadoSolicitud.EN_ATENCION, TipoAccion.CAMBIO_ESTADO, this, observacion);
    }

    /**
     * Finaliza la atención de una solicitud académica cambiando su estado a
     * ATENDIDA
     * Requiere que el usuario esté activo y que no sea estudiante
     *
     * @param solicitud   Solicitud a finalizar
     * @param observacion Descripción detallada de la acción
     */
    public void finalizarAtencionSolicitud(Solicitud solicitud, String observacion) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (!Boolean.TRUE.equals(this.activo)) {
            throw new IllegalStateException("El usuario no está activo y no puede finalizar solicitudes.");
        }
        if (this.rol == Rol.ESTUDIANTE) {
            throw new IllegalStateException("Un estudiante no tiene permisos para finalizar solicitudes.");
        }
        if (!solicitud.getHistorial().stream()
                .anyMatch(h -> h.getEstado() == EstadoSolicitud.EN_ATENCION)) {
            throw new IllegalStateException("La solicitud no esta siendo atendida.");
        }
        solicitud.setEstado(EstadoSolicitud.ATENDIDA);
        solicitud.crearHistorial(EstadoSolicitud.ATENDIDA, TipoAccion.CAMBIO_ESTADO, this, observacion);
    }

    /**
     * Cierra una solicitud académica cambiando su estado a CERRADA
     * Requiere que el usuario esté activo y que no sea estudiante
     *
     * @param solicitud   Solicitud a cerrar
     * @param observacion Descripción detallada de la acción
     */
    public void cerrarSolicitud(Solicitud solicitud, String observacion) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (!Boolean.TRUE.equals(this.activo)) {
            throw new IllegalStateException("El usuario no está activo y no puede cerrar solicitudes.");
        }
        if (this.rol == Rol.ESTUDIANTE) {
            throw new IllegalStateException("Un estudiante no tiene permisos para cerrar solicitudes.");
        }
        if (!solicitud.getHistorial().stream()
                .anyMatch(h -> h.getEstado() == EstadoSolicitud.ATENDIDA)) {
            throw new IllegalStateException("La solicitud no se ha acabado de atender.");
        }

        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitud.crearHistorial(EstadoSolicitud.CERRADA, TipoAccion.CAMBIO_ESTADO, this, observacion);
    }
}
