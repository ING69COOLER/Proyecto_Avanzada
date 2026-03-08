package co.edu.uniquindio.Proyecto_Avanzada.domain.Prueba;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.ResumenSolicitudService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Solicitud;
import co.edu.uniquindio.Proyecto_Avanzada.domain.entities.Usuario;
import co.edu.uniquindio.Proyecto_Avanzada.domain.exception.SolicitudException;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.CanalOrigen;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.NivelPrioridad;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.TipoSolicitud;

@Component
public class Prueba {
	
	@Autowired(required = false)
	private ResumenSolicitudService resumenService;

	/**
	 * Demostración completa del ciclo de vida de una solicitud (RF01-RF09)
	 * Ejecuta automáticamente al iniciar la aplicación
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void demostracionCompleta() {
		System.out.println("\n[DEMOSTRACION COMPLETA: CICLO DE VIDA DE UNA SOLICITUD (RF01-RF09)]");
		System.out.println("=".repeat(70));
		
		try {
			// Crear usuarios
			System.out.println("[PASO 1] Creando usuarios del sistema...");
			Usuario estudiante = crearUsuario(1L, "Juan Pérez", "1001234567", Rol.ESTUDIANTE);
			Usuario docente = crearUsuario(2L, "Prof. María García", "1101234567", Rol.DOCENTE);
			Usuario coordinador = crearUsuario(3L, "Coordinador Luis", "1201234567", Rol.COORDINADOR);
			System.out.println("[OK] Usuarios creados: Estudiante, Docente, Coordinador\n");
			
			// RF01: Registrar solicitud
			System.out.println("[PASO 2] RF-01 - Registrando solicitud...");
			Solicitud solicitud = crearSolicitud(estudiante);
			System.out.println("   ID Solicitud: " + solicitud.getId());
			System.out.println("   Estado: " + solicitud.getEstado());
			System.out.println("   Tipo: " + solicitud.getTipo());
			System.out.println("[OK] Solicitud registrada en estado REGISTRADA\n");
			
			// RF02: Clasificar solicitud
			System.out.println("[PASO 3] RF-02 - Clasificando solicitud...");
			try {
				solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, coordinador, 
					"Solicitud clasificada como registro de asignatura");
				System.out.println("   Tipo: " + solicitud.getTipo());
				System.out.println("   Estado: " + solicitud.getEstado());
				System.out.println("[OK] Solicitud clasificada correctamente\n");
			} catch (SolicitudException e) {
				System.out.println("   Error: " + e.getMessage());
			}
			
			// RF03: Priorizar solicitud
			System.out.println("[PASO 4] RF-03 - Priorizando solicitud...");
			try {
				solicitud.priorizarSolicitud(NivelPrioridad.ALTA, 
					"Solicitud urgente para matriculación de fin de semestre");
				System.out.println("   Prioridad: " + solicitud.getPrioridad().getNivel());
				System.out.println("   Justificación: " + solicitud.getPrioridad().getDescripcion());
				System.out.println("[OK] Solicitud priorizada como ALTA\n");
			} catch (Exception e) {
				System.out.println("   Error: " + e.getMessage());
			}
			
			// RF04/RF05: Asignar responsable
			System.out.println("[PASO 5] RF-04/RF-05 - Asignando responsable...");
			try {
				solicitud.asignarResponsable(docente, 
					"Solicitud asignada al docente para revisión");
				System.out.println("   Estado: " + solicitud.getEstado());
				System.out.println("   Responsable: " + docente.getNombre());
				System.out.println("[OK] Solicitud asignada correctamente\n");
			} catch (Exception e) {
				System.out.println("   Error: " + e.getMessage());
			}
			
			// RF06: Historial
			System.out.println("[PASO 6] RF-06 - Visualizando historial...");
			System.out.println("   Total de cambios registrados: " + solicitud.getHistorial().size());
			if (!solicitud.getHistorial().isEmpty()) {
				System.out.println("   Ultimos eventos:");
				solicitud.getHistorial().stream()
					.skip(Math.max(0, solicitud.getHistorial().size() - 3))
					.forEach(h -> System.out.println("      - " + h.getAccion() + ": " + h.getObservacion()));
			}
			System.out.println("[OK] Historial consultado exitosamente\n");
			
			// Atender solicitud (previo a cierre)
			System.out.println("[PASO 7] Atendiendo solicitud (previo a cierre)...");
			try {
				solicitud.atenderSolicitud(docente, "Solicitud atendida y resuelta satisfactoriamente");
				System.out.println("   Estado: " + solicitud.getEstado());
				System.out.println("[OK] Solicitud atendida correctamente\n");
			} catch (Exception e) {
				System.out.println("   Error: " + e.getMessage());
			}
			
			// RF08: Cerrar solicitud
			System.out.println("[PASO 8] RF-08 - Cerrando solicitud...");
			try {
				solicitud.cerrarSolicitud(coordinador, 
					"Solicitud cerrada después de resolución satisfactoria");
				System.out.println("   Estado: " + solicitud.getEstado());
				System.out.println("   Fecha de Cierre: " + solicitud.getFechaCierre());
				System.out.println("[OK] Solicitud cerrada correctamente\n");
			} catch (SolicitudException e) {
				System.out.println("   Error: " + e.getMessage());
			}
			
			// RF09: Generar resumen
			System.out.println("[PASO 9] RF-09 - Generando resumen con IA (Gemini)...");
			try {
				// Crear solicitud con ID para el resumen (simulando persistencia)
				String resumen = resumenService != null 
					? resumenService.generarResumenSolicitud(solicitud)
					: generarResumenFallback(solicitud);
				
				System.out.println("[RESUMEN GENERADO]");
				System.out.println("-".repeat(60));
				System.out.println(resumen.substring(0, Math.min(resumen.length(), 500)));
				System.out.println("-".repeat(60));
				System.out.println("[OK] Resumen generado exitosamente\n");
			} catch (Exception e) {
				System.out.println("   Error al generar resumen: " + e.getMessage());
				System.out.println("   Usando fallback...\n");
			}
			
		} catch (Exception e) {
			System.err.println("[ERROR] Error en la demostración: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper: Crear un usuario
	 */
	private Usuario crearUsuario(Long id, String nombre, String identificacion, Rol rol) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNombre(nombre);
		usuario.setIdentificacion(identificacion);
		usuario.setActivo(true);
		usuario.setRol(rol);
		return usuario;
	}

	/**
	 * Helper: Crear una solicitud inicial (RF01)
	 */
	private Solicitud crearSolicitud(Usuario estudiante) {
		return new Solicitud(
			TipoSolicitud.REGISTRO_ASIGNATURA,
			"Solicitud de inscripción a curso de Programación Avanzada para completar plan de estudios",
			CanalOrigen.PORTAL_WEB,
			LocalDateTime.now(),
			estudiante.getIdentificacion(),
			null, null, estudiante, null
		);
	}

	/**
	 * Helper: Fallback para resumen si Gemini no está disponible
	 */
	private String generarResumenFallback(Solicitud solicitud) {
		StringBuilder resumen = new StringBuilder();
		resumen.append("═══════════════════════════════════════════\n");
		resumen.append("    RESUMEN DE SOLICITUD ACADÉMICA\n");
		resumen.append("═══════════════════════════════════════════\n\n");
		resumen.append(" INFORMACIÓN GENERAL:\n");
		resumen.append("  • Tipo: ").append(solicitud.getTipo()).append("\n");
		resumen.append("  • Estado: ").append(solicitud.getEstado()).append("\n");
		resumen.append("  • Solicitante: ").append(solicitud.getIdentificacionSolicitante()).append("\n");
		resumen.append("  • Descripción: ").append(solicitud.getDescripcion()).append("\n");
		if (solicitud.getPrioridad() != null) {
			resumen.append("  • Prioridad: ").append(solicitud.getPrioridad().getNivel()).append("\n");
		}
		resumen.append("  • Cambios registrados: ").append(solicitud.getHistorial() != null ? solicitud.getHistorial().size() : 0).append("\n");
		resumen.append("\n═══════════════════════════════════════════\n");
		return resumen.toString();
	}


}
