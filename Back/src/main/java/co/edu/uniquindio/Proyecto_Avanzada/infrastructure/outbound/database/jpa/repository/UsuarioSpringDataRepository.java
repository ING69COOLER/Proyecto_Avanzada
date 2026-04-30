package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository;

import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.UsuarioJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository para usuarios — solo en infraestructura.
 */
public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioJPAEntity, Long> {
    Optional<UsuarioJPAEntity> findByIdentificacion(String identificacion);
}
