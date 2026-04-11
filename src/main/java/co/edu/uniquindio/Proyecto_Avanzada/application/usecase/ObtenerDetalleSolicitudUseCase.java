package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;

@Service
@RequiredArgsConstructor
public class ObtenerDetalleSolicitudUseCase {
    private final IRepositorioSolicitud repository;

    public Solicitud ejecutar(Long codigoSolicitud) {
        return repository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe una solicitud con codigo: " + codigoSolicitud));
    }
}
