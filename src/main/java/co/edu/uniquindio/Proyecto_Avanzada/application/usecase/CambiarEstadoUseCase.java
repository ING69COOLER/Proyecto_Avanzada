package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.AtencionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;

@Service
@RequiredArgsConstructor
public class CambiarEstadoUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final AtencionSolicitudesService dominio;

    public Solicitud ejecutar(Long codigoSolicitud, String identificacionUsuario, EstadoSolicitud nuevoEstado, String observacion) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
                
        Usuario usuario = usuarioRepository.obtenerUsuarioIdentificacion(identificacionUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + identificacionUsuario);
        }
        
        // En este dominio, atender una solicitud implica cambiar su estado a ATENDIDA
        if (nuevoEstado != EstadoSolicitud.ATENDIDA) {
            throw new IllegalArgumentException("Esta operación dinámica solo soporta la transición a ATENDIDA por el momento.");
        }
        
        Solicitud solicitudAtendida = dominio.atenderSolicitud(usuario, solicitud, observacion);
        solicitudRepository.guardarSolicitud(solicitudAtendida);
        return solicitudAtendida;
    }
}
