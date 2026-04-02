package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.AtencionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;

@Service
public class AtencionSolicitudesApplicationService {

    private AtencionSolicitudesService dominio;
    private IRepositorioSolicitud repositorio;

    public AtencionSolicitudesApplicationService() {
        this.dominio = new AtencionSolicitudesService();
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

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
}
