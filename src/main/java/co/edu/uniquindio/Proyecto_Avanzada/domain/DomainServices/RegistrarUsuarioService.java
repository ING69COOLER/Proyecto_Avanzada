package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioUsuario;

public class RegistrarUsuarioService {

    IRepositorioUsuario repositorioUsuario;

    public RegistrarUsuarioService(){
        repositorioUsuario = RepositorioUsuario.getInstancia();
    }
    // METODO SOLICITADO
    public void registrarUsuario(Usuario usuario){

        if (repositorioUsuario.obtenerUsuarioIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException("EL usuario ya existe");
        }

        repositorioUsuario.guardarUsuario(usuario);
    }
}
