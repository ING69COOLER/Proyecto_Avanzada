package co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
//caso de uso
public class RegistrarUsuarioService {

    

    // METODO SOLICITADO
    public Usuario registrarUsuario(Usuario usuario){

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        return usuario;
    }
}
