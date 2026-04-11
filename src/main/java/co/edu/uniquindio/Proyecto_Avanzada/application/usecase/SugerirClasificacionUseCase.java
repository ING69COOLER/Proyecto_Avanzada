package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.ResumenSolicitudService;

@Service
@RequiredArgsConstructor
public class SugerirClasificacionUseCase {
    private final ResumenSolicitudService dominio;

    public String ejecutar(String descripcion) {
        return dominio.sugerirClasificacion(descripcion);
    }
}
