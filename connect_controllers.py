import os
import re

base_usecase_dir = r"c:\Users\Olimpica\Documents\Manuel\Semestre VI\Avanzada\Proyecto_Avanzada\src\main\java\co\edu\uniquindio\Proyecto_Avanzada\application\usecase"
base_controller_dir = r"c:\Users\Olimpica\Documents\Manuel\Semestre VI\Avanzada\Proyecto_Avanzada\src\main\java\co\edu\uniquindio\Proyecto_Avanzada\infrastructure\inbound\controllers"

# 1. Update UseCases to return Solicitud instead of void
files_to_update = ["AsignarResponsableUseCase.java", "CambiarEstadoUseCase.java", "CerrarSolicitudUseCase.java", "ClasificarSolicitudUseCase.java", "PriorizarSolicitudUseCase.java"]

for fname in files_to_update:
    path = os.path.join(base_usecase_dir, fname)
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    
    content = content.replace("public void ejecutar(", "public Solicitud ejecutar(")
    # Need to return the saved solicitud
    # Find the line that guards/updates
    content = re.sub(r"solicitudRepository\.guardarSolicitud\((.*?)\);", r"solicitudRepository.guardarSolicitud(\1);\n        return \1;", content)
    
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

# 2. Create missing use cases
with open(os.path.join(base_usecase_dir, "ObtenerDetalleSolicitudUseCase.java"), "w", encoding="utf-8") as f:
    f.write("""package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

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
""")

with open(os.path.join(base_usecase_dir, "ConsultarSolicitudesFiltradasUseCase.java"), "w", encoding="utf-8") as f:
    f.write("""package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Objects;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesFiltradasUseCase {
    private final IRepositorioSolicitud repository;
    private final IRepositorioUsuario usuarioRepository;

    public List<Solicitud> ejecutar(EstadoSolicitud estado, TipoSolicitud tipo, String identificacionResponsable) {
        List<Solicitud> solicitudes = repository.listar();

        if (estado != null) {
            solicitudes = solicitudes.stream()
                    .filter(s -> Objects.equals(s.getEstado(), estado))
                    .toList();
        }

        if (tipo != null) {
            solicitudes = solicitudes.stream()
                    .filter(s -> Objects.equals(s.getTipo(), tipo))
                    .toList();
        }

        if (identificacionResponsable != null && !identificacionResponsable.isBlank()) {
            Usuario responsable = usuarioRepository.obtenerUsuarioIdentificacion(identificacionResponsable);
            if(responsable != null) {
                solicitudes = solicitudes.stream()
                        .filter(solicitud -> solicitud.obtenerUsuariosDeHistorias().stream()
                                .anyMatch(usuario -> usuario != null
                                        && Objects.equals(usuario.getIdentificacion(), responsable.getIdentificacion())))
                        .toList();
            }
        }

        return solicitudes;
    }
}
""")

# 3. Rewrite SolicitudesController
controller_code = """package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

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
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) String identificacionResponsable) {

        List<Solicitud> solicitudes = consultarSolicitudesFiltradasUseCase.ejecutar(estado, tipo, identificacionResponsable);
        return ResponseEntity.ok(SolicitudResponseMapper.toResumenResponseList(solicitudes));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<SolicitudDetalleResponse> obtenerDetalleSolicitud(@PathVariable Long codigo) {
        Solicitud solicitud = obtenerDetalleSolicitudUseCase.ejecutar(codigo);
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @PatchMapping("/{codigo}/clasificacion")
    public ResponseEntity<SolicitudDetalleResponse> clasificarSolicitud(
            @PathVariable Long codigo,
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
            @PathVariable Long codigo,
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
            @PathVariable Long codigo,
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
            @PathVariable Long codigo,
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
            @PathVariable Long codigo,
            @Valid @RequestBody CerrarSolicitudRequest request) throws SolicitudException {

        Solicitud solicitud = cerrarSolicitudUseCase.ejecutar(
                codigo,
                request.identificacionUsuario(),
                request.observacionCierre());
        return ResponseEntity.ok(SolicitudResponseMapper.toDetalleResponse(solicitud));
    }

    @GetMapping("/{codigo}/historial")
    public ResponseEntity<List<EventoHistorialResponse>> consultarHistorialSolicitud(@PathVariable Long codigo) {
        Solicitud solicitud = obtenerDetalleSolicitudUseCase.ejecutar(codigo);
        return ResponseEntity.ok(SolicitudResponseMapper.toHistorialResponseList(solicitud.getHistorial()));
    }
}
"""
with open(os.path.join(base_controller_dir, "SolicitudesController.java"), "w", encoding="utf-8") as f:
    f.write(controller_code)

# 4. Rewrite UsuariosController
users_controller_code = """package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.UsuarioResumenDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.usecase.RegistrarUsuarioUseCase;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResumenDTO> registrarUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = new Usuario(
                null,
                request.nombre(),
                request.identificacion(),
                request.correo(),
                request.activo(),
                request.rol());

        Usuario usuarioRegistrado = registrarUsuarioUseCase.ejecutar(usuario);
        UsuarioResumenDTO response = SolicitudResponseMapper.toUsuarioResumenDTO(usuarioRegistrado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
"""
with open(os.path.join(base_controller_dir, "UsuariosController.java"), "w", encoding="utf-8") as f:
    f.write(users_controller_code)

print("Controllers successfully connected to the modern Use Cases.")
