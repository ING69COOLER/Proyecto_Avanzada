# README de cambios del proyecto

Este documento resume la estructura actual del proyecto, los cambios realizados en back y front, los metodos agregados y como se conectan las funcionalidades principales.

## Como correr el proyecto

### Backend

Ruta:

```bash
cd Back
.\gradlew.bat bootRun
```

URL base:

```text
http://localhost:8082
```

Pruebas:

```bash
cd Back
.\gradlew.bat test
```

### Frontend

Ruta:

```bash
cd Front
npm start -- --host localhost --port 4200
```

URL:

```text
http://localhost:4200
```

Build:

```bash
cd Front
npm run build
```

## Estructura general

```text
Proyecto_Avanzada/
  Back/
    src/main/java/co/edu/uniquindio/Proyecto_Avanzada/
      application/
        dto/
        mapper/
        security/
        usecase/
      domain/
        entities/
        exception/
        ports/
        services/
        valueobjects/
      infrastructure/
        inbound/controllers/
        outbound/database/jpa/
        security/
    src/main/resources/application.properties
    build.gradle
  Front/
    src/app/
      componentes/
      services/
      app.routes.ts
      app.html
      app.css
    src/styles.css
    proxy.conf.json
    package.json
```

## Estructura del frontend

```text
Front/src/app/
  app.html
    Barra de navegacion principal, outlet y footer.

  app.routes.ts
    Rutas principales: inicio, login, registro y solicitudes.

  componentes/
    inicio/
      Pantalla inicial. Muestra botones distintos segun rol.

    login/
      Formulario de inicio de sesion.

    registro/
      Formulario de registro de usuario y rol.

    solicitudes/
      list/
        Dashboard principal por pestanas.
        Muestra acciones segun rol.
        Permite registrar, consultar, clasificar, priorizar, asignar, atender y cerrar.

      detalle/
        Vista de detalle de solicitud, ahora sin links de redireccion operativa.

      crear/
      clasificar/
      priorizar/
      asignar/
      cambiar-estado/
      cerrar/
        Componentes antiguos conservados por compatibilidad, pero el flujo principal esta centralizado en list/.

  services/
    auth.service.ts
      Login, registro, token, lectura de rol, identificacion y permisos.

    solicitud.service.ts
      Cliente HTTP para solicitudes.

    usuario.service.ts
      Cliente HTTP para usuarios y responsables asignables.

    auth.guard.ts
      Protege rutas privadas.

    auth.interceptor.ts
      Agrega Authorization Bearer al consumir el back.
```

## Roles y paneles activos

### ESTUDIANTE

Puede:

- Registrar solicitudes.
- Ver solo sus propias solicitudes.
- Ver estado, prioridad e historial de sus solicitudes.

No puede:

- Ver solicitudes de otros usuarios.
- Clasificar, priorizar, asignar, atender o cerrar.

### COORDINADOR

Puede:

- Ver la bandeja general.
- Clasificar solicitudes.
- Priorizar solicitudes.
- Asignar responsables.
- Cerrar solicitudes.

No puede:

- Atender solicitudes como responsable.

### DOCENTE

Puede:

- Ver solo solicitudes asignadas a el.
- Atender solicitudes asignadas.

No puede:

- Ver bandeja general.
- Asignar responsables.
- Clasificar, priorizar o cerrar.

### ADMINISTRATIVO

Puede:

- Registrar solicitudes.
- Ver solo solicitudes asignadas a el.
- Atender solicitudes asignadas.

No puede:

- Ver bandeja general.
- Clasificar, priorizar, asignar o cerrar.

## Cambios principales en frontend

### Dashboard por rol

Archivo:

```text
Front/src/app/componentes/solicitudes/list/list.ts
```

Cambios:

- Se elimino el flujo basado en links de redireccion para acciones.
- Se agrego un dashboard con pestanas visibles segun permisos.
- Se agrego seleccion de solicitud activa.
- Se agrego carga de detalle para mostrar historial.
- Se agrego validacion de observaciones antes de enviar acciones al back.
- Se agrego lectura de query params para abrir una pestana concreta.

Metodos relevantes:

- `visibleTabs()`
  - Construye las pestanas segun rol.

- `load()`
  - Carga solicitudes.
  - Coordinador carga todo.
  - Docente/administrativo cargan asignadas.
  - Estudiante carga propias.

- `loadResponsables()`
  - Carga usuarios asignables desde `/api/usuarios/responsables`.

- `getInitialTab()`
  - Lee `?tab=...` para abrir la pestana correcta desde la portada.

