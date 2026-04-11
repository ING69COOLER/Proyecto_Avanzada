package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.command.CrearSolicitudCommand;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.RegistroSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistroSolicitudesApplicationService {

    private final RegistroSolicitudesService dominio;
    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;

    

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

    public SolicitudDetalleResponse registrarSolicitud(CrearSolicitudCommand command) {
        Usuario solicitante = obtenerUsuario(command.identificacionSolicitante());
        Solicitud solicitud = registrarSolicitudBasica(
                solicitante,
                command.tipoSolicitud(),
                command.descripcion(),
                command.canalOrigen());
        return SolicitudResponseMapper.toDetalleResponse(solicitud);
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
