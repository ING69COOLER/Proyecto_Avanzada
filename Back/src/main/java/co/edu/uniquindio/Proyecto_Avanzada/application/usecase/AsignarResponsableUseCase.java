package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.AtencionSolicitudesService;
import jakarta.transaction.Transactional;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignarResponsableUseCase {
    private final IRepositorioSolicitud solicitudRepository;
    private final IRepositorioUsuario usuarioRepository;
    private final AtencionSolicitudesService dominio;

    public Solicitud ejecutar(Long codigoSolicitud, String coordinadorId, String responsableId, String observacion) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
                
        Usuario coordinador = usuarioRepository.obtenerUsuarioIdentificacion(coordinadorId);
        Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(responsableId);
        
        if (coordinador == null || responsable == null) {
            throw new IllegalArgumentException("Usuario coordinador o responsable no encontrados");
        }
        
        String observacionFinal = observacion + " Responsable asignado: " + responsable.getIdentificacion();
        Solicitud solicitudAsignada = dominio.asignarResponsable(coordinador, responsable, solicitud, observacionFinal);
        
        solicitudRepository.guardarSolicitud(solicitudAsignada);
        return solicitudAsignada; // Actualizar en el repositorio
    }
}
