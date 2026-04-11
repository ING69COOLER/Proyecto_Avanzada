package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.RegistrarUsuarioService;

@Service
@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {
    private final IRepositorioUsuario usuarioRepository;
    private final RegistrarUsuarioService dominio;

    public Usuario ejecutar(Usuario usuario) {
        if (usuarioRepository.obtenerUsuarioIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Usuario usuarioAprobado = dominio.registrarUsuario(usuario);
        usuarioRepository.guardarUsuario(usuarioAprobado);
        return usuarioAprobado;
    }
}
