package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.RegistrarUsuarioService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioUsuario;

public class RegistrarUsuarioApplicationService {

    private RegistrarUsuarioService dominio;
    private IRepositorioUsuario repositorio;

    public RegistrarUsuarioApplicationService() {
        this.dominio = new RegistrarUsuarioService();
        this.repositorio = RepositorioUsuario.getInstancia();
    }

    public Usuario registrarUsuario(Usuario usuario) {

        if (repositorio.obtenerUsuarioIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException("EL usuario ya existe");
        }

        Usuario usuarioValidado = dominio.registrarUsuario(usuario);
        repositorio.guardarUsuario(usuarioValidado);
        return usuarioValidado;
    }
}