- `selectSolicitud()`
  - Define la solicitud activa.
  - Opcionalmente carga detalle.

- `submitCrear()`
  - Registra una solicitud.

- `submitClasificar()`
  - Clasifica una solicitud.

- `submitPriorizar()`
  - Prioriza una solicitud.

- `submitAsignar()`
  - Asigna un responsable elegido desde una lista.

- `submitAtender()`
  - Marca solicitud como atendida.

- `submitCerrar()`
  - Cierra una solicitud.

- `ensureObservation()`
  - Valida observaciones entre 5 y 500 caracteres.

- `getErrorMessage()`
  - Resume errores del back para mostrarlos de forma limpia.

### Selector de responsables

Archivos:

```text
Front/src/app/componentes/solicitudes/list/list.html
Front/src/app/services/usuario.service.ts
```

Cambio:

- El campo manual de identificacion del responsable fue reemplazado por un `<select>`.
- El selector muestra:

```text
Nombre - Identificacion
```

Servicio agregado:

- `UsuarioService.listarResponsables()`
  - Consume `GET /api/usuarios/responsables`.

### Botones por rol en pantalla inicial

Archivo:

```text
Front/src/app/componentes/inicio/inicio.html
```

Cambios:

- Usuario sin sesion:
  - Iniciar sesion.
  - Registrarse.

- Estudiante:
  - Registrar solicitud.
  - Mis solicitudes.

- Coordinador:
  - Bandeja general.
  - Asignar.
  - Cerrar.

- Docente:
  - Mis asignadas.
  - Atender.

- Administrativo:
  - Registrar solicitud.
  - Mis asignadas.
  - Atender.

### AuthService

Archivo:

```text
Front/src/app/services/auth.service.ts
```

Metodos agregados:

- `getRole()`
  - Lee el rol desde el token JWT.

- `getIdentification()`
  - Lee la identificacion desde el `sub` del token JWT.

- `hasRole(...roles)`
  - Valida si el usuario tiene alguno de los roles indicados.

- `canRegisterSolicitudes()`
  - `ESTUDIANTE` y `ADMINISTRATIVO`.

- `canManageSolicitudes()`
  - `COORDINADOR`.

- `canAttendSolicitudes()`
  - `DOCENTE` y `ADMINISTRATIVO`.

- `canConsultSolicitudes()`
  - `COORDINADOR`, `DOCENTE`, `ADMINISTRATIVO` y `ESTUDIANTE`.

## Cambios principales en backend

### Consulta de solicitudes con usuario autenticado

Archivo:

```text
Back/src/main/java/.../infrastructure/inbound/controllers/SolicitudesController.java
```

Cambio:

- En `consultarSolicitudes(...)` se usa `getAuthenticatedUsername()`.
- Antes se mandaba `null` al caso de uso y fallaba la consulta.

### Consulta filtrada por rol

Archivo:

```text
Back/src/main/java/.../application/usecase/ConsultarSolicitudesFiltradasUseCase.java
```

Logica actual:

- `COORDINADOR`
  - Puede listar todo o filtrar.

- `DOCENTE` y `ADMINISTRATIVO`
  - Solo ven solicitudes asignadas a ellos.

- `ESTUDIANTE`
  - Solo ve solicitudes donde es solicitante.

Metodo principal:

- `ejecutar(...)`
  - Recibe filtros y la identificacion del usuario autenticado.
  - Resuelve el usuario.
  - Aplica consulta segun rol.

### Permisos de usuario

Archivo:

```text
Back/src/main/java/.../domain/entities/Usuario.java
```

Cambios:

- `puedeAtenderSolicitud()`
  - Ahora permite `DOCENTE` y `ADMINISTRATIVO`.

- `puedeConsultarSolicitudes()`
  - Ahora permite `COORDINADOR`, `DOCENTE`, `ADMINISTRATIVO` y `ESTUDIANTE`.

- `validarPuedeAtenderSolicitud()`
  - Ya no exige exclusivamente `DOCENTE`; usa `puedeAtenderSolicitud()`.

### Asignacion correcta del responsable

Archivos:

```text
Back/src/main/java/.../domain/entities/Solicitud.java
Back/src/main/java/.../domain/services/AtencionSolicitudesService.java
Back/src/main/java/.../application/usecase/AsignarResponsableUseCase.java
```

Problema corregido:

- Antes la asignacion guardaba como responsable en el historial al coordinador.
- Eso hacia que el docente/administrativo no viera la solicitud asignada.

Cambios:

- Se agrego una sobrecarga en `Solicitud`:

