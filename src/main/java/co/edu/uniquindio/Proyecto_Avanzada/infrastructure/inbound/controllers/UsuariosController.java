package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.UsuarioResumenDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.mapper.SolicitudResponseMapper;
import co.edu.uniquindio.Proyecto_Avanzada.application.services.RegistrarUsuarioApplicationService;
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

    private final RegistrarUsuarioApplicationService registrarUsuarioApplicationService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResumenDTO> registrarUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = new Usuario(
                null,
                request.nombre(),
                request.identificacion(),
                request.correo(),
                request.activo(),
                request.rol());

        Usuario usuarioRegistrado = registrarUsuarioApplicationService.registrarUsuario(usuario);
        UsuarioResumenDTO response = SolicitudResponseMapper.toUsuarioResumenDTO(usuarioRegistrado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
