# Proyecto Avanzada - Trazabilidad de Requisitos

Mapeo de cada Requisito Funcional (RF) a las clases que lo implementan.

---

## RF-01 · Registro de solicitudes académicas

> Registrar tipo, descripción, canal de origen, fecha/hora e identificación del solicitante.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | Constructor principal — valida y almacena los campos obligatorios. Registra la primera entrada en el historial. |
| `domain/entities/Usuario.java` | `puedeRegistrarSolicitud()` — verifica que el usuario sea ESTUDIANTE o ADMINISTRATIVO. |
| `domain/repos/repoImplementation/RepositorioSolicitud.java` | `guardarSolicitud()` — persiste la solicitud en memoria. |

---

## RF-02 · Clasificación de solicitudes

> Clasificar la solicitud por tipo (registro, homologación, cancelación, cupos, consulta).

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | `clasificarSolicitud()` — cambia el tipo y transiciona el estado a CLASIFICADA. |
| `domain/DomainServices/ClasificacionSolicitudesService.java` | Orquesta la clasificación, valida parámetros, guarda en repositorio. |
| `domain/entities/Usuario.java` | `puedeClasificarSolicitud()` — solo COORDINADOR puede clasificar (RN2). |

---

## RF-03 · Priorización de solicitudes

> Asignar prioridad (ALTA/MEDIA/BAJA) con justificación.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | `priorizarSolicitud()` — asigna el objeto `Prioridad` a la solicitud. |
| `domain/DomainServices/PriorizacionService.java` | Orquesta la priorización y valida el rol del usuario (RN3). |
| `domain/entities/Usuario.java` | `puedePriorizar()` — solo COORDINADOR puede priorizar. |
| `domain/valueobjects/Prioridad.java` | Value Object que encapsula nivel y justificación. |
| `domain/valueobjects/NivelPrioridad.java` | Enum con los niveles: ALTA, MEDIA, BAJA. |

---

## RF-04 · Gestión del ciclo de vida de la solicitud

> Estados: REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | Gestiona todas las transiciones de estado en sus métodos. `validarNoEsterrada()` impide modificar solicitudes cerradas. |
| `domain/DomainServices/AtencionSolicitudesService.java` | `asignarResponsable()` → EN_ATENCION. `atenderSolicitud()` → ATENDIDA. |
| `domain/valueobjects/EstadoSolicitud.java` | Enum con los estados del ciclo de vida. |

---

## RF-05 · Asignación de responsables

> Asignar solicitud a un responsable activo; la asignación queda en el historial.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | `asignarResponsable()` — cambia estado a EN_ATENCION y registra en historial. `UsuarioPuedeAtender()` — verifica si el usuario fue asignado previamente. |
| `domain/DomainServices/AtencionSolicitudesService.java` | `asignarResponsable()` — valida que el usuario esté activo y sea COORDINADOR. |
| `domain/entities/Usuario.java` | `puedeAsignar()` — solo COORDINADOR puede asignar. |

---

## RF-06 · Registro del historial de la solicitud

> Historial auditable con fecha/hora, acción, usuario responsable y observación.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | `crearHistoria()` (privado) — llamado automáticamente en cada operación. |
| `domain/entities/HistorialSolicitud.java` | Entidad que representa una entrada del historial. |
| `domain/valueobjects/TipoAccion.java` | Enum con los tipos de acción (CREACION, CLASIFICADA, ASIGNACION, CAMBIO_ESTADO, CIERRE). |

---

## RF-07 · Consulta de solicitudes

> Consultar por estado, tipo, prioridad y responsable asignado.

| Clase | Rol |
|---|---|
| `domain/repos/repoImplementation/RepositorioSolicitud.java` | `consultarEstado()`, `consultarTipoSolicitud()`, `consultarPrioridad()`, `consultarResponsable()`. |
| `domain/repos/repoInterfaces/IRepositorioSolicitud.java` | Interfaz que define el contrato de consulta. |

---

## RF-08 · Cierre de solicitudes

> Cerrar solo si está ATENDIDA, con observación obligatoria. Solicitud cerrada no es modificable.

