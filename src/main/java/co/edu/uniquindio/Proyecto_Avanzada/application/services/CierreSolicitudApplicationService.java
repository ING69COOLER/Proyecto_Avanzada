package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.CerrarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.CierreSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioUsuario;

@Service
public class CierreSolicitudApplicationService {

    private final CierreSolicitudService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    public CierreSolicitudApplicationService() {
        this.dominio = new CierreSolicitudService();
        this.repositorio = RepositorioSolicitud.getInstancia();
        this.repositorioUsuario = RepositorioUsuario.getInstancia();
    }

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
