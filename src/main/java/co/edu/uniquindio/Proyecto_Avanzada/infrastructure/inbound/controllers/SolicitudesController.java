package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.PriorizarSolicitudRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudResumenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.RequestCommandMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.AtencionSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.CierreSolicitudApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.ClasificacionSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.ConsultaSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.PriorizacionApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.RegistroSolicitudesApplicationService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudesController {

    private final RegistroSolicitudesApplicationService registroSolicitudesApplicationService;
    private final ConsultaSolicitudesApplicationService consultaSolicitudesApplicationService;
    private final ClasificacionSolicitudesApplicationService clasificacionSolicitudesApplicationService;
    private final PriorizacionApplicationService priorizacionApplicationService;
    private final AtencionSolicitudesApplicationService atencionSolicitudesApplicationService;
    private final CierreSolicitudApplicationService cierreSolicitudApplicationService;

    @PostMapping
    public ResponseEntity<SolicitudDetalleResponse> crearSolicitud(@Valid @RequestBody CrearSolicitudRequest request) {
        SolicitudDetalleResponse response = registroSolicitudesApplicationService
                .registrarSolicitud(RequestCommandMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResumenResponse>> consultarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) String identificacionResponsable) {

        List<SolicitudResumenResponse> response = consultaSolicitudesApplicationService
                .consultarResumen(estado, tipo, identificacionResponsable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<SolicitudDetalleResponse> obtenerDetalleSolicitud(@PathVariable Long codigo) {
        return ResponseEntity.ok(consultaSolicitudesApplicationService.obtenerDetalle(codigo));
    }

    @PatchMapping("/{codigo}/clasificacion")
    public ResponseEntity<SolicitudDetalleResponse> clasificarSolicitud(
            @PathVariable Long codigo,
            @Valid @RequestBody ClasificarSolicitudRequest request) throws SolicitudException {

        SolicitudDetalleResponse response = clasificacionSolicitudesApplicationService
                .clasificarSolicitud(codigo, RequestCommandMapper.toCommand(request));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{codigo}/prioridad")
    public ResponseEntity<SolicitudDetalleResponse> priorizarSolicitud(
            @PathVariable Long codigo,
            @Valid @RequestBody PriorizarSolicitudRequest request) throws SolicitudException {

        SolicitudDetalleResponse response = priorizacionApplicationService
                .priorizarSolicitud(codigo, RequestCommandMapper.toCommand(request));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{codigo}/asignacion")
    public ResponseEntity<SolicitudDetalleResponse> asignarResponsableSolicitud(
            @PathVariable Long codigo,
            @Valid @RequestBody AsignarResponsableRequest request) throws SolicitudException {

        SolicitudDetalleResponse response = atencionSolicitudesApplicationService
                .asignarResponsable(codigo, RequestCommandMapper.toCommand(request));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{codigo}/estado")
    public ResponseEntity<SolicitudDetalleResponse> cambiarEstadoSolicitud(
            @PathVariable Long codigo,
            @Valid @RequestBody CambiarEstadoRequest request) throws SolicitudException {

        SolicitudDetalleResponse response = atencionSolicitudesApplicationService
                .cambiarEstado(codigo, RequestCommandMapper.toCommand(request));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{codigo}/cierre")
    public ResponseEntity<SolicitudDetalleResponse> cerrarSolicitud(
            @PathVariable Long codigo,
            @Valid @RequestBody CerrarSolicitudRequest request) throws SolicitudException {

        SolicitudDetalleResponse response = cierreSolicitudApplicationService
                .cerrarSolicitud(codigo, RequestCommandMapper.toCommand(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{codigo}/historial")
    public ResponseEntity<List<EventoHistorialResponse>> consultarHistorialSolicitud(@PathVariable Long codigo) {
        SolicitudDetalleResponse detalle = consultaSolicitudesApplicationService.obtenerDetalle(codigo);
        return ResponseEntity.ok(detalle.historial());
    }
}
