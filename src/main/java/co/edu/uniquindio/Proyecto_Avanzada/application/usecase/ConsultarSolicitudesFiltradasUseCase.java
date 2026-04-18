package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
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
    private final ConsultarSolicitudesPorEstadoUseCase consultarSolicitudesPorEstadoUseCase;
    private final ConsultarSolicitudesPorTipoUseCase consultarSolicitudesPorTipoUseCase;
    private final ConsultarSolicitudesPorResponsableUseCase consultarSolicitudesPorResponsableUseCase;
    private final ConsultarSolicitudesPorPrioridadUseCase consultarSolicitudesPorPrioridadUseCase;
    // se podran listar por consulta mas compleja de jpa
    public Page<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsableAtencion, NivelPrioridad nivelPrioridad, String identificacionResponsableAccion, Pageable pageable) {
        Pageable pageableEfectivo = pageable != null ? pageable : PageRequest.of(0, 10);
        dominio.consultasValidacion(usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAccion));
        //
        boolean sinFiltros = estado == null
                && tipo == null
                && (identificacionResponsableAtencion == null || identificacionResponsableAtencion.isBlank())
                && nivelPrioridad == null;

        if (sinFiltros) {
            return repository.listar(pageableEfectivo);
        }

        List<Solicitud> solicitudes = repository.listar();
        //consultar solicitudes por caso de uso, mejor buscarlas en la db en vez de guevonear con esto,
        //implementar solo verificacion en el caso de uso
        if (estado != null) {
            List<Solicitud> solicitudesPorEstado = consultarSolicitudesPorEstadoUseCase.ejecutar(estado, identificacionResponsableAccion);
            Set<Long> codigosPorEstado = solicitudesPorEstado.stream()
                .map(Solicitud::getCodigo)
                .collect(Collectors.toSet());
            solicitudes = solicitudes.stream()
                .filter(s -> codigosPorEstado.contains(s.getCodigo()))
                    .toList();
        }
        // lo mismo que de la anterior
        if (tipo != null) {
            List<Solicitud> solicitudesPorTipo = consultarSolicitudesPorTipoUseCase.ejecutar(tipo, identificacionResponsableAccion);
            Set<Long> codigosPorTipo = solicitudesPorTipo.stream()
                .map(Solicitud::getCodigo)
                .collect(Collectors.toSet());
            solicitudes = solicitudes.stream()
                .filter(s -> codigosPorTipo.contains(s.getCodigo()))
                    .toList();
        }
        // lo mismo que la anterior 
        if (identificacionResponsableAtencion != null && !identificacionResponsableAtencion.isBlank()) {
            List<Solicitud> solicitudesPorResponsable = consultarSolicitudesPorResponsableUseCase
                .ejecutar(identificacionResponsableAtencion);
            Set<Long> codigosPorResponsable = solicitudesPorResponsable.stream()
                .map(Solicitud::getCodigo)
                .collect(Collectors.toSet());
            solicitudes = solicitudes.stream()
                .filter(s -> codigosPorResponsable.contains(s.getCodigo()))
                .toList();
        }

        if (nivelPrioridad != null) {
            List<Solicitud> solicitudesPorPrioridad = consultarSolicitudesPorPrioridadUseCase.ejecutar(nivelPrioridad);
            Set<Long> codigosPorPrioridad = solicitudesPorPrioridad.stream()
                .map(Solicitud::getCodigo)
                .collect(Collectors.toSet());
            solicitudes = solicitudes.stream()
                .filter(s -> codigosPorPrioridad.contains(s.getCodigo()))
                    .toList();
        }

        int total = solicitudes.size();
        int fromIndex = Math.min((int) pageableEfectivo.getOffset(), total);
        int toIndex = Math.min(fromIndex + pageableEfectivo.getPageSize(), total);
        List<Solicitud> pagina = solicitudes.subList(fromIndex, toIndex);

        return new PageImpl<>(pagina, pageableEfectivo, total);
    }
}
