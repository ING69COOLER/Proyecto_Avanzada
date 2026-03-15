package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;

/**
 * RF-01 / RF-13: Entidad que representa a un usuario del sistema academico.
 *
 * Almacena la informacion basica del usuario y define los permisos segun su rol
 * (RF-13: autorizacion basica de operaciones). Los roles disponibles son:
 * - ESTUDIANTE: puede registrar solicitudes
 * - COORDINADOR: puede clasificar, priorizar, asignar y cerrar solicitudes
 * - DOCENTE: puede atender solicitudes
 * - ADMINISTRATIVO: puede registrar solicitudes
 */
@Data
public class Usuario {

    private Long id;

    /** Nombre completo del usuario */
    private String nombre;

    /** Numero de identificacion del usuario */
    private String identificacion;

    /** Correo electronico del usuario */
    private String correo;

    /** RF-05: El responsable debe estar activo para poder ser asignado */
    private Boolean activo;

    /** RF-13: Rol que determina que operaciones puede ejecutar el usuario */
    private Rol rol;

    public Usuario(Long id, String nombre, String identificacion, String correo, Boolean activo, Rol rol) {
        if (identificacion == null || identificacion.isBlank() || identificacion.isEmpty()) {
            throw new IllegalArgumentException("la identificacion no puede ser vacia ni nula");
        }
        this.id = id;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.correo = correo;
        this.activo = activo;
        this.rol = rol;
    }

    /**
     * RF-01 / RF-13: Verifica si el usuario puede registrar solicitudes.
     * Solo los usuarios con rol ESTUDIANTE o ADMINISTRATIVO pueden registrar.
     *
     * @return true si el usuario esta activo y tiene rol ESTUDIANTE o
     *         ADMINISTRATIVO
     */

    

    public boolean puedeRegistrarSolicitud() {
        return activo && (rol.equals(Rol.ESTUDIANTE) || rol.equals(Rol.ADMINISTRATIVO));
    }

    /**
     * RF-02 / RF-13: Verifica si el usuario puede clasificar solicitudes (RN2).
     * Solo el COORDINADOR tiene permiso para clasificar.
     *
     * @return true si el usuario esta activo y tiene rol COORDINADOR
     */
    public boolean puedeClasificarSolicitud() {
        return activo && (rol.equals(Rol.COORDINADOR));
    }

    /**
     * RF-08 / RF-13: Verifica si el usuario puede cerrar solicitudes.
     * Solo el COORDINADOR puede formalizar el cierre de una solicitud.
     *
     * @return true si el usuario esta activo y tiene rol COORDINADOR
     */
    public boolean puedeCerrarSolicitud() {
        return activo && rol.equals(Rol.COORDINADOR);
    }

    /**
     * RF-03 / RF-13: Verifica si el usuario puede priorizar solicitudes (RN3).
     * Solo el COORDINADOR puede asignar prioridad a una solicitud.
     *
     * @return true si el usuario esta activo y tiene rol COORDINADOR
     */
    public boolean puedePriorizar() {
        return activo && rol.equals(Rol.COORDINADOR);
    }

    /**
     * RF-05 / RF-13: Verifica si el usuario puede asignar responsables.
     * Solo el COORDINADOR puede asignar solicitudes a otros usuarios.
     *
     * @return true si el usuario esta activo y tiene rol COORDINADOR
     */
    public boolean puedeAsignar() {
        return activo && rol.equals(Rol.COORDINADOR);
    }

    /**
     * RF-04 / RF-13: Verifica si el usuario puede atender (resolver) solicitudes.
     * Solo el DOCENTE puede marcar una solicitud como atendida.
     *
     * @return true si el usuario esta activo y tiene rol DOCENTE
     */
    public boolean puedeAtenderSolicitud() {
        return activo && rol.equals(Rol.DOCENTE);
    }
}
