package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;

    private String nombre;

    private String identificacion;

    private String correo;

    private Boolean activo;

    private Rol rol;

    public boolean puedeRegistrarSolicitud() {
        return activo && (rol.equals(Rol.ESTUDIANTE) || rol.equals(Rol.ADMINISTRATIVO));
    }

    // RN2
    public boolean puedeClasificarSolicitud() {
        return activo && (rol.equals(Rol.COORDINADOR));
    }

    public boolean puedeCerrarSolicitud() {
        return activo && rol.equals(Rol.COORDINADOR);
    }

    //RN3
    public boolean puedePriorizar(){
        return activo && rol.equals(Rol.COORDINADOR);
    }

    //puede asignarResponsable

    //puede atenderSolicitud

}
