package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ClasificacionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
public class ClasificarSolicitudUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final ClasificacionSolicitudesService dominio;

    public Solicitud ejecutar(Long codigoSolicitud, String identificacionUsuario, TipoSolicitud tipoSolicitud, String justificacion) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
        Usuario usuario = usuarioRepository.obtenerUsuarioIdentificacion(identificacionUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Solicitud solicitudClasificada = dominio.clasificarSolicitud(solicitud, tipoSolicitud, usuario, justificacion);
        solicitudRepository.guardarSolicitud(solicitudClasificada);
        return solicitudClasificada;
    }
}
