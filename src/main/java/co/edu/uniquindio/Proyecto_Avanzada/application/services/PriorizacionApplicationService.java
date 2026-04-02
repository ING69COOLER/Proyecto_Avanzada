package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.PriorizacionService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;

@Service
public class PriorizacionApplicationService {

    private PriorizacionService dominio;
    private IRepositorioSolicitud repositorio;

    public PriorizacionApplicationService() {
        this.dominio = new PriorizacionService();
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

    public Solicitud priorizarSolicitud(Usuario usuario, String justificacion,
            Solicitud solicitud, NivelPrioridad prioridad) throws SolicitudException {

        Solicitud solicitudPrioridad = dominio.priorizarSolicitud(usuario, justificacion, solicitud, prioridad);
        repositorio.guardarSolicitud(solicitudPrioridad);
        return solicitudPrioridad;
    }
}