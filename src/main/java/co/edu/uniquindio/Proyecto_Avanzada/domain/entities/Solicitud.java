package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import lombok.Data;
import lombok.Getter;

/**
 * RF-01: Entidad central que representa una solicitud academica registrada en
 * el sistema.
 *
 * Almacena los datos minimos requeridos: tipo, descripcion, canal de origen,
 * fecha/hora de registro e identificacion del solicitante.
 *
 * Gestiona ademas el ciclo de vida completo (RF-04): REGISTRADA -> CLASIFICADA
 * -> EN_ATENCION -> ATENDIDA -> CERRADA.
 *
 * RF-06: Mantiene un historial auditable de todas las acciones realizadas.
 * RF-13: Valida que cada operacion sea ejecutada por un usuario con el rol
 * correcto.
 */

@Data
@Getter
public class Solicitud {
    // le deje codigo de solicitud ya que no se considera como tal un id
    private Long codigo;

    /** RF-01: Tipo de solicitud (registro, homologacion, cancelacion, etc.) */
    private TipoSolicitud tipo;

    /** RF-01: Descripcion detallada de lo que solicita el estudiante */
    private String descripcion;

    /** RF-01: Canal por el que ingreso la solicitud (CSU, correo, SAC, etc.) */
    private CanalOrigen canalOrigen;

    /** RF-01: Momento exacto en que se registro la solicitud */
    private LocalDateTime fechaHoraRegistro;

    /** RF-08: Fecha y hora en que se cerro la solicitud */
    private LocalDateTime fechaCierre;

    /** RF-04: Estado actual dentro del ciclo de vida de la solicitud */
    private EstadoSolicitud estado;

    /** RF-05: Usuario solicitante que origino la solicitud */
    private Usuario usuarioSolicitante;

    /** RF-03: Nivel de prioridad asignado y su justificacion */
    private Prioridad prioridad;

    /**
     * RF-06: Historial auditable con todas las acciones realizadas sobre la
     * solicitud
     */
    private List<HistorialSolicitud> historial;

