package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.AsignarResponsableCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.command.CambiarEstadoCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.AtencionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;

@Service
@RequiredArgsConstructor
public class AtencionSolicitudesApplicationService {

    private final AtencionSolicitudesService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    

    public Solicitud asignarResponsable(Usuario user, Solicitud solicitud, String descripcion) throws SolicitudException {
        Solicitud solicitudAsignada = dominio.asignarResponsable(user, solicitud, descripcion); // actualmente devuelve void en el dominio, pero mantendremos dans
        repositorio.guardarSolicitud(solicitudAsignada);
        return solicitudAsignada;
    }

    public Solicitud atenderSolicitud(Usuario user, Solicitud solicitud, String observacion) throws SolicitudException {
        Solicitud solicitudAtendida = dominio.atenderSolicitud(user, solicitud, observacion); // actualmente void, adaptamos later
        repositorio.guardarSolicitud(solicitudAtendida);
        return solicitudAtendida;
    }

    public SolicitudDetalleResponse asignarResponsable(Long codigoSolicitud, AsignarResponsableCommand command)
            throws SolicitudException {
        Solicitud solicitud = obtenerSolicitud(codigoSolicitud);
        Usuario coordinador = obtenerUsuario(command.identificacionCoordinador());
        Usuario responsableAsignado = obtenerUsuario(command.identificacionResponsable());

        String observacion = "%s Responsable asignado: %s".formatted(
                command.observacion(),
                responsableAsignado.getIdentificacion());

        Solicitud solicitudAsignada = dominio.asignarResponsable(coordinador, solicitud, observacion);
        return SolicitudResponseMapper.toDetalleResponse(solicitudAsignada);
    }

    public SolicitudDetalleResponse cambiarEstado(Long codigoSolicitud, CambiarEstadoCommand command)
            throws SolicitudException {
        Solicitud solicitud = obtenerSolicitud(codigoSolicitud);
        Usuario usuario = obtenerUsuario(command.identificacionUsuario());

        if (command.nuevoEstado() != EstadoSolicitud.ATENDIDA) {
            throw new IllegalArgumentException(
                    "La operacion cambiarEstado solo soporta actualmente la transicion a ATENDIDA");
        }

        Solicitud solicitudAtendida = dominio.atenderSolicitud(usuario, solicitud, command.observacion());
        return SolicitudResponseMapper.toDetalleResponse(solicitudAtendida);
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
