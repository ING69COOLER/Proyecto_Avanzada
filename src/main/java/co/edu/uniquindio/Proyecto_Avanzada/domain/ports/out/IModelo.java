package co.edu.uniquindio.Proyecto_Avanzada.domain.ports.out;

public interface IModelo {
    /**
     * RF-09: Genera un resumen de una solicitud usando un modelo de IA
     * 
     * @param solicitud Solicitud a resumir
     * @return Resumen generado por el modelo de IA
     */
    String generarResumenIA(String resumen);

    /**
     * RF-10: Sugiere tipo y prioridad a partir del texto descriptivo
     * 
     * @param descripcion Texto de la solicitud
     * @return Sugerencia de clasificación (debe ser confirmada por un humano)
     */
    String sugerirClasificacionIA(String descripcion);

}
