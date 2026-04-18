package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ResumenSolicitudService;
import jakarta.transaction.Transactional;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerarResumenSolicitudUseCase {
    private final IRepositorioSolicitud repository;
    private final ResumenSolicitudService dominio;

    public String ejecutar(Long codigoSolicitud) throws SolicitudException {
        Solicitud solicitud = repository.obtenerPorId(codigoSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe solicitud con código: " + codigoSolicitud));
        
        return dominio.generarResumenSolicitud(solicitud);
    }
}
