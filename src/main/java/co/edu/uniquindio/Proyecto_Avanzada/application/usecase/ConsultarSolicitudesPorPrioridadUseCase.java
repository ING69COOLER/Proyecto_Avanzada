package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesPorPrioridadUseCase {
    private final IRepositorioSolicitud repository;

    public List<Solicitud> ejecutar(Prioridad prioridad) {
        return repository.consultarPrioridad(prioridad);
    }
}
