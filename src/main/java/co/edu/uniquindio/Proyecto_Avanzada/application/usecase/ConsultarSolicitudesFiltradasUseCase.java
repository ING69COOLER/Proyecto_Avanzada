package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Objects;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesFiltradasUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;

    public List<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsable) {
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

        if (identificacionResponsable != null && !identificacionResponsable.isBlank()) {
            Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsable);
            if(responsable != null) {
                solicitudes = solicitudes.stream()
                        .filter(solicitud -> solicitud.obtenerUsuariosDeHistorias().stream()
                                .anyMatch(usuario -> usuario != null
                                        && Objects.equals(usuario.getIdentificacion(), responsable.getIdentificacion())))
                        .toList();
            }
        }

        return solicitudes;
    }
}
