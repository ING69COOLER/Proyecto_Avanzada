# Sistema de Gestión de Solicitudes Académicas

### Universidad del Quindío — Proyecto Programación Avanzada

---

## Integrantes

| Nombre                         | Identificación |
| ------------------------------ | -------------- |
| Manuel Pineda Varela           | 1092455543     |
| Santiago Solarte Trujillo      | 1059355828     |
| Carlos Alonso Barahona Alvarez | 1094896340     |

---

## Descripción del Proyecto

Este proyecto es un sistema de gestión de solicitudes académicas desarrollado para la **Universidad del Quindío**, implementado con principios de **Domain-Driven Design (DDD)** y arquitectura limpia.

El sistema permite gestionar el ciclo de vida completo de una solicitud académica desde su registro hasta su cierre, garantizando trazabilidad, control de acceso por roles y soporte opcional de inteligencia artificial para la generación de resúmenes y sugerencias de clasificación.

---

## Funcionalidades principales

| RF    | Descripción                                                                              | Cobertura       |
| ----- | ---------------------------------------------------------------------------------------- | --------------- |
| RF-01 | Registro de solicitudes académicas (tipo, descripción, canal, fecha, solicitante)        | Diseño + Código |
| RF-02 | Clasificación de solicitudes por tipo (homologación, cupos, cancelación, etc.)           | Diseño + Código |
| RF-03 | Priorización de solicitudes con justificación obligatoria (ALTA / MEDIA / BAJA)          | Diseño + Código |
| RF-04 | Gestión del ciclo de vida: `REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA` | Diseño + Código |
| RF-06 | Historial auditable automático de cada acción sobre la solicitud                         | Diseño + Código |
| RF-08 | Cierre de solicitudes con validaciones estrictas de estado y observación                 | Diseño + Código |
| RF-11 | Funcionamiento independiente de IA (fallback a resumen estructurado)                     | Diseño          |
| RF-13 | Control de autorización por roles: ESTUDIANTE, COORDINADOR, DOCENTE                      | Diseño + Código |

---

## Roles del sistema

* **ESTUDIANTE / ADMINISTRATIVO** → pueden registrar solicitudes
* **COORDINADOR** → puede clasificar, priorizar, asignar responsable y cerrar solicitudes
* **DOCENTE** → puede atender solicitudes asignadas

---

## Tecnologías

* Java 17+
* Spring Boot
* JUnit 5
* Gradle (Wrapper incluido)

---

## Compilar y ejecutar el proyecto

### Prerrequisitos

* Tener instalado **Java 17** o superior
* No es necesario instalar Gradle (el proyecto incluye `gradlew`)

Verifica tu versión de Java:

```bash
java -version
```

---

## Clonar el repositorio

```bash
git clone https://github.com/ING69COOLER/Proyecto_Avanzada.git
cd Proyecto_Avanzada
```

---

## Compilar el proyecto

```bash
./gradlew build
```

En Windows:

```bash
gradlew.bat build
```

---

## Ejecutar pruebas

```bash
./gradlew test
```

En Windows:

```bash
gradlew.bat test
```

---

## Ejecutar pruebas específicas

```bash
./gradlew test --tests "RF01_RegistroSolicitudTest"
```

Ejemplo:

```bash
./gradlew test --tests "RF08_CierreSolicitudesTest"
```

---

## Ver reporte de pruebas

Los resultados se generan en:

```
build/reports/tests/test/index.html
```

Abre ese archivo en el navegador para ver el reporte completo.

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

---

## Notas importantes

* El proyecto sigue un enfoque **Domain-First**, donde el dominio define la lógica y la futura API REST se construye a partir de él.
* Actualmente se encuentra en fase de diseño y lógica de dominio; la capa REST (controllers) se implementará posteriormente.
* El proyecto utiliza **Gradle Wrapper**, por lo que no es necesario instalar Gradle manualmente.

---
