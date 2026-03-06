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
}
