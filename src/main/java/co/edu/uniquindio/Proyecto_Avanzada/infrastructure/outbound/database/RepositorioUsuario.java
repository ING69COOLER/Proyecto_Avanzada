package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;

import org.springframework.stereotype.Repository;

@Repository
public class RepositorioUsuario implements IRepositorioUsuario{

        private List<Usuario> usuarios;

    public RepositorioUsuario(){
        this.usuarios = new ArrayList<>();
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuario obtenerUsuarioIdentificacion(String id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getIdentificacion().equals(id))
                .findFirst()
                .orElse(null);
    }

}
