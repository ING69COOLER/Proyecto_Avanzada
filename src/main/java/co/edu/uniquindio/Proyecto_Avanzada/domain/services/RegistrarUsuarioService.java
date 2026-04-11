package co.edu.uniquindio.Proyecto_Avanzada.domain.services;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import org.springframework.stereotype.Service;

//caso de uso
@Service
public class RegistrarUsuarioService {

    

    // METODO SOLICITADO
    public Usuario registrarUsuario(Usuario usuario){

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        return usuario;
    }
}
