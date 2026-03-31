package co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation;

import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioUsuario;

public class RepositorioUsuario implements IRepositorioUsuario{

    private static RepositorioUsuario instancia;
    private List<Usuario> usuarios;

    private RepositorioUsuario(){}

    public static RepositorioUsuario getInstancia(){
        if (instancia == null) {
            instancia = new RepositorioUsuario();
        }
        return instancia;
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuario obtenerUsuarioIdentificacion(String id) {
        return (Usuario)usuarios.stream().filter((val) -> val.getIdentificacion().equals(id));
    }

}
