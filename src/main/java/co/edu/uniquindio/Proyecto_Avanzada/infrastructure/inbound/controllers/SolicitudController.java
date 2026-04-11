package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import co.edu.uniquindio.Proyecto_Avanzada.application.services.ConsultaSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    // Dependemos de la interfaz del caso de uso o de ApplicationService directamente
    private final ConsultaSolicitudesApplicationService consultaService;

    @GetMapping("/responsable/{identificacion}")
    public ResponseEntity<List<Solicitud>> listarPorResponsable(@PathVariable String identificacion) {
        try {
            // Ejemplo de llamada a la capa de aplicación
            List<Solicitud> solicitudes = consultaService.consultarPorResponsable(identificacion);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
