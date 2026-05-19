package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.Data;

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
    /** ID unico del usuario */
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

    public Long getId() {
        return id;
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
     * DOCENTE y ADMINISTRATIVO pueden atender solicitudes asignadas.
     *
     * @return true si el usuario esta activo y tiene rol DOCENTE o ADMINISTRATIVO
     */
    public boolean puedeAtenderSolicitud() {
        return activo && (rol.equals(Rol.DOCENTE) || rol.equals(Rol.ADMINISTRATIVO));
    }

    public boolean puedeConsultarSolicitudes() {
        return activo && (rol == Rol.COORDINADOR ||
                rol == Rol.DOCENTE ||
                rol == Rol.ADMINISTRATIVO ||
                rol == Rol.ESTUDIANTE);
    }

    // SE QUE NO PUEDO PONER GETS ASI COMO ASI YA QUE QUEDA COMO ANEMICO
    public String getIdentificacion() {
        return identificacion;
    }

    // metodo interno reutilizable (no duplicar lógica)
    private void validarRol(Rol rolRequerido, String accion) {
        if (this.activo == null || !this.activo) {
            throw new IllegalArgumentException(
                    "El usuario debe estar activo para " + accion + ".");
        }

        if (this.rol != rolRequerido) {
            throw new IllegalArgumentException(
                    "Acceso denegado: no tiene permisos para " + accion +
                            ". Rol actual: " + this.rol +
                            ", Rol requerido: " + rolRequerido);
        }
    }

    public void validarPuedeRegistrarSolicitud() {
        if (this.activo == null || !this.activo ||
                (this.rol != Rol.ESTUDIANTE && this.rol != Rol.ADMINISTRATIVO)) {

            throw new IllegalArgumentException(
                    "El usuario no puede registrar solicitudes. " +
                            "Debe estar activo y tener rol ESTUDIANTE o ADMINISTRATIVO. " +
                            "Rol actual: " + this.rol);
        }
    }

    

    public void validarPuedeConsultarSolicitudes() {
        if (!puedeConsultarSolicitudes()) {
            throw new IllegalArgumentException(
                    "El usuario no tiene permisos para consultar solicitudes. Rol actual: " + rol);
        }
    }

    public void validarPuedeAtenderSolicitud() {
        if (!puedeAtenderSolicitud()) {
            throw new IllegalArgumentException(
                    "Acceso denegado: no tiene permisos para atender solicitudes. Rol actual: " + this.rol);
        }
    }

    public void validarPuedePriorizar() {
        validarRol(Rol.COORDINADOR, "priorizar solicitudes");
    }

    public void validarPuedeCerrarSolicitud() {
        validarRol(Rol.COORDINADOR, "cerrar solicitudes");
    }

}
