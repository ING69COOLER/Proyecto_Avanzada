package co.edu.uniquindio.Proyecto_Avanzada.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.EstadoSolicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Prioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "identificacion", nullable = false, unique = true)
    private String identificacion;
    
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;
    
    @OneToMany(mappedBy = "usuarioResponsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudes;

    public void crearSolicitud(String descripcion, 
                                LocalDateTime fechaHoraRegistro, 
                                LocalDateTime fechaCierre, 
                                TipoSolicitud tipo, 
                                Usuario usuarioResponsable, 
                                Prioridad prioridad, 
                                CanalOrigen canalOrigen){

        // Constructor recibe: id (null), descripción, fecha/hora registro, fecha cierre, tipo solicitud,
        // usuario responsable, prioridad, canal origen y estado inicial (ABIERTA)

        //tipo de solicitud, descripcion, canal_origen, fecha y hora, identificacion del solicitante

        //requisito funcional
        if (!(tipo == null | descripcion == null | canalOrigen == null | fechaHoraRegistro == null | identificacion == null) ) {
            solicitudes.add(new Solicitud(null, descripcion, fechaHoraRegistro, fechaCierre , EstadoSolicitud.REGISTRADA, tipo, usuarioResponsable, prioridad, canalOrigen, null));
        } else {
            System.err.print("debe de haber minimo: tipo de solicitud, descripcion, canal origen, fecha y hora, identificacion del solicitante");
        }
    }

    // esto va por tipo de solicitud y fecha limite asociada
    public NivelPrioridad establecePrioridad(TipoSolicitud tipo, LocalDateTime fechaCierre) {

        double razonPrioridadTipo = 1.0 / ((double) (tipo.ordinal() + 1) / TipoSolicitud.values().length);
        double razonPrioridadTiempo = 10.0 / ChronoUnit.DAYS.between(LocalDateTime.now(), fechaCierre);
        double razonTotal = razonPrioridadTiempo + razonPrioridadTipo;

        NivelPrioridad nivel;
        if (razonTotal >= 8)
            nivel = NivelPrioridad.ALTA;
        else if (razonTotal >= 3)
            nivel = NivelPrioridad.MEDIA;
        else
            nivel = NivelPrioridad.BAJA;

        return nivel;
    }
}

