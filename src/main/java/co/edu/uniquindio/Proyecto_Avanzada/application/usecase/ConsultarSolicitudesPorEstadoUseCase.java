package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ConsultaSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultarSolicitudesPorEstadoUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario users;
    private final ConsultaSolicitudesService dominio;
    // falta vefificacion de identidad
    public List<Solicitud> ejecutar(EstadoSolicitud estado, String identificacion) {
        dominio.consultasValidacion(users.obtenerUsuarioIdentificacion(identificacion));
        return repository.consultarEstado(estado);
    }
}
