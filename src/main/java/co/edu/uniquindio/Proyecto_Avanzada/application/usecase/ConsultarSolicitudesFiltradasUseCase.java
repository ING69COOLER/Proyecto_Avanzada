package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Objects;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
// consulta las solicitudes filtradas por tipo, estado, JAJAJAJJJAAJJAJAJ ESTA ES LA UNICA QUE SE USA
public class ConsultarSolicitudesFiltradasUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;
    private final ConsultaSolicitudesService dominio;
    private final ConsultarSolicitudesPorPrioridadUseCase consultarSolicitudesPorPrioridadUseCase;
    // se podran listar por consulta mas compleja de jpa
    public List<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsableAtencion, NivelPrioridad nivelPrioridad, String identificacionResponsableAccion) {
        dominio.consultasValidacion(usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAccion));
        List<Solicitud> solicitudes = repository.listar();

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

        if (identificacionResponsableAtencion != null && !identificacionResponsableAtencion.isBlank()) {
            Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAtencion);
            if(responsable != null) {
                solicitudes = solicitudes.stream()
                        .filter(solicitud -> solicitud.obtenerUsuariosDeHistorias().stream()
                                .anyMatch(usuario -> usuario != null
                                        && Objects.equals(usuario.getIdentificacion(), responsable.getIdentificacion())))
                        .toList();
            }
        }

        if (nivelPrioridad != null) {
            consultarSolicitudesPorPrioridadUseCase.ejecutar(nivelPrioridad);
        }

        return solicitudes;
    }
}
