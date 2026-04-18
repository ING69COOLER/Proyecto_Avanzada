package co.edu.uniquindio.Proyecto_Avanzada.application.usecase;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.services.RegistrarUsuarioService;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrarUsuarioUseCase {
    private final IRepositorioUsuario usuarioRepository;
    private final RegistrarUsuarioService dominio;

    public Usuario ejecutar(Usuario usuario, String passwordHash) {
        if (usuarioRepository.obtenerUsuarioIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Usuario usuarioAprobado = dominio.registrarUsuario(usuario);
        usuarioRepository.guardarUsuario(usuarioAprobado, passwordHash);
        return usuarioAprobado;
    }
}
