package co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Implementación del repositorio de solicitudes con patrón Singleton.
 * Almacena las solicitudes en memoria (lista).
 */

@Service
public class RepositorioSolicitud implements IRepositorioSolicitud {

    private static RepositorioSolicitud instancia;
    private List<Solicitud> solicitudes;

    /**
     * Constructor privado para el patrón Singleton
     */
    private RepositorioSolicitud() {
        this.solicitudes = new ArrayList<>();
        System.out.println("✅ RepositorioSolicitud inicializado (Singleton)");
    }

    /**
     * Obtiene la instancia única del repositorio
     */
    public static synchronized RepositorioSolicitud getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioSolicitud();
        }
        return instancia;
    }

    /**
     * Guarda una nueva solicitud en el repositorio
     */
    @Override
    public void guardarSolicitud(Solicitud solicitud) {
        if (solicitud != null) {
            solicitudes.add(solicitud);
            System.out.println("✅ Solicitud guardada: " + solicitud.getTipo() + " - " + solicitud.getDescripcion());
        }
    }

    /**
     * Obtiene una solicitud por su ID
     */
    public Optional<Solicitud> obtenerPorId(Long id) {
        return solicitudes.stream()
                .filter(s -> s.getId() != null && s.getId().equals(id))
                .findFirst();
    }

    /**
     * Actualiza una solicitud existente
     */
    public void actualizar(Solicitud solicitud) {
        if (solicitud != null && solicitud.getId() != null) {
            solicitudes.stream()
                    .filter(s -> s.getId().equals(solicitud.getId()))
                    .findFirst()
                    .ifPresent(s -> {
                        int index = solicitudes.indexOf(s);
                        solicitudes.set(index, solicitud);
                        System.out.println("✅ Solicitud actualizada: ID " + solicitud.getId());
                    });
        }
    }

    /**
     * Elimina una solicitud por su ID
     */
    public void eliminar(Long id) {
        boolean eliminada = solicitudes.removeIf(s -> s.getId() != null && s.getId().equals(id));
        if (eliminada) {
            System.out.println("✅ Solicitud eliminada: ID " + id);
        }
    }

    /**
     * Obtiene todas las solicitudes
     */
    public List<Solicitud> listar() {
        System.out.println("📋 Total de solicitudes: " + solicitudes.size());
        return new ArrayList<>(solicitudes);
    }

    /**
     * Obtiene el conteo de solicitudes
     */
    public int contar() {
        return solicitudes.size();
    }

    /**
     * Limpia todas las solicitudes del repositorio (útil para pruebas)
     */
    public void limpiar() {
        solicitudes.clear();
        System.out.println("🗑️ Repositorio limpiado");
    }

/**
     * RF-07: Consulta solicitudes por estado
     */
    @Override
    public List<Solicitud> consultarEstado(EstadoSolicitud estadoSolicitud) {
        if (estadoSolicitud == null) {
            throw new IllegalArgumentException("El estado de solicitud no puede ser nulo");
        }
        List<Solicitud> resultado = solicitudes.stream()
                .filter(s -> s.getEstado() != null && s.getEstado().equals(estadoSolicitud))
                .toList();
        System.out.println("🔍 Consulta por estado " + estadoSolicitud + ": " + resultado.size() + " resultados");
        return resultado;
    }

    /**
     * RF-07: Consulta solicitudes por tipo de solicitud
     */
    @Override
    public List<Solicitud> consultarTipoSolicitud(TipoSolicitud tipoSolicitud) {
        if (tipoSolicitud == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo");
        }
        List<Solicitud> resultado = solicitudes.stream()
                .filter(s -> s.getTipo() != null && s.getTipo().equals(tipoSolicitud))
                .toList();
        System.out.println("🔍 Consulta por tipo " + tipoSolicitud + ": " + resultado.size() + " resultados");
        return resultado;
    }

    /**
     * RF-07: Consulta solicitudes por prioridad
     */
    @Override
    public List<Solicitud> consultarPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
        List<Solicitud> resultado = solicitudes.stream()
                .filter(s -> s.getPrioridad() != null && s.getPrioridad().equals(prioridad))
                .toList();
        System.out.println("🔍 Consulta por prioridad: " + resultado.size() + " resultados");
        return resultado;
    }

    /**
     * RF-07: Consulta solicitudes asignadas a un responsable específico
     * Busca en el historial la asignación más reciente
     */
    @Override
    public List<Solicitud> consultarResponsable(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario responsable no puede ser nulo");
        }
        List<Solicitud> resultado = solicitudes.stream()
                .filter(s -> s.getHistorial() != null && 
                        s.getHistorial().stream()
                            .anyMatch(h -> h.getResponsable() != null && h.getResponsable().equals(usuario)))
                .toList();
        System.out.println("🔍 Consulta por responsable " + usuario.getNombre() + ": " + resultado.size() + " resultados");
        return resultado;
    }
}
