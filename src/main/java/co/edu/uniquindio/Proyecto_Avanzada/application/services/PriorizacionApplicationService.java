package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.PriorizarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.PriorizacionService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;

@Service
@RequiredArgsConstructor
public class PriorizacionApplicationService {

    private final PriorizacionService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    

    public Solicitud priorizarSolicitud(Usuario usuario, String justificacion,
            Solicitud solicitud, NivelPrioridad prioridad) throws SolicitudException {

        Solicitud solicitudPrioridad = dominio.priorizarSolicitud(usuario, justificacion, solicitud, prioridad);
        repositorio.guardarSolicitud(solicitudPrioridad);
        return solicitudPrioridad;
    }

    public SolicitudDetalleResponse priorizarSolicitud(Long codigoSolicitud, PriorizarSolicitudCommand command)
            throws SolicitudException {
        Solicitud solicitud = obtenerSolicitud(codigoSolicitud);
        Usuario usuario = obtenerUsuario(command.identificacionUsuario());
        Solicitud solicitudPriorizada = dominio.priorizarSolicitud(
                usuario,
                command.justificacion(),
                solicitud,
                command.nivelPrioridad());
        return SolicitudResponseMapper.toDetalleResponse(solicitudPriorizada);
    }

    private Solicitud obtenerSolicitud(Long codigoSolicitud) {
        return repositorio.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una solicitud con codigo: " + codigoSolicitud));
    }

    private Usuario obtenerUsuario(String identificacion) {
        Usuario usuario = repositorioUsuario.obtenerUsuarioIdentificacion(identificacion);
        if (usuario == null) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con identificacion: " + identificacion);
        }
        return usuario;
    }
}
