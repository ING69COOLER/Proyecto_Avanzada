# Sistema de Gestión de Solicitudes Académicas
### Universidad del Quindío — Proyecto Programación Avanzada


## Integrantes
|Manuel Pineda Varela|1092455543|
|Santiago Solarte Trujillo|1059355828|
|Carlos Alonso Barahona Alvarez|1094896340|

## Descripción del Proyecto

Este proyecto es un sistema de gestión de solicitudes académicas desarrollado para la **Universidad del Quindío**, implementado con principios de **Domain-Driven Design (DDD)** y arquitectura limpia.

El sistema permite gestionar el ciclo de vida completo de una solicitud académica —desde su registro hasta su cierre— garantizando trazabilidad, control de acceso por roles y soporte opcional de inteligencia artificial para la generación de resúmenes y sugerencias de clasificación.

### Funcionalidades principales

| RF | Descripción | Cobertura |
|----|-------------|-----------|
| RF-01 | Registro de solicitudes académicas (tipo, descripción, canal, fecha, solicitante) | Diseño + Código |
| RF-02 | Clasificación de solicitudes por tipo (homologación, cupos, cancelación, etc.) | Diseño + Código |
| RF-03 | Priorización de solicitudes con justificación obligatoria (ALTA / MEDIA / BAJA) | Diseño + Código |
| RF-04 | Gestión del ciclo de vida: `REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA` | Diseño + Código |
| RF-06 | Historial auditable automático de cada acción sobre la solicitud | Diseño + Código |
| RF-08 | Cierre de solicitudes con validaciones estrictas de estado y observación | Diseño + Código |
| RF-11 | Funcionamiento independiente de IA (fallback a resumen estructurado) | Diseño |
| RF-13 | Control de autorización por roles: ESTUDIANTE, COORDINADOR, DOCENTE | Diseño + Código |

### Roles del sistema

- **ESTUDIANTE / ADMINISTRATIVO** — pueden registrar solicitudes.
- **COORDINADOR** — puede clasificar, priorizar, asignar responsable y cerrar solicitudes.
- **DOCENTE** — puede atender solicitudes que le fueron asignadas.

---

## Tecnologías

- Java 17+
- Spring Boot
- JUnit 5
- Maven

## Compilar y ejecutar las pruebas

### Prerrequisitos

- Tener instalado **Java 17** o superior.
- Tener instalado **Maven 3.8+**.

Verifica las versiones con:

```bash
java -version
mvn -version
```

### Clonar el repositorio

```bash
git clone https://github.com/ING69COOLER/Proyecto_Avanzada.git
cd Proyecto_Avanzada
```

### Compilar el proyecto

```bash
mvn clean compile
```

### Ejecutar todas las pruebas

```bash
mvn test
```

### Ejecutar las pruebas de un RF específico

```bash
# Ejemplo: solo RF-01
mvn test -Dtest=RF01_RegistroSolicitudTest

# Ejemplo: solo RF-08
mvn test -Dtest=RF08_CierreSolicitudesTest
```

### Ver reporte de pruebas

Los resultados se generan en:

```
target/surefire-reports/
```

Para generar un reporte HTML navegable:

```bash
mvn surefire-report:report
# El reporte queda en: target/site/surefire-report.html
```

---

## Estructura del proyecto

```
src/
├── main/java/co/edu/uniquindio/Proyecto_Avanzada/
│   └── domain/
│       ├── entities/          # Agregados: Solicitud, Usuario, HistorialSolicitud
│       ├── valueobjects/      # Enums y VOs: Prioridad, Rol, EstadoSolicitud, etc.
│       ├── DomainServices/    # Servicios de dominio por RF
│       ├── exception/         # SolicitudException
│       └── repos/             # Interfaces e implementaciones de repositorios
└── test/java/co/edu/uniquindio/Proyecto_Avanzada/
    └── domain/                # Pruebas unitarias por RF (RF01 a RF13)
```