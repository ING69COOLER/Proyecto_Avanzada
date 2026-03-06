package co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de solicitudes con patrón Singleton.
 * Almacena las solicitudes en memoria (lista).
 */
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
}
