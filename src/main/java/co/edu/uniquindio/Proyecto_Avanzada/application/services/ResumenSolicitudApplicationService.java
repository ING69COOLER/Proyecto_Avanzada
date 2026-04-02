package co.edu.uniquindio.Proyecto_Avanzada.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.Proyecto_Avanzada.domain.IAIntegration.IModeloLenguaje;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoImplementation.RepositorioSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.repos.repoInterfaces.IRepositorioSolicitud;

@Service
public class ResumenSolicitudApplicationService {

    private ResumenSolicitudService dominio;
    private IRepositorioSolicitud repositorio;

    public ResumenSolicitudApplicationService(IModeloLenguaje modeloLenguaje) {
        this.dominio = new ResumenSolicitudService(modeloLenguaje);
        this.repositorio = RepositorioSolicitud.getInstancia();
    }

    public String generarResumenSolicitud(Solicitud solicitud) throws SolicitudException {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        Optional<Solicitud> solicitudCompleta;
        if (solicitud.getCodigo() == null) {
            solicitudCompleta = Optional.of(solicitud);
        } else {
            solicitudCompleta = repositorio.obtenerPorId(solicitud.getCodigo());
            if (!solicitudCompleta.isPresent()) {
                throw new SolicitudException("No se encontró la solicitud con ID: " + solicitud.getCodigo());
            }
        }

        if (solicitud.getCodigo() != null) {
            Optional<Solicitud> persisted = repositorio.obtenerPorId(solicitud.getCodigo());
            if (persisted.isPresent()) {
                solicitud = persisted.get();
            } else {
                throw new SolicitudException("No se encontró la solicitud con ID: " + solicitud.getCodigo());
            }
        }

        return dominio.generarResumenSolicitud(solicitud);
    }

    public List<String> generarResumenesMasivos(List<Solicitud> solicitudes) throws SolicitudException {
        return dominio.generarResumenesMasivos(solicitudes);
    }

    public int obtenerLongitudEstimadaResumen(Solicitud solicitud) {
        return dominio.obtenerLongitudEstimadaResumen(solicitud);
    }

    public String sugerirClasificacion(String descripcion) {
        return dominio.sugerirClasificacion(descripcion);
    }
}
