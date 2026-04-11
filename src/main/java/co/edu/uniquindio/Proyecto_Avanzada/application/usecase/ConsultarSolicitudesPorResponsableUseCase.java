package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesPorResponsableUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;
    private final ConsultaSolicitudesService dominio;

    public List<Solicitud> ejecutar(String identificacionResponsable) {
        Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsable);
        if (responsable == null) {
            throw new IllegalArgumentException("Usuario responsable no encontrado");
        }
        
        List<Solicitud> todas = repository.listar();
        return dominio.consultarPorResponsable(todas, responsable);
    }
}
