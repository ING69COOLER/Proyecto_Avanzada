package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.adapter;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.SolicitudJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.mapper.JpaEntityMapper;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository.SolicitudSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia JPA para el puerto IRepositorioSolicitud.
 *
 * Traduce entre el modelo de dominio (Solicitud) y el modelo de persistencia
 * (SolicitudJPAEntity) usando JpaEntityMapper. El dominio nunca ve JPA.
 *
 * @Primary hace que Spring inyecte este adaptador en lugar del repositorio en memoria.
 */
@Repository
@Primary
@RequiredArgsConstructor
public class SolicitudJpaAdapter implements IRepositorioSolicitud {

    private final SolicitudSpringDataRepository springRepo;

    @Override
    public void guardarSolicitud(Solicitud solicitud) {
        SolicitudJPAEntity entity = JpaEntityMapper.toJpa(solicitud);
        SolicitudJPAEntity saved = springRepo.save(entity);
        // Propagar el ID generado de vuelta al dominio
        solicitud.setCodigo(saved.getCodigo());
    }

    @Override
    public Optional<Solicitud> obtenerPorId(Long id) {
        return springRepo.findById(id).map(JpaEntityMapper::toDomain);
    }

    @Override
    public List<Solicitud> listar() {
        return JpaEntityMapper.toDomainList(springRepo.findAll());
    }

    @Override
    public Page<Solicitud> listar(Pageable pageable) {
        return springRepo.findAll(pageable).map(JpaEntityMapper::toDomain);
    }

    @Override
    public List<Solicitud> consultarEstado(EstadoSolicitud estado) {
        return JpaEntityMapper.toDomainList(springRepo.findByEstado(estado));
    }

    @Override
    public List<Solicitud> consultarTipoSolicitud(TipoSolicitud tipo) {
        return JpaEntityMapper.toDomainList(springRepo.findByTipo(tipo));
    }

    @Override
    public List<Solicitud> consultarPrioridad(Prioridad prioridad) {
        return JpaEntityMapper.toDomainList(
                springRepo.findByPrioridadNivel(prioridad != null ? prioridad.nivel() : null)
        );
    }

    

    @Override
    public List<Solicitud> consultarResponsable(Usuario usuario) {
        // Filtrado en memoria sobre los resultados: el historial ya viene cargado (EAGER)
        return springRepo.findAll().stream()
                .map(JpaEntityMapper::toDomain)
                .filter(s -> s.obtenerUsuariosDeHistorias().stream()
                        .anyMatch(u -> u != null
                                && u.getIdentificacion().equals(usuario.getIdentificacion())))
                .toList();
    }

    @Override
    public List<Solicitud> consultarPorNivelPrioridad(NivelPrioridad nivelPrioridad) {
        return JpaEntityMapper.toDomainList(springRepo.findByPrioridadNivel(nivelPrioridad));
    }
}
