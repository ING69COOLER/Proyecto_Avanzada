package co.edu.uniquindio.Proyecto_Avanzada.application.mapper;

import org.mapstruct.Mapper;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.CanalOrigenEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.EstadoSolicitudEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.NivelPrioridadEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.RolEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.TipoAccionEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.enums.TipoSolicitudEnumDTO;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoAccion;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Mapper(componentModel = "spring")
public interface EnumDtoMapper {

    default CanalOrigenEnumDTO toDto(CanalOrigen value) {
        return value == null ? null : new CanalOrigenEnumDTO(value.name());
    }

    default EstadoSolicitudEnumDTO toDto(EstadoSolicitud value) {
        return value == null ? null : new EstadoSolicitudEnumDTO(value.name());
    }

    default NivelPrioridadEnumDTO toDto(NivelPrioridad value) {
        return value == null ? null : new NivelPrioridadEnumDTO(value.name());
    }

    default TipoAccionEnumDTO toDto(TipoAccion value) {
        return value == null ? null : new TipoAccionEnumDTO(value.name());
    }

    default TipoSolicitudEnumDTO toDto(TipoSolicitud value) {
        return value == null ? null : new TipoSolicitudEnumDTO(value.name());
    }

    default RolEnumDTO toDto(Rol value) {
        return value == null ? null : new RolEnumDTO(value.name());
    }

    default CanalOrigen toCanalOrigen(CanalOrigenEnumDTO dto) {
        return dto == null ? null : parseEnum(CanalOrigen.class, dto.codigo(), "canalOrigen");
    }

    default EstadoSolicitud toEstadoSolicitud(EstadoSolicitudEnumDTO dto) {
        return dto == null ? null : parseEnum(EstadoSolicitud.class, dto.codigo(), "estadoSolicitud");
    }

    default NivelPrioridad toNivelPrioridad(NivelPrioridadEnumDTO dto) {
        return dto == null ? null : parseEnum(NivelPrioridad.class, dto.codigo(), "nivelPrioridad");
    }

    default TipoAccion toTipoAccion(TipoAccionEnumDTO dto) {
        return dto == null ? null : parseEnum(TipoAccion.class, dto.codigo(), "tipoAccion");
    }

    default TipoSolicitud toTipoSolicitud(TipoSolicitudEnumDTO dto) {
        return dto == null ? null : parseEnum(TipoSolicitud.class, dto.codigo(), "tipoSolicitud");
    }

    default Rol toRol(RolEnumDTO dto) {
        return dto == null ? null : parseEnum(Rol.class, dto.codigo(), "rol");
    }

    default EstadoSolicitud toEstadoSolicitud(String value) {
        return parseEnumOrNull(EstadoSolicitud.class, value, "estadoSolicitud");
    }

    default TipoSolicitud toTipoSolicitud(String value) {
        return parseEnumOrNull(TipoSolicitud.class, value, "tipoSolicitud");
    }

    default NivelPrioridad toNivelPrioridad(String value) {
        return parseEnumOrNull(NivelPrioridad.class, value, "prioridadSolicitud");
    }

    private <T extends Enum<T>> T parseEnumOrNull(Class<T> type, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseEnum(type, value, fieldName);
    }

    private <T extends Enum<T>> T parseEnum(Class<T> type, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio");
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Valor invalido para " + fieldName + ": " + value);
        }
    }
}
