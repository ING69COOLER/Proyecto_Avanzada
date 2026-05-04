package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entidad: HistorialSolicitud - Constructor y accesores")
class HistorialSolicitudTest {

    @Test
    @DisplayName("(Exito) - Crear entrada de historial valida")
    void crearEntradaValida() {
        Usuario u = new Usuario(1L, "User", "111", "u@u", true, Rol.DOCENTE);
        Solicitud s = new Solicitud(co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud.HOMOLOGACION,
                "desc", co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen.PORTAL_WEB,
                java.time.LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, u, null);

        HistorialSolicitud h = new HistorialSolicitud(EstadoSolicitud.REGISTRADA, TipoAccion.CREACION, u, "Obs", s);
        assertEquals(u, h.obtenerUsuario());
        assertEquals("Obs", h.getObservacion());
        assertNotNull(h.getFechaHora());
    }

    @Test
    @DisplayName("(Fallo) - Crear entrada con responsable nulo o observacion vacia lanza")
    void crearEntradaInvalidaLanza() {
        Usuario u = new Usuario(2L, "U2", "222", "u2@u", true, Rol.DOCENTE);
        Solicitud s = new Solicitud(co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud.CONSULTA_ACADEMICA,
                "d", co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen.SAC,
                java.time.LocalDateTime.now(), null, EstadoSolicitud.REGISTRADA, u, null);

        assertThrows(IllegalArgumentException.class, () -> new HistorialSolicitud(EstadoSolicitud.REGISTRADA, TipoAccion.CREACION, null, "Obs", s));
        assertThrows(IllegalArgumentException.class, () -> new HistorialSolicitud(EstadoSolicitud.REGISTRADA, TipoAccion.CREACION, u, "", s));
    }
}
