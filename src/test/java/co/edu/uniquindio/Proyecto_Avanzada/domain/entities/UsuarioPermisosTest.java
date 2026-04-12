package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Capa Dominio: Entidad Usuario (Permisos y Roles)")
class UsuarioPermisosTest {

    @Test
    @DisplayName("Un Estudiante DEBE poder registrar solicitudes y NO DEBE poder clasificar, asignar, atender o cerrar.")
    void estudiante_TienePermisosExclusivosDeRegistro() {
        // Arrange
        Usuario estudiante = new Usuario(1L, "Estudiante 1", "12345", "est@mail.com", true, Rol.ESTUDIANTE);

        // Act & Assert (AAA)
        assertTrue(estudiante.puedeRegistrarSolicitud(), "Un estudiante deberia poder registrar solicitudes");
        
        assertFalse(estudiante.puedeAsignar(), "Un estudiante NO deberia tener permisos de Coordinador");
        assertFalse(estudiante.puedeAtenderSolicitud(), "Un estudiante NO deberia tener permisos de Docente");
    }

    @Test
    @DisplayName("Un Coordinador DEBE poder clasificar, asignar y cerrar, pero NO DEBE registrar ni atender operativamente.")
    void coordinador_TienePermisosDeGestionAdministrativa() {
        // Arrange
        Usuario coordinador = new Usuario(2L, "Coordinador 1", "54321", "coord@mail.com", true, Rol.COORDINADOR);

        // Act & Assert
        assertTrue(coordinador.puedeAsignar(), "Un coordinador deberia tener permisos de asignacion y clasificacion");
        assertTrue(coordinador.puedeClasificarSolicitud() && coordinador.puedeCerrarSolicitud(), "Un coordinador deberia poder emitir decisiones (clasificar/cerrar)");

        assertFalse(coordinador.puedeRegistrarSolicitud(), "Un coordinador NO deberia poder registrar solicitudes propias");
        assertFalse(coordinador.puedeAtenderSolicitud(), "Un coordinador NO deberia atender solicitudes directamente");
    }

    @Test
    @DisplayName("Un Docente DEBE poder atender solicitudes asignadas, pero NO DEBE registrar, clasificar ni cerrar.")
    void docente_TienePermisosExclusivosDeAtencion() {
        // Arrange
        Usuario docente = new Usuario(3L, "Docente 1", "99999", "docente@mail.com", true, Rol.DOCENTE);

        // Act & Assert
        assertTrue(docente.puedeAtenderSolicitud(), "Un docente deberia poder atender solicitudes");

        assertFalse(docente.puedeAsignar(), "Un docente NO deberia tener permisos de gestion (no asigna/clasifica).");
        assertFalse(docente.puedeClasificarSolicitud() || docente.puedeCerrarSolicitud(), "Un docente NO deberia poder cerrar solicitudes ni clasificarlas.");
        assertFalse(docente.puedeRegistrarSolicitud(), "Un docente NO deberia registrar solicitudes como estudiante.");
    }
}
