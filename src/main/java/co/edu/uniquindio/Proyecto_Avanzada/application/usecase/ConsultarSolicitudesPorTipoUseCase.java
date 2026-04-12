package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesPorTipoUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;
    private final ConsultaSolicitudesService dominio;
    // falta verificacion de identidad
    public List<Solicitud> ejecutar(TipoSolicitud tipo, String identificacion) {
        dominio.consultasValidacion(usuarioRepository.obtenerUsuarioIdentificacion(identificacion));
        return repository.consultarTipoSolicitud(tipo);
    }
}
