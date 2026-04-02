package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.CierreSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;

@Service
public class CierreSolicitudApplicationService {

    private CierreSolicitudService dominio;
    private IRepositorioSolicitud repositorio;

    public CierreSolicitudApplicationService() {
        this.dominio = new CierreSolicitudService();
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

    public Solicitud cerrarSolicitud(Usuario user, Solicitud solicitud, String observacion) throws SolicitudException {

        Solicitud solicitudCerrada = dominio.cerrarSolicitud(user, solicitud, observacion);
        repositorio.guardarSolicitud(solicitudCerrada);
        return solicitudCerrada;
    }
}