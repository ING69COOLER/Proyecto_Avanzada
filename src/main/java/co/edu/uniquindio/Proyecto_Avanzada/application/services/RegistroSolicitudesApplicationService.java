package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.RegistroSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

@Service
public class RegistroSolicitudesApplicationService {

    private RegistroSolicitudesService dominio;
    private IRepositorioSolicitud repositorio;

    public RegistroSolicitudesApplicationService() {
        this.dominio = new RegistroSolicitudesService();
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

    public Solicitud registrarSolicitudBasica(Usuario responsableCreacion, TipoSolicitud tipo, 
                                    String descripcion, CanalOrigen canalOrigen){

        Solicitud solicitud = dominio.registrarSolicitudBasica(responsableCreacion, tipo, descripcion, canalOrigen);
        repositorio.guardarSolicitud(solicitud);
        return solicitud;
    }

    public Solicitud registrarSolicitudCompleta(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad){

        Solicitud solicitud = dominio.registrarSolicitudCompleta(tipo, descripcion, canalOrigen, fechaCierre, estado, usuarioSolicitante, prioridad);
        repositorio.guardarSolicitud(solicitud);
        return solicitud;
    }
}