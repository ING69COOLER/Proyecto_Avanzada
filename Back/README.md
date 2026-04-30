# Sistema de Gestion de Solicitudes Academicas

### Universidad del Quindio - Proyecto Programacion Avanzada

## Integrantes

| Nombre |
| --- |
| Manuel Pineda |
| Santiago Solarte |
| Carlos Alonso Barahona |

## Descripcion

Sistema backend para la gestion de solicitudes academicas de la Universidad del Quindio. El proyecto esta construido con Spring Boot y sigue una organizacion inspirada en Domain-Driven Design, arquitectura limpia y separacion por capas.

La aplicacion permite registrar usuarios, autenticar con JWT, crear solicitudes academicas, consultar solicitudes filtradas, clasificar, priorizar, asignar responsables, cambiar estados, cerrar solicitudes y conservar un historial auditable de los eventos de cada solicitud.

## Estado actual del proyecto

El proyecto ya cuenta con:

- Modelo de dominio para `Solicitud`, `Usuario` e `HistorialSolicitud`.
- Value objects y enums de negocio para roles, estados, tipos de solicitud, prioridad, acciones y canales de origen.
- Servicios de dominio para registro, consulta, clasificacion, priorizacion, atencion, cierre y resumen de solicitudes.
- Casos de uso en la capa de aplicacion.
- DTOs de entrada y salida con validaciones.
- API REST con controladores para autenticacion, usuarios y solicitudes.
- Seguridad con Spring Security, BCrypt y JWT.
- Persistencia con Spring Data JPA.
- Adapters JPA para usuarios y solicitudes.
- Integracion con Google Gemini para resumenes y sugerencias mediante puerto de modelo.
- Contrato OpenAPI en `docs/api/OpenAPI.yaml`.
- Pruebas unitarias y de contexto con JUnit 5.

## Funcionalidades principales

| RF | Descripcion | Estado |
| --- | --- | --- |
| RF-01 | Registro de solicitudes academicas | Implementado |
| RF-02 | Clasificacion de solicitudes por tipo | Implementado |
| RF-03 | Priorizacion de solicitudes con justificacion | Implementado |
| RF-04 | Gestion del ciclo de vida de la solicitud | Implementado |
| RF-06 | Historial auditable de acciones | Implementado |
| RF-08 | Cierre de solicitudes con validaciones | Implementado |
| RF-09 | Generacion de resumenes con IA | Implementado |
| RF-10 | Sugerencia automatica de clasificacion/prioridad | Implementado |
| RF-11 | Funcionamiento con fallback si la IA falla | Parcial |
| RF-13 | Autorizacion por roles y autenticacion JWT | Implementado |

## Roles del sistema

- `ESTUDIANTE`: puede registrar solicitudes academicas.
- `ADMINISTRATIVO`: puede registrar solicitudes academicas.
- `COORDINADOR`: puede clasificar, priorizar, asignar responsables y cerrar solicitudes.
- `DOCENTE`: puede atender solicitudes asignadas.

## Tecnologias

- Java 25, configurado en el toolchain de Gradle.
- Spring Boot 4.0.2.
- Spring Web.
- Spring Validation.
- Spring Security.
- Spring Data JPA.
- JWT con `jjwt`.
- PostgreSQL y H2.
- Lombok.
- MapStruct.
- JUnit 5.
- Gradle Wrapper.

## Arquitectura

El codigo principal esta en `src/main/java/co/edu/uniquindio/Proyecto_Avanzada`.

```text
src/main/java/co/edu/uniquindio/Proyecto_Avanzada/
|-- application/
|   |-- dto/                 # Requests, responses y enums de API
|   |-- mapper/              # Mappers de DTOs y enums
|   |-- security/            # Servicio de autenticacion
|   `-- usecase/             # Casos de uso de la aplicacion
|-- domain/
|   |-- entities/            # Solicitud, Usuario, HistorialSolicitud
|   |-- exception/           # Excepciones de dominio
|   |-- ports/out/           # Puertos hacia infraestructura
|   |-- services/            # Servicios de dominio
|   `-- valueobjects/        # Estados, roles, prioridad, tipos, canales
`-- infrastructure/
    |-- inbound/controllers/ # API REST y manejo global de errores
    |-- outbound/database/   # Persistencia JPA
    |-- outbound/external/   # Adapter de Gemini
    `-- security/            # Configuracion de seguridad y JWT
```

## API REST

La aplicacion expone endpoints bajo `http://localhost:8082` segun la configuracion actual.

