package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository;

import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.SolicitudJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository — solo existe en infraestructura.
 */
public interface SolicitudSpringDataRepository extends JpaRepository<SolicitudJPAEntity, Long> {
    List<SolicitudJPAEntity> findByEstado(EstadoSolicitud estado);
    List<SolicitudJPAEntity> findByTipo(TipoSolicitud tipo);
    List<SolicitudJPAEntity> findByPrioridadNivel(NivelPrioridad nivel);

    Page<SolicitudJPAEntity> findByEstado(EstadoSolicitud estado, Pageable pageable);
    Page<SolicitudJPAEntity> findByTipo(TipoSolicitud tipo, Pageable pageable);
    Page<SolicitudJPAEntity> findByPrioridadNivel(NivelPrioridad nivel, Pageable pageable);

    @Query("""
            select distinct s
            from SolicitudJPAEntity s
            join s.historial h
            join h.responsable r
            where r.identificacion = :identificacion
            """)
    Page<SolicitudJPAEntity> findByResponsableIdentificacion(@Param("identificacion") String identificacion, Pageable pageable);
}
