package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.PriorizacionService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;

@Service
@RequiredArgsConstructor
public class PriorizarSolicitudUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final PriorizacionService dominio;

    public Solicitud ejecutar(Long codigoSolicitud, String identificacionUsuario, NivelPrioridad prioridad, String justificacion) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
        Usuario usuario = usuarioRepository.obtenerUsuarioIdentificacion(identificacionUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Solicitud solicitudPriorizada = dominio.priorizarSolicitud(usuario, justificacion, solicitud, prioridad);
        solicitudRepository.guardarSolicitud(solicitudPriorizada);
        return solicitudPriorizada;
    }
}
