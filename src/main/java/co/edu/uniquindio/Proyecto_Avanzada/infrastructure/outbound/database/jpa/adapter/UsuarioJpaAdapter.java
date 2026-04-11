package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.adapter;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioUsuario;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.UsuarioJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.mapper.JpaEntityMapper;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository.UsuarioSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Adaptador JPA para el puerto IRepositorioUsuario.
 * El dominio nunca conoce la existencia de Spring Data ni de UsuarioJPAEntity.
 */
@Repository
@Primary
@RequiredArgsConstructor
public class UsuarioJpaAdapter implements IRepositorioUsuario {

    private final UsuarioSpringDataRepository springRepo;

    @Override
    public void guardarUsuario(Usuario usuario) {
        UsuarioJPAEntity entity = JpaEntityMapper.toJpa(usuario);
        springRepo.save(entity);
    }

    @Override
    public Usuario obtenerUsuarioIdentificacion(String identificacion) {
        return springRepo.findByIdentificacion(identificacion)
                .map(JpaEntityMapper::toDomain)
                .orElse(null);
    }
}
