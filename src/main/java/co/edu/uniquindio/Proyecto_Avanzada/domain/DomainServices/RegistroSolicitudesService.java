package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import org.springframework.beans.factory.annotation.Autowired;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
/*
Solicitud(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad) */
//RN1
@Service
public class RegistroSolicitudesService {

    private IRepositorioSolicitud repositorioSolicitud;
    
    public RegistroSolicitudesService() {
        this.repositorioSolicitud = RepositorioSolicitud.getInstancia();
    }
    
    // metodo para crear solicitud incluyendo reglas validadas en usuario
    public void registrarSolicitudBasica(Usuario responsableCreacion, TipoSolicitud tipo, 
                                    String descripcion, CanalOrigen canalOrigen, 
                                    LocalDateTime fechaHoraRegisro, String identificacion){

        if(!responsableCreacion.puedeRegistrarSolicitud()){
            throw new IllegalArgumentException("EL usuario es invalido");
        }

        Solicitud solicitud = new Solicitud(tipo, descripcion, canalOrigen, 
                    fechaHoraRegisro,  null,  null,
                    responsableCreacion,  null);

        repositorioSolicitud.guardarSolicitud(solicitud);

    }

    public void registrarSolicitudCompleta(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, 
                    LocalDateTime fechaHoraRegisro, String identificacion, LocalDateTime fechaCierre, EstadoSolicitud estado,
                    Usuario usuarioSolicitante, Prioridad prioridad){

        if(!usuarioSolicitante.puedeRegistrarSolicitud()){
            throw new IllegalArgumentException("EL usuario es invalido");
        }
        Solicitud solicitud = new Solicitud(tipo, descripcion, canalOrigen, 
                    fechaHoraRegisro,  null,  null,
                    null,  null);

        repositorioSolicitud.guardarSolicitud(solicitud);


    }










}
