package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.CerrarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.CierreSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;

@Service
@RequiredArgsConstructor
public class CierreSolicitudApplicationService {

    private final CierreSolicitudService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    

    public Solicitud cerrarSolicitud(Usuario user, Solicitud solicitud, String observacion) throws SolicitudException {

        Solicitud solicitudCerrada = dominio.cerrarSolicitud(user, solicitud, observacion);
        repositorio.guardarSolicitud(solicitudCerrada);
        return solicitudCerrada;
    }

    public SolicitudDetalleResponse cerrarSolicitud(Long codigoSolicitud, CerrarSolicitudCommand command)
            throws SolicitudException {
        Solicitud solicitud = obtenerSolicitud(codigoSolicitud);
        Usuario usuario = obtenerUsuario(command.identificacionUsuario());
        Solicitud solicitudCerrada = dominio.cerrarSolicitud(usuario, solicitud, command.observacionCierre());
        return SolicitudResponseMapper.toDetalleResponse(solicitudCerrada);
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
