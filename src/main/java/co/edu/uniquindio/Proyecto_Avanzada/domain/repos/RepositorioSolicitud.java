package co.edu.uniquindio.Proyecto_Avanzada.domain.repos;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;

public class RepositorioSolicitud {

    private static volatile RepositorioSolicitud instancia;
    private ArrayList<Solicitud> listaSolicitudes;

    private RepositorioSolicitud() {
        this.listaSolicitudes = new ArrayList<>();
    }

    public static RepositorioSolicitud getInstancia() {
        if (instancia == null) {
            synchronized (RepositorioSolicitud.class) {
                if (instancia == null) {
                    instancia = new RepositorioSolicitud();
                }
            }
        }
        return instancia;
    }

    public void agregar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        listaSolicitudes.add(solicitud);
    }

    public List<Solicitud> listar() {
        return new ArrayList<>(listaSolicitudes);
    }

    public Solicitud buscarPorId(Long id) {
        return listaSolicitudes.stream()
                .filter(s -> s.getId() != null && s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
