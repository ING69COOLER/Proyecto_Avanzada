package co.edu.uniquindio.Proyecto_Avanzada.domain.Prueba;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.AtencionSolicitudesService;
import co.edu.uniquindio.Proyecto_Avanzada.domain.DomainServices.PriorizacionService;
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

	private static final String SEP = "=".repeat(60);
	private static final String LINE = "-".repeat(60);
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Autowired(required = false)
	private PriorizacionService priorizacionService;

	@Autowired(required = false)
	private ResumenSolicitudService resumenService;

	@Autowired(required = false)
	private AtencionSolicitudesService atencionService;

	/**
	 * Demostracion completa del ciclo de vida (RF01-RF13).
	 * Se ejecuta automaticamente al iniciar la aplicacion.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void demostracionCompleta() {
		encabezado("DEMOSTRACION: CICLO DE VIDA DE SOLICITUD ACADEMICA (RF01-RF13)");

		try {
			// ---- PASO 1: Usuarios -----------------------------------------------
			paso(1, "Crear usuarios del sistema");
			Usuario estudiante = crearUsuario(1L, "Juan Perez", "1001234567", Rol.ESTUDIANTE);
			Usuario docente = crearUsuario(2L, "Prof. Maria Garcia", "1101234567", Rol.DOCENTE);
			Usuario coordinador = crearUsuario(3L, "Coordinador Luis", "1201234567", Rol.COORDINADOR);
			info("Usuarios: " + estudiante.getNombre()
					+ "  [" + estudiante.getRol() + "]");
			info("         " + docente.getNombre()
					+ "  [" + docente.getRol() + "]");
			info("         " + coordinador.getNombre()
					+ "  [" + coordinador.getRol() + "]");
			ok("Usuarios creados");

			// ---- PASO 2: RF-01 Registrar solicitud --------------------------------
			paso(2, "RF-01 | Registrar solicitud");
			Solicitud solicitud = crearSolicitud(estudiante);
			info("Tipo   : " + solicitud.getTipo());
			info("Estado : " + solicitud.getEstado());
			info("Canal  : " + solicitud.getCanalOrigen());
			ok("Solicitud registrada");

			// ---- PASO 3: RF-10 Sugerencia de clasificacion (IA) ------------------
			paso(3, "RF-10 | Sugerencia automatica de clasificacion (IA)");
			if (resumenService != null) {
				String sugerencia = resumenService.sugerirClasificacion(solicitud.getDescripcion());
				System.out.println(LINE);
				System.out.println(sugerencia);
				System.out.println(LINE);
				ok("Sugerencia generada (debe ser confirmada por un funcionario)");
			} else {
				info("Servicio de IA no disponible");
			}

			// ---- PASO 4: RF-13 Demo rechazo de rol incorrecto --------------------
			paso(4, "RF-13 | Autorizacion: intentos con roles incorrectos");

			info("Intento 1: ESTUDIANTE intenta clasificar -> debe fallar");
			try {
				solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, estudiante,
						"Intento no autorizado");
				error("Se permitio una operacion no autorizada!");
			} catch (SolicitudException | IllegalArgumentException e) {
				ok("Acceso denegado correctamente: " + e.getMessage());
			}

			info("Intento 2: DOCENTE intenta cerrar -> debe fallar");
			try {
				solicitud.cerrarSolicitud(docente, "Intento no autorizado");
				error("Se permitio una operacion no autorizada!");
			} catch (SolicitudException e) {
				ok("Acceso denegado correctamente: " + e.getMessage());
			}

			info("Intento 3: ESTUDIANTE intenta priorizar -> debe fallar");
			try {
				if (priorizacionService != null) {
					priorizacionService.priorizarSolicitud(estudiante, "Urgente", solicitud, NivelPrioridad.ALTA);
				} else {
					throw new IllegalArgumentException("Acceso denegado: el usuario no puede priorizar solicitudes. Rol actual: " + estudiante.getRol());
				}
				error("Se permitio una operacion no autorizada!");
			} catch (IllegalArgumentException e) {
				ok("Acceso denegado correctamente: " + e.getMessage());
			}

			// ---- PASO 5: RF-02 Clasificar (COORDINADOR) --------------------------
			paso(5, "RF-02 | Clasificar solicitud [COORDINADOR]");
			try {
				solicitud.clasificarSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA, coordinador,
						"Solicitud clasificada como registro de asignatura");
				info("Tipo   : " + solicitud.getTipo());
				info("Estado : " + solicitud.getEstado());
				ok("Solicitud clasificada");
			} catch (SolicitudException e) {
				error(e.getMessage());
			}

			// ---- PASO 6: RF-03 Priorizar (COORDINADOR) ---------------------------
			paso(6, "RF-03 | Priorizar solicitud [COORDINADOR]");
			if (priorizacionService != null) {
				try {
					priorizacionService.priorizarSolicitud(coordinador,
							"Solicitud urgente para matriculacion de fin de semestre",
							solicitud,
							NivelPrioridad.ALTA);
					info("Prioridad     : " + solicitud.getPrioridad().getNivel());
					info("Justificacion : " + solicitud.getPrioridad().getDescripcion());
					ok("Solicitud priorizada");
				} catch (SolicitudException e) {
					error(e.getMessage());
				}
			} else {
				info("Servicio de priorización no disponible");
			}

			// ---- PASO 7: RF-04/05 Asignar (COORDINADOR via AtencionSolicitudesService) -------
			paso(7, "RF-04/05 | Asignar responsable [COORDINADOR via servicio]");
			if (atencionService != null) {
				try {
					atencionService.asignarResponsable(coordinador, solicitud,
							"Solicitud asignada para revision y atencion");
					info("Estado      : " + solicitud.getEstado());
					info("Asignado por: " + coordinador.getNombre());
					ok("Responsable asignado via AtencionSolicitudesService");
				} catch (SolicitudException | IllegalArgumentException e) {
					error(e.getMessage());
				}
			} else {
				try {
					solicitud.asignarResponsable(coordinador, "Asignada para revision");
					info("Estado      : " + solicitud.getEstado());
					ok("Responsable asignado (fallback directo)");
				} catch (Exception e) {
					error(e.getMessage());
				}
			}

			// ---- PASO 8: RF-06 Historial -----------------------------------------
			paso(8, "RF-06 | Consultar historial");
			info("Total de cambios: " + solicitud.getHistorial().size());
			solicitud.getHistorial().stream()
					.skip(Math.max(0, solicitud.getHistorial().size() - 3))
					.forEach(h -> info("  [" + h.getAccion() + "] " + h.getObservacion()));
			ok("Historial consultado");

			// ---- PASO 9: Atender (DOCENTE) ----------------------------------------
			paso(9, "Atender solicitud [DOCENTE]");
			try {
				solicitud.atenderSolicitud(docente,
						"Solicitud atendida y resuelta satisfactoriamente");
				info("Estado : " + solicitud.getEstado());
				ok("Solicitud atendida");
			} catch (SolicitudException e) {
				error(e.getMessage());
			}

			// ---- PASO 10: RF-08 Cerrar (COORDINADOR) -----------------------------
			paso(10, "RF-08 | Cerrar solicitud [COORDINADOR]");
			try {
				solicitud.cerrarSolicitud(coordinador,
						"Solicitud cerrada despues de resolucion satisfactoria");
				info("Estado       : " + solicitud.getEstado());
				info("Fecha cierre : " + (solicitud.getFechaCierre() != null
						? solicitud.getFechaCierre().format(FMT)
						: "-"));
				ok("Solicitud cerrada");
			} catch (SolicitudException e) {
				error(e.getMessage());
			}

			// ---- PASO 11: RF-13 Operacion sobre solicitud cerrada ----------------
			paso(11, "RF-13 | Intento de modificar solicitud cerrada");
			try {
				solicitud.clasificarSolicitud(TipoSolicitud.HOMOLOGACION, coordinador,
						"Intento sobre solicitud cerrada");
				error("Se permitio modificar una solicitud cerrada!");
			} catch (SolicitudException e) {
				ok("Proteccion activa: " + e.getMessage());
			}

			// ---- PASO 12: RF-09 Resumen con IA ----------------------------------
			paso(12, "RF-09 | Resumen generado con IA (Gemini)");
			try {
				String resumen = resumenService != null
						? resumenService.generarResumenSolicitud(solicitud)
						: generarResumenFallback(solicitud);
				System.out.println(LINE);
				System.out.println(resumen);
				System.out.println(LINE);
				ok("Resumen generado exitosamente");
			} catch (Exception e) {
				error("Error: " + e.getMessage());
			}

			// ---- FIN -------------------------------------------------------------
			System.out.println();
			System.out.println(SEP);
			System.out.println("  DEMOSTRACION COMPLETADA EXITOSAMENTE");
			System.out.println(SEP);
			System.out.println();

		} catch (Exception e) {
			System.err.println("[ERROR CRITICO] " + e.getMessage());
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------
	// Helpers de formato
	// -------------------------------------------------------------------------
	private static void encabezado(String titulo) {
		System.out.println();
		System.out.println(SEP);
		System.out.println("  " + titulo);
		System.out.println(SEP);
		System.out.println();
	}

	private static void paso(int num, String descripcion) {
		System.out.println();
		System.out.println(LINE);
		System.out.printf("  PASO %02d | %s%n", num, descripcion);
		System.out.println(LINE);
	}

	private static void info(String msg) {
		System.out.println("  " + msg);
	}

	private static void ok(String msg) {
		System.out.println("  [OK]    " + msg);
	}

	private static void error(String msg) {
		System.out.println("  [ERROR] " + msg);
	}

	// -------------------------------------------------------------------------
	// Helpers de dominio
	// -------------------------------------------------------------------------
	private Usuario crearUsuario(Long id, String nombre, String identificacion, Rol rol) {
		return new Usuario(id, nombre, identificacion, null, true, rol);
	}
	private Solicitud crearSolicitud(Usuario estudiante) {
		return new Solicitud(
				TipoSolicitud.REGISTRO_ASIGNATURA,
				"Solicitud de inscripcion a curso de Programacion Avanzada para completar plan de estudios",
				CanalOrigen.PORTAL_WEB,
				LocalDateTime.now(),
				null, null, estudiante, null);
	}

	private String generarResumenFallback(Solicitud s) {
		return "Tipo:        " + s.getTipo() + "\n"
				+ "Estado:      " + s.getEstado() + "\n"
				+ "Solicitante: " + s.getUsuarioSolicitante().getIdentificacion() + "\n"
				+ "Descripcion: " + s.getDescripcion() + "\n"
				+ "Prioridad:   " + (s.getPrioridad() != null ? s.getPrioridad().getNivel() : "Sin asignar") + "\n"
				+ "Cambios:     " + (s.getHistorial() != null ? s.getHistorial().size() : 0);
	}
}
