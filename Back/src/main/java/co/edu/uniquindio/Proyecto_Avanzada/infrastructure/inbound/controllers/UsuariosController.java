package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.RolEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.UsuarioResumenDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.EnumDtoMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.usecase.RegistrarUsuarioUseCase;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.UsuarioJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository.UsuarioSpringDataRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final SolicitudResponseMapper solicitudResponseMapper;
    private final EnumDtoMapper enumDtoMapper;
    private final UsuarioSpringDataRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResumenDTO> registrarUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = new Usuario(
                null,
                request.nombre(),
                request.identificacion(),
                request.correo(),
                request.activo(),
                enumDtoMapper.toRol(request.rol()));

        Usuario usuarioRegistrado = registrarUsuarioUseCase.ejecutar(usuario, request.password());
        UsuarioResumenDTO response = solicitudResponseMapper.toUsuarioResumenDTO(usuarioRegistrado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/responsables")
    public ResponseEntity<List<UsuarioResumenDTO>> listarResponsablesAsignables() {
        List<UsuarioResumenDTO> responsables = Stream.concat(
                usuarioRepository.findByRolAndActivoTrueOrderByNombreAsc(Rol.DOCENTE).stream(),
                usuarioRepository.findByRolAndActivoTrueOrderByNombreAsc(Rol.ADMINISTRATIVO).stream())
                .map(this::toUsuarioResumenDTO)
                .toList();

        return ResponseEntity.ok(responsables);
    }

    private UsuarioResumenDTO toUsuarioResumenDTO(UsuarioJPAEntity usuario) {
        return new UsuarioResumenDTO(
                usuario.getNombre(),
                usuario.getIdentificacion(),
                usuario.getCorreo(),
                usuario.getActivo(),
                new RolEnumDTO(usuario.getRol().name()));
    }
}
