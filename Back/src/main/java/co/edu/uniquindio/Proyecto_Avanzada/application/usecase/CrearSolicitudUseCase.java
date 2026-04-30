package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import jakarta.transaction.Transactional;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.RegistroSolicitudesService;

@Service
@RequiredArgsConstructor
@Transactional
public class CrearSolicitudUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final RegistroSolicitudesService dominio;

    public Solicitud ejecutar(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, String identificacionSolicitante) {
        Usuario solicitante = usuarioRepository.obtenerUsuarioIdentificacion(identificacionSolicitante);
        if (solicitante == null) {
            throw new IllegalArgumentException("Usuario no encontrado con identificación: " + identificacionSolicitante);
        }
        // hace falta la de crear solicitud con todo 
        Solicitud solicitud = dominio.registrarSolicitud(solicitante, tipo, descripcion, canalOrigen);
        solicitudRepository.guardarSolicitud(solicitud);
        return solicitud;
    }
}
