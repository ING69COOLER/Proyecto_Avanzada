package co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Manuel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prioridad {

    private Long id;

    private String descripcion;

    private NivelPrioridad nivel;

    public Prioridad(NivelPrioridad prioridad, String justificacion){
        //RN3 debe de haber una justificacion
        if (justificacion.isBlank() || justificacion.isEmpty()) {
            throw new IllegalArgumentException("debe de haber una justificacion para poder registrar una prioridad");
        }
    }
}
