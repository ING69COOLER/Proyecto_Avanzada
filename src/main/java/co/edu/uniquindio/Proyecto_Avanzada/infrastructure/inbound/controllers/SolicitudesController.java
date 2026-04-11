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
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.usecase.*;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudesController {

    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final ConsultarSolicitudesFiltradasUseCase consultarSolicitudesFiltradasUseCase;
    private final ObtenerDetalleSolicitudUseCase obtenerDetalleSolicitudUseCase;
    private final ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    private final PriorizarSolicitudUseCase priorizarSolicitudUseCase;
    private final AsignarResponsableUseCase asignarResponsableUseCase;
    private final CambiarEstadoUseCase cambiarEstadoUseCase;
    private final CerrarSolicitudUseCase cerrarSolicitudUseCase;

    @PostMapping
    public ResponseEntity<SolicitudDetalleResponse> crearSolicitud(@Valid @RequestBody CrearSolicitudRequest request) {
        Solicitud solicitud = crearSolicitudUseCase.ejecutar(
                request.tipoSolicitud(),
                request.descripcion(),
                request.canalOrigen(),
                request.identificacionSolicitante());
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResumenResponse>> consultarSolicitudes(
            @RequestParam(name="estado", required = false) EstadoSolicitud estado,
            @RequestParam(name="tipo", required = false) TipoSolicitud tipo,
            @RequestParam(name="identificacionResponsable", required = false) String identificacionResponsable) {

        List<Solicitud> solicitudes = consultarSolicitudesFiltradasUseCase.ejecutar(estado, tipo, identificacionResponsable);
        return ResponseEntity.ok(SolicitudResponseMapper.toResumenResponseList(solicitudes));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<SolicitudDetalleResponse> obtenerDetalleSolicitud(@PathVariable("codigo") Long codigo) {
        Solicitud solicitud = obtenerDetalleSolicitudUseCase.ejecutar(codigo);
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/clasificacion")
    public ResponseEntity<SolicitudDetalleResponse> clasificarSolicitud(
            @PathVariable("codigo") Long codigo,
            @Valid @RequestBody ClasificarSolicitudRequest request) throws SolicitudException {

        Solicitud solicitud = clasificarSolicitudUseCase.ejecutar(
                codigo,
                request.identificacionUsuario(),
                request.tipoSolicitud(),
                request.observacion());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/prioridad")
    public ResponseEntity<SolicitudDetalleResponse> priorizarSolicitud(
            @PathVariable("codigo") Long codigo,
            @Valid @RequestBody PriorizarSolicitudRequest request) throws SolicitudException {

        Solicitud solicitud = priorizarSolicitudUseCase.ejecutar(
                codigo,
                request.identificacionUsuario(),
                request.nivelPrioridad(),
                request.justificacion());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/asignacion")
    public ResponseEntity<SolicitudDetalleResponse> asignarResponsableSolicitud(
            @PathVariable("codigo") Long codigo,
            @Valid @RequestBody AsignarResponsableRequest request) throws SolicitudException {

        Solicitud solicitud = asignarResponsableUseCase.ejecutar(
                codigo,
                request.identificacionCoordinador(),
                request.identificacionResponsable(),
                request.observacion());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/estado")
    public ResponseEntity<SolicitudDetalleResponse> cambiarEstadoSolicitud(
            @PathVariable("codigo") Long codigo,
            @Valid @RequestBody CambiarEstadoRequest request) throws SolicitudException {

        Solicitud solicitud = cambiarEstadoUseCase.ejecutar(
                codigo,
                request.identificacionUsuario(),
                request.nuevoEstado(),
                request.observacion());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/cierre")
    public ResponseEntity<SolicitudDetalleResponse> cerrarSolicitud(
            @PathVariable("codigo") Long codigo,
            @Valid @RequestBody CerrarSolicitudRequest request) throws SolicitudException {

        Solicitud solicitud = cerrarSolicitudUseCase.ejecutar(
                codigo,
                request.identificacionUsuario(),
                request.observacionCierre());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @GetMapping("/{codigo}/historial")
    public ResponseEntity<List<EventoHistorialResponse>> consultarHistorialSolicitud(@PathVariable("codigo") Long codigo) {
        Solicitud solicitud = obtenerDetalleSolicitudUseCase.ejecutar(codigo);
        return ResponseEntity.ok(SolicitudResponseMapper.toHistorialResponseList(solicitud.getHistorial()));
    }
}
