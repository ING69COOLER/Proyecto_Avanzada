package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entidad: Solicitud - Constructor y transiciones basicas")
class SolicitudEntityTest {

    @Test
    @DisplayName("(Exito) - Crear solicitud valida inicializa historial y estado")
    void crearSolicitudValida() {
        Usuario u = new Usuario(1L, "Est", "777", "e@e", true, Rol.ESTUDIANTE);
        Solicitud s = new Solicitud(TipoSolicitud.HOMOLOGACION, "Desc", CanalOrigen.PORTAL_WEB,
                LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, u, null);

        assertEquals(EstadoSolicitud.REGISTRADA, s.getEstado());
        assertNotNull(s.getHistorial());
        assertFalse(s.getHistorial().isEmpty());
    }

    @Test
    @DisplayName("(Fallo) - Crear solicitud con campos obligatorios nulos lanza")
    void crearSolicitudConNulosLanza() {
        Usuario u = new Usuario(2L, "Est2", "888", "e2@e", true, Rol.ESTUDIANTE);
        assertThrows(IllegalArgumentException.class, () -> new Solicitud(null, "D", CanalOrigen.SAC, LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, u, null));
        assertThrows(IllegalArgumentException.class, () -> new Solicitud(TipoSolicitud.SOLICITUD_CUPOS, "", CanalOrigen.SAC, LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, u, null));
    }

    @Test
    @DisplayName("(Exito) - Clasificar y asignar flujo basico")
    void clasificarYAsignarFlujo() throws SolicitudException {
        Usuario solicitante = new Usuario(3L, "Est3", "999", "s@e", true, Rol.ESTUDIANTE);
        Usuario coord = new Usuario(4L, "Coord", "1010", "c@c", true, Rol.COORDINADOR);
        Usuario docente = new Usuario(5L, "Doc", "1111", "d@d", true, Rol.DOCENTE);

        Solicitud s = new Solicitud(TipoSolicitud.SOLICITUD_CUPOS, "X", CanalOrigen.PORTAL_WEB, LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, solicitante, null);
        s.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, coord, "Reclasifica");
        assertEquals(EstadoSolicitud.CLASIFICADA, s.getEstado());

        // asignar responsable debe fallar si usuario no es coordinador
        assertThrows(SolicitudException.class, () -> s.asignarResponsable(docente, "asign"));

        // asignar correcto
        s.asignarResponsable(coord, "asignando");
        assertEquals(EstadoSolicitud.EN_ATENCION, s.getEstado());
    }
}
