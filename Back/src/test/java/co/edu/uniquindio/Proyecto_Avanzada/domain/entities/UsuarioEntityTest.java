package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entidad: Usuario - Validaciones y permisos básicos")
class UsuarioEntityTest {

    @Test
    @DisplayName("(Exito) - Crear usuario con identificacion valida")
    void crearUsuarioIdentificacionValida() {
        Usuario u = new Usuario(1L, "Nombre", "12345", "a@b.co", true, Rol.ESTUDIANTE);
        assertEquals("12345", u.getIdentificacion());
        assertTrue(u.puedeRegistrarSolicitud());
    }

    @Test
    @DisplayName("(Fallo) - Crear usuario con identificacion vacia lanza excepción")
    void crearUsuarioIdentificacionVaciaLanza() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(2L, "N", "", "x@x", true, Rol.ESTUDIANTE));
    }

    @Test
    @DisplayName("(Exito) - Roles y permisos")
    void rolesYPermisos() {
        Usuario estudiante = new Usuario(3L, "Est", "777", "e@e", true, Rol.ESTUDIANTE);
        Usuario coord = new Usuario(4L, "Coord", "888", "c@c", true, Rol.COORDINADOR);
        Usuario docente = new Usuario(5L, "Doc", "999", "d@d", true, Rol.DOCENTE);

        assertTrue(estudiante.puedeRegistrarSolicitud());
        assertFalse(estudiante.puedeClasificarSolicitud());

        assertTrue(coord.puedeClasificarSolicitud());
        assertTrue(coord.puedeAsignar());

        assertTrue(docente.puedeAtenderSolicitud());
        assertFalse(docente.puedeAsignar());
    }

    @Test
    @DisplayName("(Fallo) - validarPuedeRegistrarSolicitud lanza si inactivo o rol incorrecto")
    void validarPuedeRegistrarSolicitudFalla() {
        Usuario inactivo = new Usuario(6L, "In", "1010", "i@i", false, Rol.ESTUDIANTE);
        Usuario coord = new Usuario(7L, "Coord", "2020", "c2@c", true, Rol.COORDINADOR);

        assertThrows(IllegalArgumentException.class, inactivo::validarPuedeRegistrarSolicitud);
        assertThrows(IllegalArgumentException.class, coord::validarPuedeRegistrarSolicitud);
    }
}
