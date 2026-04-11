package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out.IRepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

/**
 * RF-01 / RF-07: Implementacion del repositorio de solicitudes academicas.
 *
 * RF-01: Almacena las solicitudes registradas en el sistema (en memoria, con
 * lista).
 * RF-07: Ofrece metodos de consulta por estado, tipo, prioridad y responsable.
 *
 * Patron Singleton: garantiza que solo exista una instancia del repositorio
 * durante todo el ciclo de vida de la aplicacion.
 *
 * RF-11: Al no depender de una base de datos externa, el sistema puede operar
 * de forma independiente en entornos sin persistencia.
 */

@Repository
public class RepositorioSolicitud implements IRepositorioSolicitud {

    private List<Solicitud> solicitudes;
    private final AtomicLong secuencia = new AtomicLong(1);

    /**
     * Constructor. Inicializa la lista de solicitudes en memoria.
     */
    public RepositorioSolicitud() {
        this.solicitudes = new ArrayList<>();
        System.out.println("[REPO] RepositorioSolicitud inicializado");
    }

    /**
     * Retorna la unica instancia del repositorio (patron Singleton, thread-safe).
     * Util para acceder al repositorio desde contextos fuera del contenedor de
     * Spring.
     *
     * @return Instancia unica de RepositorioSolicitud
     */
    

    /**
     * RF-01: Guarda una nueva solicitud registrada en el repositorio en memoria.
     * Se llama despues de que el usuario registra una solicitud desde cualquier
     * canal.
     *
     * @param solicitud Solicitud a guardar (si es nula, se ignora)
     */
    @Override
    public void guardarSolicitud(Solicitud solicitud) {
        if (solicitud == null) return;
        // Si no tiene ID, asignar uno nuevo (insertar)
        if (solicitud.getCodigo() == null) {
            solicitud.setCodigo(secuencia.getAndIncrement());
            solicitudes.add(solicitud);
            System.out.println("[REPO] Solicitud creada con ID: " + solicitud.getCodigo());
        } else {
            // Si tiene ID, actualizar (upsert)
            boolean actualizada = false;
            for (int i = 0; i < solicitudes.size(); i++) {
                if (solicitudes.get(i).getCodigo().equals(solicitud.getCodigo())) {
                    solicitudes.set(i, solicitud);
                    actualizada = true;
                    System.out.println("[REPO] Solicitud actualizada: ID " + solicitud.getCodigo());
                    break;
                }
            }
            if (!actualizada) {
                solicitudes.add(solicitud);
                System.out.println("[REPO] Solicitud insertada con ID existente: " + solicitud.getCodigo());
            }
        }
    }

    /**
     * Obtiene una solicitud por su ID
     */
    public Optional<Solicitud> obtenerPorId(Long id) {
        return solicitudes.stream()
                .filter(s -> s.getCodigo() != null && s.getCodigo().equals(id))
                .findFirst();
    }

    /**
     * Actualiza una solicitud existente
     */
    public void actualizar(Solicitud solicitud) {
        if (solicitud != null && solicitud.getCodigo() != null) {
            solicitudes.stream()
                    .filter(s -> s.getCodigo().equals(solicitud.getCodigo()))
                    .findFirst()
                    .ifPresent(s -> {
                        int index = solicitudes.indexOf(s);
                        solicitudes.set(index, solicitud);
                        System.out.println("[REPO] Solicitud actualizada: ID " + solicitud.getCodigo());
                    });
        }
    }

    /**
     * Elimina una solicitud por su ID
     */
    public void eliminar(Long id) {
        boolean eliminada = solicitudes.removeIf(s -> s.getCodigo() != null && s.getCodigo().equals(id));
        if (eliminada) {
            System.out.println("[REPO] Solicitud eliminada: ID " + id);
        }
    }

    /**
     * Obtiene todas las solicitudes
     */
    public List<Solicitud> listar() {
        System.out.println("[REPO] Total de solicitudes: " + solicitudes.size());
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
        System.out.println("[REPO] Repositorio limpiado");
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
        System.out.println("[REPO] Consulta por estado " + estadoSolicitud + ": " + resultado.size() + " resultados");
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
        System.out.println("[REPO] Consulta por tipo " + tipoSolicitud + ": " + resultado.size() + " resultados");
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
        System.out.println("[REPO] Consulta por prioridad: " + resultado.size() + " resultados");
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
        System.out.println(
                "[REPO] Consulta por responsable " + usuario.getNombre() + ": " + resultado.size() + " resultados");
        return resultado;
    }
}
