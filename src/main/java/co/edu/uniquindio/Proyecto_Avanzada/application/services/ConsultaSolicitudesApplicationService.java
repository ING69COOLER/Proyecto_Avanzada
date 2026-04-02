package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
public class ConsultaSolicitudesApplicationService {

    private IRepositorioSolicitud repositorio;
    private ConsultaSolicitudesService dominio;

    public ConsultaSolicitudesApplicationService() {
        this.repositorio = RepositorioSolicitud.getInstancia();
        this.dominio = new ConsultaSolicitudesService();
    }

    public List<Solicitud> consultarPorEstado(EstadoSolicitud estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        return repositorio.consultarEstado(estado);
    }

    public List<Solicitud> consultarPorTipo(TipoSolicitud tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        return repositorio.consultarTipoSolicitud(tipo);
    }

    public List<Solicitud> consultarPorPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
        return repositorio.consultarPrioridad(prioridad);
    }
    // deberia de hacerse por una query de sql o algo asi, ya que si lo dejamos como listas va a quedar muy ineficiente, pero por ahora lo dejo asi
    public List<Solicitud> consultarPorResponsable(Usuario responsable) {
        List<Solicitud> solicitudes = repositorio.listar();
        return dominio.consultarPorResponsable(solicitudes, responsable);
    }

}