```java
asignarResponsable(Usuario coordinador, Usuario responsableAsignado, String descripcion)
```

- Se agrego una sobrecarga en `AtencionSolicitudesService`:

```java
asignarResponsable(Usuario coordinador, Usuario responsable, Solicitud solicitud, String descripcion)
```

- `AsignarResponsableUseCase.ejecutar(...)` ahora pasa coordinador y responsable real.

Resultado:

- El historial de accion `ASIGNACION` queda con el usuario responsable asignado.

### Repositorio de solicitudes

Archivos:

```text
Back/src/main/java/.../domain/ports/out/IRepositorioSolicitud.java
Back/src/main/java/.../infrastructure/outbound/database/jpa/repository/SolicitudSpringDataRepository.java
Back/src/main/java/.../infrastructure/outbound/database/jpa/adapter/SolicitudJpaAdapter.java
```

Metodos agregados:

- `IRepositorioSolicitud.consultarSolicitante(Usuario usuario, Pageable pageable)`
  - Consulta solicitudes propias de un estudiante.

- `SolicitudSpringDataRepository.findByUsuarioSolicitanteIdentificacion(...)`
  - Query derivada por solicitante.

- `SolicitudSpringDataRepository.findAsignadasByResponsableIdentificacion(...)`
  - Consulta solicitudes asignadas por evento de historial `ASIGNACION`.

- `SolicitudJpaAdapter.consultarSolicitante(...)`
  - Implementa consulta por solicitante.

- `SolicitudJpaAdapter.consultarResponsable(...)`
  - Ahora consulta asignaciones reales por accion `ASIGNACION`.

### Usuarios responsables asignables

Archivos:

```text
Back/src/main/java/.../infrastructure/inbound/controllers/UsuariosController.java
Back/src/main/java/.../infrastructure/outbound/database/jpa/repository/UsuarioSpringDataRepository.java
```

Endpoint agregado:

```http
GET /api/usuarios/responsables
```

Devuelve:

- Usuarios activos con rol `DOCENTE`.
- Usuarios activos con rol `ADMINISTRATIVO`.

Metodo agregado:

```java
UsuarioSpringDataRepository.findByRolAndActivoTrueOrderByNombreAsc(Rol rol)
```

### Mensajes de error limpios

Archivo:

```text
Back/src/main/java/.../infrastructure/inbound/controllers/GlobalExceptionHandler.java
```

Cambio:

- `handleValidationErrors(...)` ya no devuelve un mensaje enorme de Spring.
- Ahora devuelve el primer mensaje util de validacion, por ejemplo:

```text
La justificacion debe tener entre 5 y 500 caracteres
```

## Endpoints principales usados por el front

```http
POST /auth/login
POST /auth/register

GET  /api/solicitudes
POST /api/solicitudes
GET  /api/solicitudes/{codigo}
PATCH /api/solicitudes/{codigo}/clasificacion
PATCH /api/solicitudes/{codigo}/prioridad
PATCH /api/solicitudes/{codigo}/asignacion
PATCH /api/solicitudes/{codigo}/estado
PATCH /api/solicitudes/{codigo}/cierre

GET /api/usuarios/responsables
POST /api/usuarios/registro
```

## Flujo principal por rol

### Estudiante

1. Entra al dashboard.
2. Puede registrar solicitud.
3. Puede abrir "Mis solicitudes".
4. El back retorna solo solicitudes donde `usuarioSolicitante.identificacion` coincide con el usuario autenticado.
5. Puede ver estado e historial.

### Coordinador

1. Ve la bandeja general.
2. Clasifica solicitudes registradas.
3. Prioriza solicitudes clasificadas.
4. Asigna responsable desde lista de docentes/administrativos activos.
5. Cierra solicitudes atendidas.

### Docente / Administrativo

1. Ve "Mis solicitudes" o "Mis asignadas".
2. El back retorna solo solicitudes con historial `ASIGNACION` donde el responsable coincide.
3. Puede atender la solicitud.

## Validaciones importantes

Las observaciones y justificaciones deben tener entre 5 y 500 caracteres.

Se valida en:

- Front: antes de enviar.
- Back: con anotaciones `@NotBlank` y `@Size`.

## Estado actual de ejecucion validado

Ultima validacion realizada:

- `.\gradlew.bat test`: OK.
- `npm run build`: OK.
- Front responde en `http://localhost:4200`.
- Back responde en `http://localhost:8082`.
- Coordinador lista todo.
- Estudiante lista solo sus solicitudes.
- Docente/administrativo listan solo sus asignadas.