| Clase | Rol |
|---|---|
| `domain/entities/Solicitud.java` | `cerrarSolicitud()` — valida estado ATENDIDA, observación y rol. `validarNoEsterrada()` — bloquea cualquier operación sobre solicitudes cerradas. |
| `domain/entities/Usuario.java` | `puedeCerrarSolicitud()` — solo COORDINADOR puede cerrar. |

---

## RF-09 · Generación de resúmenes con IA *(opcional)*

> Resumen textual del estado e historial usando un modelo de lenguaje externo.

| Clase | Rol |
|---|---|
| `domain/DomainServices/ResumenSolicitudService.java` | `generarResumenSolicitud()` — orquesta la generación (IA o fallback). `generarResumenBasico()` — fallback local sin IA. |
| `domain/DomainServices/ModeloLenguajeOpenAI.java` | `generarResumen()` — llama a la API REST de Gemini y parsea la respuesta. `generarResumenFallback()` — fallback sin IA cuando la API no está disponible. |

---

## RF-10 · Sugerencia automática de clasificación *(opcional)*

> Sugerir tipo y prioridad a partir del texto; debe ser confirmada por un humano.

| Clase | Rol |
|---|---|
| `domain/DomainServices/ResumenSolicitudService.java` | `sugerirClasificacion()` — delega al modelo de IA si está disponible. |
| `domain/DomainServices/ModeloLenguajeOpenAI.java` | `sugerirClasificacion()` — genera la sugerencia con Gemini. `sugerirClasificacionFallback()` — búsqueda por palabras clave cuando la IA no está disponible. |

---

## RF-11 · Funcionamiento independiente de IA

> El sistema debe operar sin modelos de lenguaje externos.

| Clase | Rol |
|---|---|
| `domain/DomainServices/ModeloLenguajeOpenAI.java` | `init()` — detecta si la API key está disponible; si no, desactiva la IA. Métodos `*Fallback()` — implementan versiones locales de RF-09 y RF-10. |
| `domain/DomainServices/ResumenSolicitudService.java` | Usa `@Autowired(required = false)` para el modelo; si es `null`, usa el resumen básico local. |
| `domain/repos/repoImplementation/RepositorioSolicitud.java` | Almacenamiento en memoria — no depende de servicios externos. |

---

## RF-13 · Autorización básica de operaciones

> Restringir operaciones según el rol del usuario.

| Clase | Rol |
|---|---|
| `domain/entities/Usuario.java` | Métodos de permiso: `puedeRegistrarSolicitud()`, `puedeClasificarSolicitud()`, `puedePriorizar()`, `puedeAsignar()`, `puedeAtender()`, `puedeCerrarSolicitud()`. |
| `domain/entities/Solicitud.java` | Todos los métodos de operación validan el rol antes de ejecutarse y lanzan `SolicitudException` si el acceso es denegado. |
| `domain/DomainServices/ClasificacionSolicitudesService.java` | Valida rol COORDINADOR antes de clasificar. |
| `domain/DomainServices/AtencionSolicitudesService.java` | Valida rol COORDINADOR para asignar y DOCENTE para atender. |
| `domain/DomainServices/PriorizacionService.java` | Valida rol COORDINADOR para priorizar. |
| `domain/valueobjects/Rol.java` | Enum con los roles: ESTUDIANTE, DOCENTE, COORDINADOR, ADMINISTRATIVO. |
| `domain/exception/SolicitudException.java` | Excepción lanzada cuando se deniega el acceso por rol. |

---

## Resumen visual por clase

```
Solicitud.java          → RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-08, RF-13
Usuario.java            → RF-01, RF-02, RF-03, RF-04, RF-05, RF-08, RF-13
ClasificacionService    → RF-02, RF-07, RF-13
AtencionService         → RF-04, RF-05, RF-06, RF-13
PriorizacionService     → RF-03, RF-13
RepositorioSolicitud    → RF-01, RF-07, RF-11
ResumenSolicitudService → RF-09, RF-10, RF-11
ModeloLenguajeOpenAI   → RF-09, RF-10, RF-11
Prueba.java             → Demo RF-01...RF-13
```
