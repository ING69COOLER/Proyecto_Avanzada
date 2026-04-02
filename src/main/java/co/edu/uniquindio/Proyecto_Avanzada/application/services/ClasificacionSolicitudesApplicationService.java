package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ClasificacionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
public class ClasificacionSolicitudesApplicationService {

    private ClasificacionSolicitudesService dominio;
    private IRepositorioSolicitud repositorio;

    public ClasificacionSolicitudesApplicationService() {
        this.dominio = new ClasificacionSolicitudesService();
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

    public Solicitud clasificarSolicitud(Solicitud solicitud, TipoSolicitud tipoSolicitud,
            Usuario usuario, String observacion) throws SolicitudException {

        Solicitud solicitudClasificada = dominio.clasificarSolicitud(solicitud, tipoSolicitud, usuario, observacion);
        repositorio.guardarSolicitud(solicitudClasificada);
        return solicitudClasificada;
    }
}