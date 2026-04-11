package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudResumenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
public class ConsultaSolicitudesApplicationService {

    private final IRepositorioSolicitud repositorio;
    private final IRepositorioUsuario repositorioUsuario;
    private final ConsultaSolicitudesService dominio;

    

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

    public List<SolicitudResumenResponse> consultarResumenPorEstado(EstadoSolicitud estado) {
        return SolicitudResponseMapper.toResumenResponseList(consultarPorEstado(estado));
    }

    public List<SolicitudResumenResponse> consultarResumenPorTipo(TipoSolicitud tipo) {
        return SolicitudResponseMapper.toResumenResponseList(consultarPorTipo(tipo));
    }

    public List<SolicitudResumenResponse> consultarResumenPorPrioridad(Prioridad prioridad) {
        return SolicitudResponseMapper.toResumenResponseList(consultarPorPrioridad(prioridad));
    }

    public List<SolicitudResumenResponse> consultarResumenPorResponsable(String identificacionResponsable) {
        Usuario responsable = obtenerUsuario(identificacionResponsable);
        return SolicitudResponseMapper.toResumenResponseList(consultarPorResponsable(responsable));
    }

    public List<SolicitudResumenResponse> consultarResumen(EstadoSolicitud estado,
            TipoSolicitud tipo,
            String identificacionResponsable) {
        List<Solicitud> solicitudes = repositorio.listar();

        if (estado != null) {
            solicitudes = solicitudes.stream()
                    .filter(s -> Objects.equals(s.getEstado(), estado))
                    .toList();
        }

        if (tipo != null) {
            solicitudes = solicitudes.stream()
                    .filter(s -> Objects.equals(s.getTipo(), tipo))
                    .toList();
        }

        if (identificacionResponsable != null && !identificacionResponsable.isBlank()) {
            Usuario responsable = obtenerUsuario(identificacionResponsable);
            solicitudes = solicitudes.stream()
                    .filter(solicitud -> solicitud.obtenerUsuariosDeHistorias().stream()
                            .anyMatch(usuario -> usuario != null
                                    && Objects.equals(usuario.getIdentificacion(), responsable.getIdentificacion())))
                    .toList();
        }

        return SolicitudResponseMapper.toResumenResponseList(solicitudes);
    }

    public SolicitudDetalleResponse obtenerDetalle(Long codigoSolicitud) {
        Solicitud solicitud = repositorio.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una solicitud con codigo: " + codigoSolicitud));
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
