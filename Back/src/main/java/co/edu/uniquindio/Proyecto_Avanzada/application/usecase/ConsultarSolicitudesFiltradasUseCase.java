package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
// consulta las solicitudes filtradas por tipo, estado, JAJAJAJJJAAJJAJAJ ESTA ES LA UNICA QUE SE USA
public class ConsultarSolicitudesFiltradasUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;
    private final ConsultaSolicitudesService dominio;

    public Page<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsableAtencion, NivelPrioridad nivelPrioridad, String identificacionResponsableAccion, Pageable pageable) {
        Pageable pageableEfectivo = pageable != null ? pageable : PageRequest.of(0, 10);
        Usuario usuarioAccion = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAccion);
        dominio.consultasValidacion(usuarioAccion);

        if (estado != null) {
            return repository.consultarEstado(estado, pageableEfectivo);
        }

        if (tipo != null) {
            return repository.consultarTipoSolicitud(tipo, pageableEfectivo);
        }

        if (identificacionResponsableAtencion != null && !identificacionResponsableAtencion.isBlank()) {
            Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsableAtencion);
            if (responsable == null) {
                throw new IllegalArgumentException("Usuario responsable no encontrado");
            }
            return repository.consultarResponsable(responsable, pageableEfectivo);
        }

        if (nivelPrioridad != null) {
            return repository.consultarPorNivelPrioridad(nivelPrioridad, pageableEfectivo);
        }

            return repository.listar(pageableEfectivo);
    }
}
