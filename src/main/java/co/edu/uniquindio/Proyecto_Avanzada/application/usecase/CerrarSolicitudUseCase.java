package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.CierreSolicitudService;
import jakarta.transaction.Transactional;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

@Service
@RequiredArgsConstructor
@Transactional
public class CerrarSolicitudUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final CierreSolicitudService dominio;

    public Solicitud ejecutar(Long codigoSolicitud, String identificacionUsuario, String observacion) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
                
        Usuario usuario = usuarioRepository.obtenerUsuarioIdentificacion(identificacionUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + identificacionUsuario);
        }
        
        Solicitud solicitudCerrada = dominio.cerrarSolicitud(usuario, solicitud, observacion);
        solicitudRepository.guardarSolicitud(solicitudCerrada);
        return solicitudCerrada;
    }
}
