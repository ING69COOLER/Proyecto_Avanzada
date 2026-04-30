package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.mapper;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.HistorialSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.HistorialSolicitudJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.SolicitudJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.UsuarioJPAEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper puro entre entidades de dominio y entidades JPA.
 * Sin dependencias de frameworks. Solo conversión de datos.
 * No se anota como Spring Bean para mantenerlo sin estado.
 */
public final class JpaEntityMapper {

    private JpaEntityMapper() {}

    // ─────────────────────────────────────────────────────────────
    // DOMINIO → JPA
    // ─────────────────────────────────────────────────────────────

    public static UsuarioJPAEntity toJpa(Usuario domain) {
        if (domain == null) return null;
        return UsuarioJPAEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .identificacion(domain.getIdentificacion())
                .correo(domain.getCorreo())
                .activo(domain.getActivo())
                .rol(domain.getRol())
                .build();
    }

    public static SolicitudJPAEntity toJpa(Solicitud domain) {
        if (domain == null) return null;

        SolicitudJPAEntity entity = SolicitudJPAEntity.builder()
                .codigo(domain.getCodigo())
                .tipo(domain.getTipo())
                .descripcion(domain.getDescripcion())
                .canalOrigen(domain.getCanalOrigen())
                .fechaHoraRegistro(domain.getFechaHoraRegistro())
                .fechaCierre(domain.getFechaCierre())
                .estado(domain.getEstado())
                .usuarioSolicitante(toJpa(domain.getUsuarioSolicitante()))
                .prioridadNivel(domain.getPrioridad() != null ? domain.getPrioridad().nivel() : null)
                .prioridadDescripcion(domain.getPrioridad() != null ? domain.getPrioridad().descripcion() : null)
                .build();

        // Convertir el historial, estableciendo la referencia inversa
        if (domain.getHistorial() != null) {
            List<HistorialSolicitudJPAEntity> histJpa = new ArrayList<>();
            for (HistorialSolicitud h : domain.getHistorial()) {
                HistorialSolicitudJPAEntity hEntity = toJpa(h, entity);
                histJpa.add(hEntity);
            }
            entity.setHistorial(histJpa);
        }

        return entity;
    }

    public static HistorialSolicitudJPAEntity toJpa(HistorialSolicitud domain, SolicitudJPAEntity solicitudJpa) {
        if (domain == null) return null;
        return HistorialSolicitudJPAEntity.builder()
                .fechaHora(domain.getFechaHora())
                .observacion(domain.getObservacion())
                .estado(domain.getEstado())
                .accion(domain.getAccion())
                .responsable(toJpa(domain.getResponsable()))
                .solicitud(solicitudJpa)
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // JPA → DOMINIO
    // ─────────────────────────────────────────────────────────────

    public static Usuario toDomain(UsuarioJPAEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getIdentificacion(),
                entity.getCorreo(),
                entity.getActivo(),
                entity.getRol()
        );
    }

    public static Solicitud toDomain(SolicitudJPAEntity entity) {
        if (entity == null) return null;

        // Reconstruimos la solicitud sin pasar por el constructor (que lanza historial duplicado)
        Solicitud domain = new Solicitud(
                entity.getTipo(),
                entity.getDescripcion(),
                entity.getCanalOrigen(),
                entity.getFechaHoraRegistro(),
                entity.getFechaCierre(),
                entity.getEstado(),
                toDomain(entity.getUsuarioSolicitante()),
                entity.getPrioridadNivel() != null
                        ? new Prioridad(entity.getPrioridadNivel(), entity.getPrioridadDescripcion())
                        : null
        );

        // Sobrescribir el codigo y estado (el constructor asigna REGISTRADA por defecto)
        domain.setCodigo(entity.getCodigo());
        domain.setEstado(entity.getEstado());
        domain.setFechaCierre(entity.getFechaCierre());

        // Reconstruir el historial sin activar el constructor de HistorialSolicitud
        List<HistorialSolicitud> historial = new ArrayList<>();
        if (entity.getHistorial() != null) {
            for (HistorialSolicitudJPAEntity h : entity.getHistorial()) {
                historial.add(toDomain(h, domain));
            }
        }
        domain.setHistorial(historial);

        return domain;
    }

    public static HistorialSolicitud toDomain(HistorialSolicitudJPAEntity entity, Solicitud solicitudDomain) {
        if (entity == null) return null;
        // Usamos el constructor pero pasamos la instancia ya construida (sin re-crear historial)
        HistorialSolicitud h = new HistorialSolicitud(
                entity.getEstado(),
                entity.getAccion(),
                toDomain(entity.getResponsable()),
                entity.getObservacion(),
                solicitudDomain
        );
        h.setFechaHora(entity.getFechaHora());
        return h;
    }

    public static List<Solicitud> toDomainList(List<SolicitudJPAEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(JpaEntityMapper::toDomain).toList();
    }
}