### Autenticacion

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/auth/login` | Autentica un usuario y retorna un token JWT. |
| POST | `/auth/register` | Registra un usuario y retorna un token JWT. |

### Usuarios

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/usuarios/registro` | Registra un usuario en el sistema. |

### Solicitudes

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/solicitudes` | Crea una solicitud academica. |
| GET | `/api/solicitudes` | Consulta solicitudes con filtros y paginacion. |
| GET | `/api/solicitudes/{codigo}` | Obtiene el detalle de una solicitud. |
| PATCH | `/api/solicitudes/{codigo}/clasificacion` | Clasifica una solicitud. |
| PATCH | `/api/solicitudes/{codigo}/prioridad` | Asigna prioridad a una solicitud. |
| PATCH | `/api/solicitudes/{codigo}/asignacion` | Asigna responsable a una solicitud. |
| PATCH | `/api/solicitudes/{codigo}/estado` | Cambia el estado de una solicitud. |
| PATCH | `/api/solicitudes/{codigo}/cierre` | Cierra una solicitud. |
| GET | `/api/solicitudes/{codigo}/historial` | Consulta el historial de una solicitud. |

El contrato completo esta documentado en:

```text
docs/api/OpenAPI.yaml
```

## Configuracion local

El archivo `application-example.properties` contiene una referencia de configuracion. Para ejecutar localmente se usa `src/main/resources/application.properties`.

Configuracion principal esperada:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.datasource.url=jdbc:postgresql://localhost:5433/proyecto_avanzada
spring.datasource.username=postgres
spring.datasource.password=admin

server.port=8082

jwt.expiry=5
jwt.secret=CAMBIAR_ESTE_VALOR_EN_LOCAL

gemini.api.key=${GOOGLE_API_KEY:}
```

Notas:

- La base de datos configurada por defecto es PostgreSQL en el puerto `5433`.
- El proyecto tambien incluye dependencia de H2 para pruebas o ejecuciones alternativas.
- La API key de Gemini debe manejarse como variable de entorno (`GOOGLE_API_KEY`) y no subirse al repositorio.
- El secreto JWT debe cambiarse fuera de ambientes de prueba.

## Ejecutar el proyecto

### Prerrequisitos

- Tener instalado un JDK compatible con Java 25.
- Tener PostgreSQL disponible si se usa la configuracion por defecto.
- No es necesario instalar Gradle, el proyecto incluye Gradle Wrapper.

Verificar Java:

```bash
java -version
```

### Clonar el repositorio

```bash
git clone https://github.com/ING69COOLER/Proyecto_Avanzada.git
cd Proyecto_Avanzada
```

### Compilar

En Linux/macOS:

```bash
./gradlew build
```

En Windows:

```bash
.\gradlew.bat build
```

### Ejecutar

En Linux/macOS:

```bash
./gradlew bootRun
```

En Windows:

```bash
.\gradlew.bat bootRun
```

La API quedara disponible en:

```text
http://localhost:8082
```

## Pruebas

Ejecutar todas las pruebas:

```bash
.\gradlew.bat test
```

Ejecutar una prueba especifica:

```bash
.\gradlew.bat test --tests "co.edu.uniquindio.Proyecto_Avanzada.domain.services.ServiciosDominioTest"
```

El reporte HTML se genera en:

```text
build/reports/tests/test/index.html
```

## Documentacion adicional

- `DocumentacionGeneral.md`: documentacion general del proyecto.
- `docs/Lenguaje_Ubicuo.pdf`: lenguaje ubicuo del dominio.
- `docs/Reglas de Negocio del Proyecto.pdf`: reglas de negocio.
- `docs/Diagrama_UML.pdf`: diagrama UML.
- `docs/estructura.mmd`: estructura en Mermaid.
- `docs/api/OpenAPI.yaml`: contrato REST.

## Flujo general de una solicitud

```text
REGISTRADA -> CLASIFICADA -> EN_ATENCION -> ATENDIDA -> CERRADA
```

Cada accion relevante agrega un evento al historial de la solicitud, incluyendo fecha, estado, tipo de accion, observacion y usuario responsable.

## Consideraciones de seguridad

- Las rutas `/auth/login`, `/auth/register` y `/api/usuarios/registro` son publicas.
- Las demas rutas requieren token JWT.
- Las contrasenas se almacenan codificadas con BCrypt.
- El usuario autenticado se obtiene desde el token JWT y se usa como identidad para crear, clasificar, priorizar, asignar, atender o cerrar solicitudes.

