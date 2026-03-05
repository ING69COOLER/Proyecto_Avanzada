package co.edu.uniquindio.Proyecto_Avanzada.domain.interfaces;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.UserException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

public interface IUserRule {
    void registrarSolicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro, Prioridad prioridad) throws UserException;
}
