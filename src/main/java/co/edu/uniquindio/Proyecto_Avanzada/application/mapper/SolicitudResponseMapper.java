package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.EventoHistorialResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.PrioridadDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudDetalleResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.SolicitudResumenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TipoSolicitudDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.UsuarioResumenDTO;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.HistorialSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

/**
 * Mapper para transformar entidades del dominio de solicitudes en DTOs de respuesta.
 * Utiliza MapStruct para generar automáticamente los mapeos utilizando getters.
 */
@Mapper(componentModel = "spring")
public interface SolicitudResponseMapper {

    @Mapping(target = "tipoSolicitud", source = "tipo")
    SolicitudDetalleResponse toDetalleResponse(Solicitud solicitud);

    @Mapping(target = "tipoSolicitud", source = "tipo")
    SolicitudResumenResponse toResumenResponse(Solicitud solicitud);

    List<SolicitudResumenResponse> toResumenResponseList(List<Solicitud> solicitudes);

    List<EventoHistorialResponse> toHistorialResponseList(List<HistorialSolicitud> historial);

    EventoHistorialResponse toEventoHistorialResponse(HistorialSolicitud evento);

    UsuarioResumenDTO toUsuarioResumenDTO(Usuario usuario);

    TipoSolicitudDTO toTipoSolicitudDTO(TipoSolicitud tipoSolicitud);

    PrioridadDTO toPrioridadDTO(Prioridad prioridad);

}
