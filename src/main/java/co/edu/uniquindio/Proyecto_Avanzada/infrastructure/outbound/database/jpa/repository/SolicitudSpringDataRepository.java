package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository;

import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.SolicitudJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository — solo existe en infraestructura.
 */
public interface SolicitudSpringDataRepository extends JpaRepository<SolicitudJPAEntity, Long> {
    List<SolicitudJPAEntity> findByEstado(EstadoSolicitud estado);
    List<SolicitudJPAEntity> findByTipo(TipoSolicitud tipo);
    List<SolicitudJPAEntity> findByPrioridadNivel(NivelPrioridad nivel);
}
