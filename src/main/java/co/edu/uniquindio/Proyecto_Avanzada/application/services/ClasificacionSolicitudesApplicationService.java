package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.ClasificarSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ClasificacionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
public class ClasificacionSolicitudesApplicationService {

    private final ClasificacionSolicitudesService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    public ClasificacionSolicitudesApplicationService() {
        this.dominio = new ClasificacionSolicitudesService();
        this.repositorio = RepositorioSolicitud.getInstancia();
        this.repositorioUsuario = RepositorioUsuario.getInstancia();
    }

    public Solicitud clasificarSolicitud(Solicitud solicitud, TipoSolicitud tipoSolicitud,
            Usuario usuario, String observacion) throws SolicitudException {

        Solicitud solicitudClasificada = dominio.clasificarSolicitud(solicitud, tipoSolicitud, usuario, observacion);
        repositorio.guardarSolicitud(solicitudClasificada);
        return solicitudClasificada;
    }

    public SolicitudDetalleResponse clasificarSolicitud(Long codigoSolicitud, ClasificarSolicitudCommand command)
            throws SolicitudException {
        Solicitud solicitud = obtenerSolicitud(codigoSolicitud);
        Usuario usuario = obtenerUsuario(command.identificacionUsuario());
        Solicitud solicitudClasificada = dominio.clasificarSolicitud(
                solicitud,
                command.tipoSolicitud(),
                usuario,
                command.observacion());
        return SolicitudResponseMapper.toDetalleResponse(solicitudClasificada);
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
