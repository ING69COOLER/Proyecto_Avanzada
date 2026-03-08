package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.Data;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RF-01: Entidad que representa una solicitud académica registrada en el
 * sistema.
 * Almacena: tipo, descripción, canal de origen, fecha/hora de registro e
 * identificación del solicitante.
 */
@Data
public class Solicitud {

    private Long id;

    /** RF-01: Tipo de solicitud */
    private TipoSolicitud tipo;

    /** RF-01: Descripción de la solicitud */
    private String descripcion;

    /** RF-01: Canal de origen (CSU, correo, SAC, telefónico, etc.) */
    private CanalOrigen canalOrigen;

    /** RF-01: Fecha y hora de registro */
    private LocalDateTime fechaHoraRegistro;

    /** RF-01: Identificación del solicitante */
    private String identificacionSolicitante;

    private LocalDateTime fechaCierre;

    private EstadoSolicitud estado;

    private Usuario usuarioSolicitante;

    private Prioridad prioridad;

    private List<HistorialSolicitud> historial;


    // creo que prioridad, fechacierre y usuarioSolicitante no deben de meterse aunque mmm creo que eso va en el servicio de dominio
    public Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
            Usuario usuarioSolicitante, Prioridad prioridad) {
        // RF-01: validar campos obligatorios
        if (tipo == null || descripcion == null || descripcion.isBlank()
                || canalOrigen == null || fechaHoraRegistro == null
                || identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar al menos: tipo de solicitud, descripción, " +
                            "canal de origen, fecha/hora de registro e identificación del solicitante.");
        }

        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.identificacionSolicitante = identificacion;
        this.id = null;
        this.fechaCierre = fechaCierre;
        this.estado = EstadoSolicitud.REGISTRADA;
        this.usuarioSolicitante = usuarioSolicitante;
        this.prioridad = prioridad;
        this.historial = new ArrayList<>();

        crearHistoria(EstadoSolicitud.REGISTRADA, TipoAccion.CREACION, usuarioSolicitante, descripcion);
    }

    /**
     * Crea una entrada en el historial de la solicitud
     * 
     * jajajajajaj, ya no esta la etiqueta de los setters, esto va a petar
     *
     * @param estado      Estado de la solicitud en el momento del evento
     * @param accion      Tipo de acción realizada
     * @param responsable Usuario responsable de la acción
     * @param observacion Descripción detallada de lo realizado
     */
    private void crearHistoria(EstadoSolicitud estado, TipoAccion accion, Usuario responsable, String observacion) {
        if (responsable == null || observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("El responsable y la observación no pueden ser nulos o vacíos.");
        }

        HistorialSolicitud entrada = new HistorialSolicitud();
        entrada.setFechaHora(LocalDateTime.now());
        entrada.setEstado(estado);
        entrada.setAccion(accion);
        entrada.setResponsable(responsable);
        entrada.setSolicitud(this);
        entrada.setObservacion(observacion);

        this.historial.add(entrada);
    }
    //RN2
    public void clasificarSolicitud(TipoSolicitud tipoSolicitud, Usuario usuario, String observacion){
        if (tipoSolicitud == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!usuario.puedeClasificarSolicitud()) {
            throw new IllegalArgumentException("El usuario no tiene permisos para clasificar solicitudes");
        }
        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("El usuario no está activo");
        }
        
        this.crearHistoria(EstadoSolicitud.CLASIFICADA, TipoAccion.CLASIFICADA, usuario, observacion);
        this.estado = EstadoSolicitud.CLASIFICADA;
        this.tipo = tipoSolicitud;
    }
    //RN3
    public void priorizarSolicitud(NivelPrioridad prioridad, String justificacion){
        this.prioridad = new Prioridad(prioridad, justificacion); 
    }

    public void asignarResponsable(Usuario user, String descripcion) {
        this.estado = EstadoSolicitud.EN_ATENCION;
        this.crearHistoria(EstadoSolicitud.EN_ATENCION, TipoAccion.ASIGNACION, user, descripcion);
    }

    public boolean UsuarioPuedeAtender(Usuario user) {
        return !historial.stream().filter(h -> h.obtenerUsuario().equals(user)).toList().isEmpty();
    }

    public void atenderSolicitud(Usuario user, String observacion) {
        this.estado = EstadoSolicitud.ATENDIDA;
        this.crearHistoria(EstadoSolicitud.ATENDIDA, TipoAccion.CAMBIO_ESTADO, user, observacion);
    }

    // RF-04: Cierre de solicitud
    public void cerrarSolicitud(Usuario user, String observacion) throws SolicitudException {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new SolicitudException("La solicitud ya está cerrada");
        }
        if (user == null) {
            throw new SolicitudException("El usuario no puede ser nulo");
        }
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new SolicitudException("La observación no puede ser nula o vacía");
        }
        this.estado = EstadoSolicitud.CERRADA;
        this.fechaCierre = LocalDateTime.now();
        this.crearHistoria(EstadoSolicitud.CERRADA, TipoAccion.CIERRE, user, observacion);
    }



}
