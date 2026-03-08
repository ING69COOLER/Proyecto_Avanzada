package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;


import lombok.Data;


import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;

@Data

// no se porque una observacion
public class HistorialSolicitud {

    private Long id;

    private LocalDateTime fechaHora;

    private String observacion;

    private EstadoSolicitud estado;

    private TipoAccion accion;

    private Usuario responsable;

    private Solicitud solicitud;

    public Usuario obtenerUsuario(){
        return responsable;
    }

}
