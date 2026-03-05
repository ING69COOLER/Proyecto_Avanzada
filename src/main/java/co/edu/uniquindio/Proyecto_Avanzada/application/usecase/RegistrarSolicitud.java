package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import java.time.LocalDateTime;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.UserException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import lombok.RequiredArgsConstructor;

/**
 * RF-01: Caso de uso para registrar una nueva solicitud académica.
 */
@RequiredArgsConstructor
public class RegistrarSolicitud {

        private final RepositorioSolicitud repositorioSolicitud;




    public void registrarSolicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen,
            LocalDateTime fechaHoraRegistro, Prioridad prioridad, Usuario usuarioResponsable) throws UserException {
        if (tipo == null || descripcion == null || descripcion.isBlank()
                || canalOrigen == null || fechaHoraRegistro == null
                || usuarioResponsable.getIdentificacion() == null) {
            throw new UserException(
                    "Debe proporcionar al menos: tipo de solicitud, descripción, " +
                            "canal de origen, fecha/hora de registro e identificación del solicitante.");
        }

        Solicitud nuevaSolicitud = Solicitud.builder()
                .tipo(tipo)
                .descripcion(descripcion)
                .canalOrigen(canalOrigen)
                .fechaHoraRegistro(fechaHoraRegistro)
                .identificacionSolicitante(usuarioResponsable.getIdentificacion())
                .estado(EstadoSolicitud.REGISTRADA)
                .usuarioResponsable(usuarioResponsable)
                .prioridad(prioridad)
                .build();

        repositorioSolicitud.agregar(nuevaSolicitud);
    }

}
