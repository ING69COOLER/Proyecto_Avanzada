package co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects;


public record Prioridad(Long id, String descripcion, NivelPrioridad nivel) {

    public Prioridad {
        //RN3 debe de haber una justificacion
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("debe de haber una justificacion para poder registrar una prioridad");
        }
        if (nivel == null) {
            throw new IllegalArgumentException("El nivel de prioridad no puede ser nulo");
        }
    }

    public Prioridad(NivelPrioridad prioridad, String justificacion) {
        this(null, justificacion, prioridad);
    }
}