    /**
     * RF-01: Constructor principal para registrar una nueva solicitud academica.
     *
     * Valida que los campos obligatorios esten presentes y registra automaticamente
     * la primera entrada en el historial con estado REGISTRADA.
     *
     * @param tipo              Tipo de solicitud (no puede ser nulo)
     * @param descripcion       Texto descriptivo (no puede ser nulo ni vacio)
     * @param canalOrigen       Canal de ingreso (no puede ser nulo)
     * @param fechaHoraRegistro Timestamp del registro (no puede ser nulo)
     * @param identificacion    Identificacion del solicitante (no puede ser nula ni
     *                          vacia)
     * @throws IllegalArgumentException Si alguno de los campos obligatorios es
     *                                  invalido
     */
    public Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro, LocalDateTime fechaCierre,
            EstadoSolicitud estado, Usuario usuarioSolicitante, Prioridad prioridad) {

        // RF-01: validar que todos los campos obligatorios esten presentes
        if (tipo == null || descripcion == null || descripcion.isBlank()
                || canalOrigen == null || fechaHoraRegistro == null
                || usuarioSolicitante.getIdentificacion() == null || usuarioSolicitante.getIdentificacion().isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar al menos: tipo de solicitud, descripcion, " +
                            "canal de origen, fecha/hora de registro e identificacion del solicitante.");
        }

        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.fechaCierre = fechaCierre;
        this.estado = EstadoSolicitud.REGISTRADA; // RF-04: estado inicial del ciclo de vida
        this.usuarioSolicitante = usuarioSolicitante;
        this.prioridad = prioridad;
        this.historial = new ArrayList<>();

        // RF-06: registrar la primera entrada del historial al crear la solicitud
        crearHistoria(EstadoSolicitud.REGISTRADA, TipoAccion.CREACION, usuarioSolicitante, descripcion);
    }

    /**
     * RF-04 / RF-08: Valida que la solicitud no este cerrada antes de permitir
     * modificaciones.
     * Una solicitud cerrada no puede ser modificada (restriccion del RF-08).
     *
     * @throws SolicitudException Si la solicitud ya esta en estado CERRADA
     */
    private void validarNoEsterrada() throws SolicitudException {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new SolicitudException("No se puede modificar una solicitud cerrada");
        }
    }

    /**
     * RF-06: Registra una nueva entrada en el historial auditable de la solicitud.
     *
     * Cada accion realizada sobre la solicitud queda registrada con: fecha/hora,
     * estado en ese momento, accion realizada, usuario responsable y observacion.
     *
     * @param estado      Estado de la solicitud al momento del evento
     * @param accion      Tipo de accion realizada (CREACION, CLASIFICADA,
     *                    ASIGNACION, etc.)
     * @param responsable Usuario que ejecuto la accion
     * @param observacion Descripcion de lo realizado
     * @throws IllegalArgumentException Si el responsable o la observacion son
     *                                  nulos/vacios
     */
    private void crearHistoria(EstadoSolicitud estado, TipoAccion accion,
            Usuario responsable, String observacion) {

        HistorialSolicitud entrada = new HistorialSolicitud(estado, accion, responsable, observacion, this);
        this.historial.add(entrada);
    }

    /**
     * RF-02: Clasifica la solicitud asignandole un tipo especifico.
     * RF-04: Transiciona el estado de la solicitud a CLASIFICADA.
     * RF-13: Solo usuarios con rol COORDINADOR pueden clasificar (RN2).
     *
     * @param tipoSolicitud Nuevo tipo de solicitud asignado
     * @param usuario       Usuario que realiza la clasificacion (debe ser
     *                      COORDINADOR)
     * @param observacion   Justificacion de la clasificacion
     * @throws SolicitudException       Si la solicitud esta cerrada
     * @throws IllegalArgumentException Si el usuario no tiene permisos de
     *                                  clasificacion
     */

    /*
    {
  "tipoSolicitud": "REGISTRO_ASIGNATURA",
  "usuarioIdentificacion": "string",
  "observacion": "string"
}
    */
    public void clasificarSolicitud(TipoSolicitud tipoSolicitud, Usuario usuario,
            String observacion) throws SolicitudException {
        // RF-08: no se puede modificar una solicitud cerrada
        validarNoEsterrada();

        if (this.estado != EstadoSolicitud.REGISTRADA) {
            throw new SolicitudException(
                    "La solicitud solo puede clasificarse cuando esta en estado REGISTRADA. Estado actual: "
                            + this.estado);
        }

        if (tipoSolicitud == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!usuario.puedeAsignar()) {
            throw new SolicitudException("Acceso denegado: solo el COORDINADOR puede clasificar solicitudes.");
        }
        // RF-04: transicion de estado
        this.estado = EstadoSolicitud.CLASIFICADA;
        // RF-02: asignar el nuevo tipo de solicitud
        this.tipo = tipoSolicitud;

        // RF-06: registrar en el historial la accion de clasificacion
        this.crearHistoria(EstadoSolicitud.CLASIFICADA, TipoAccion.CLASIFICADA, usuario, observacion);
    }

    /**
     * RF-03: Asigna una prioridad a la solicitud con base en nivel y justificacion.
     * 
     *
     * @param prioridad     Nivel de prioridad (ALTA, MEDIA, BAJA)
     * @param justificacion Razon por la que se asigna dicha prioridad
     * @throws SolicitudException       Si la solicitud esta cerrada
     * @throws IllegalArgumentException Si el nivel de prioridad es nulo
     */
    public void priorizarSolicitud(NivelPrioridad prioridad,
            String justificacion) throws SolicitudException {
        validarNoEsterrada();
        if (this.estado != EstadoSolicitud.CLASIFICADA) {
            throw new SolicitudException(
                    "La prioridad solo puede asignarse en estados CLASIFICADA o EN_ATENCION. Estado actual: "
                            + this.estado);
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("El nivel de prioridad no puede ser nulo");
        }
        this.prioridad = new Prioridad(prioridad, justificacion);
    }

    /**
     * RF-04 / RF-05: Asigna un responsable a la solicitud y transiciona al estado
     * EN_ATENCION.
     * RF-13: Solo el COORDINADOR puede asignar responsables.
     * RF-06: Registra la asignacion en el historial.
     *
     * @param user        Usuario que asigna (debe ser COORDINADOR)
     * @param descripcion Observacion de la asignacion
     * @throws SolicitudException Si la solicitud esta cerrada o el rol no es valido
     */
    public void asignarResponsable(Usuario user, String descripcion) throws SolicitudException {
        validarNoEsterrada();
        if (user == null || !user.puedeAsignar()) {
            throw new SolicitudException(
                    "Acceso denegado: solo el COORDINADOR puede asignar responsables." +
                            (user != null ? " Rol actual: " + user.getRol() : ""));
        }
        if (!this.estado.equals(EstadoSolicitud.CLASIFICADA)) {
            throw new SolicitudException(
                    "para poder asignar un responsable, la solicitud debe de estar en estado de clasificada"
                           );
        }
        // RF-04: transicion de estado a EN_ATENCION
        this.estado = EstadoSolicitud.EN_ATENCION;
        // RF-06: registrar la asignacion en el historial
        this.crearHistoria(EstadoSolicitud.EN_ATENCION, TipoAccion.ASIGNACION, user, descripcion);
    }

    /**
     * RF-05: Verifica si un usuario puede atender esta solicitud.
     * Confirma que el usuario haya participado previamente en el historial.
     *
     * @param user Usuario a verificar
     * @return true si el usuario aparece en el historial de la solicitud
     */
    

    public List<Usuario> obtenerUsuariosDeHistorias(){
        List<Usuario> usuarios = new LinkedList<>();
        for (HistorialSolicitud historialSolicitud : historial) {
            usuarios.add(historialSolicitud.getResponsable());
        }
        return usuarios;
    }

    /**
     * RF-04: Transiciona la solicitud al estado ATENDIDA.
     * RF-13: Solo el DOCENTE puede marcar una solicitud como atendida.
     * RF-06: Registra la atencion en el historial.
     *
     * @param user        Usuario que atiende (debe ser DOCENTE)
     * @param observacion Descripcion de lo realizado
     * @throws SolicitudException Si la solicitud esta cerrada o el rol no es valido
     */
    public void atenderSolicitud(Usuario user, String observacion) throws SolicitudException {
        validarNoEsterrada();
        // RF-13: verificar que el usuario sea DOCENTE
        if (user == null || !user.puedeAtenderSolicitud()) {
            throw new SolicitudException(
                    "Acceso denegado: solo el DOCENTE puede atender solicitudes." +
                            (user != null ? " Rol actual: " + user.getRol() : ""));
        }
        // verifica que el que esta encargado de resolverla la resuelva y no cualquiera
        if (historial.stream().filter((historial) -> historial.getResponsable().getIdentificacion().equals(user.getIdentificacion()) && historial.getEstado().equals(EstadoSolicitud.EN_ATENCION)).toList().isEmpty()) {
            throw new SolicitudException(
                    "Acceso denegado: solo el asignado como responsable de responder solicitud puede atenderla" +
                            (user != null ? " Rol actual: " + user.getRol() : ""));
        }
        if (!this.estado.equals(EstadoSolicitud.EN_ATENCION)) {
            throw new SolicitudException(
                    "la solicitud debe de estar en atencion antes de ser atendida" +
                            (user != null ? " Rol actual: " + user.getRol() : ""));
        }
        // RF-04: transicion de estado a ATENDIDA
        this.estado = EstadoSolicitud.ATENDIDA;
        // RF-06: registrar la atencion en el historial
        this.crearHistoria(EstadoSolicitud.ATENDIDA, TipoAccion.CAMBIO_ESTADO, user, observacion);
    }

    /**
     * RF-08: Cierra formalmente la solicitud, finalizando su ciclo de vida.
     * RF-04: Transiciona al estado CERRADA (estado final, sin retorno).
     * RF-13: Solo el COORDINADOR puede cerrar solicitudes.
     * RF-06: Registra el cierre en el historial.
     *
     * Condiciones previas (RF-08):
     * - La solicitud debe estar en estado ATENDIDA
     * - Se debe proporcionar una observacion de cierre
     * - Una vez cerrada, la solicitud no puede ser modificada
     *
     * @param user        Usuario que cierra (debe ser COORDINADOR activo)
     * @param observacion Observacion obligatoria de cierre
     * @throws SolicitudException Si no se cumplen las condiciones de cierre o el
     *                            rol es invalido
     */
    public void cerrarSolicitud(Usuario user, String observacion) throws SolicitudException {
        // RF-08: la solicitud debe estar en estado ATENDIDA para poder cerrarla
        if (this.estado != EstadoSolicitud.ATENDIDA) {
            throw new SolicitudException(
                    "La solicitud debe estar en estado ATENDIDA para ser cerrada. " +
                            "Estado actual: " + this.estado);
        }
        if (user == null) {
            throw new SolicitudException("El usuario no puede ser nulo, debe de haber un responsable");
        }
        if (!user.puedeAsignar()) {
            throw new SolicitudException(
                    "Acceso denegado: solo el COORDINADOR puede cerrar solicitudes." +
                            " Rol actual: " + user.getRol());
        }
        // RF-08: la observacion de cierre es obligatoria
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new SolicitudException("Debe proporcionar una observacion de cierre");
        }

        if (estado == EstadoSolicitud.CERRADA) {
            throw new IllegalArgumentException("La solicitud ya esta cerrada");
        }

        // RF-04: transicion al estado final CERRADA
        this.estado = EstadoSolicitud.CERRADA;
        this.fechaCierre = LocalDateTime.now();
        // RF-06: registrar el cierre en el historial
        this.crearHistoria(EstadoSolicitud.CERRADA, TipoAccion.CIERRE, user, observacion);
    }
}
