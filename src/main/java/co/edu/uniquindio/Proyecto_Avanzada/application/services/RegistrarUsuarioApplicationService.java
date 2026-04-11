package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.services.RegistrarUsuarioService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrarUsuarioApplicationService {

    private final RegistrarUsuarioService dominio;
    private final IRepositorioUsuario repositorio;

    

    public Usuario registrarUsuario(Usuario usuario) {

        if (repositorio.obtenerUsuarioIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException("EL usuario ya existe");
        }

        Usuario usuarioValidado = dominio.registrarUsuario(usuario);
        repositorio.guardarUsuario(usuarioValidado);
        return usuarioValidado;
    }
}