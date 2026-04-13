package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
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
    public Page<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsableAtencion, NivelPrioridad nivelPrioridad, String identificacionResponsableAccion, Pageable pageable) {
        Pageable pageableEfectivo = pageable != null ? pageable : PageRequest.of(0, 10);
        dominio.consultasValidacion(usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAccion));

        boolean sinFiltros = estado == null
                && tipo == null
                && (identificacionResponsableAtencion == null || identificacionResponsableAtencion.isBlank())
                && nivelPrioridad == null;

        if (sinFiltros) {
            return repository.listar(pageableEfectivo);
        }

        List<Solicitud> solicitudes = nivelPrioridad != null
                ? consultarSolicitudesPorPrioridadUseCase.ejecutar(nivelPrioridad)
                : repository.listar();

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
            solicitudes = solicitudes.stream()
                    .filter(solicitud -> solicitud.getPrioridad() != null
                            && Objects.equals(solicitud.getPrioridad().nivel(), nivelPrioridad))
                    .toList();
        }

        int total = solicitudes.size();
        int fromIndex = Math.min((int) pageableEfectivo.getOffset(), total);
        int toIndex = Math.min(fromIndex + pageableEfectivo.getPageSize(), total);
        List<Solicitud> pagina = solicitudes.subList(fromIndex, toIndex);

        return new PageImpl<>(pagina, pageableEfectivo, total);
    }
}
