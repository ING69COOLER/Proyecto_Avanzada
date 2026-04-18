package co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;

public interface IRepositorioUsuario {
    public void guardarUsuario(Usuario usuario, String passwordHash);
    public Usuario obtenerUsuarioIdentificacion(String id);
}
