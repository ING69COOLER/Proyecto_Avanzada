# Proyecto Avanzada - Frontend (Angular 21)

Frontend del proyecto final de Programación Avanzada, desarrollado con **Angular 21** en modo **standalone**.

---

## Estructura del Proyecto

```
src/app/
├── componentes/            # Componentes visuales de la aplicación
│   ├── inicio/             # Página de inicio
│   │   ├── inicio.ts
│   │   ├── inicio.html
│   │   └── inicio.css
│   ├── login/              # Página de login
│   │   ├── login.ts
│   │   ├── login.html
│   │   └── login.css
│   └── registro/           # Página de registro
│       ├── registro.ts
│       ├── registro.html
│       └── registro.css
├── dto/                    # Clases/interfaces de datos (DTOs)
├── servicios/              # Lógica de negocio y conexión con APIs
├── app.ts                  # Componente raíz
├── app.html                # Vista principal con navegación y router-outlet
├── app.css                 # Estilos globales del componente raíz
├── app.config.ts           # Configuración de la aplicación
└── app.routes.ts           # Definición de rutas
```

---

## Configuración Realizada

### 1. Directorios de trabajo

Se crearon tres directorios dentro de `src/app/` para organizar el código:

| Directorio     | Propósito                                    |
|----------------|----------------------------------------------|
| `componentes/` | Componentes visuales (vistas de la app)      |
| `dto/`         | Clases e interfaces de transferencia de datos|
| `servicios/`   | Lógica de negocio y conexión con el backend  |

### 2. Componentes generados

Se generaron 3 componentes standalone usando Angular CLI:

```bash
ng generate component componentes/inicio --skip-tests
ng generate component componentes/login --skip-tests
ng generate component componentes/registro --skip-tests
```

Cada componente contiene:
- **`.ts`** — Lógica y configuración del componente (decorador `@Component`)
- **`.html`** — Estructura visual (vista)
- **`.css`** — Estilos específicos del componente

> En Angular 21, los archivos ya no llevan el sufijo `.component`. Ejemplo: `inicio.ts` en lugar de `inicio.component.ts`.

### 3. Componente raíz (`app.ts`)

Se configuró el componente raíz con **Signals** de Angular:

```typescript
import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = signal('My-App');
  footer = signal('Universidad del Quindío');
}
```

- **`signal()`**: Se usan Señales (Signals) para manejar variables reactivas.
- **`RouterOutlet`**: Permite cargar componentes dinámicamente según la ruta.
- **`RouterLink`**: Permite navegación SPA sin recargar la página.

### 4. Vista principal (`app.html`)

```html
<header>
  <p>Página inicial de {{ title() }}</p>
  <ul>
    <li><a routerLink="/">Inicio</a></li>
    <li><a routerLink="/login">Login</a></li>
    <li><a routerLink="/registro">Registro</a></li>
  </ul>
</header>

<router-outlet />

<footer>{{ footer() }}</footer>
```

- Se usa `routerLink` en lugar de `href` para navegación sin recarga completa de la página.
- `{{ title() }}` y `{{ footer() }}` leen el valor de los Signals con paréntesis `()`.
- `<router-outlet />` carga el componente correspondiente a la ruta activa.

### 5. Rutas (`app.routes.ts`)

```typescript
import { Routes } from '@angular/router';
import { Inicio } from './componentes/inicio/inicio';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: '**', pathMatch: 'full', redirectTo: '/' },
];
```

| Ruta         | Componente | Descripción              |
|--------------|------------|--------------------------|
| `/`          | Inicio     | Página principal         |
| `/login`     | Login      | Formulario de login      |
| `/registro`  | Registro   | Formulario de registro   |
| `**`         | —          | Redirige rutas no encontradas a `/` |

---

## Cómo ejecutar

### Requisitos previos
- Node.js (v18+)
- Angular CLI

### Instalación
```bash
cd Front
npm install
```

### Ejecución en desarrollo
```bash
ng serve
```
O alternativamente:
```bash
npm start
```

> **Nota:** Si PowerShell bloquea la ejecución de scripts, ejecuta desde **cmd** o habilita scripts con:
> ```powershell
> Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
> ```

La aplicación estará disponible en `http://localhost:4200/`.

---

## Tecnologías

- **Angular 21** (Standalone Components)
- **TypeScript**
- **Angular Signals** para reactividad
- **Angular Router** para navegación SPA

---

## Backend

El backend de este proyecto está en la carpeta `../Back` y está desarrollado con **Spring Boot** (Gradle). Se ejecuta con:

```bash
cd Back
.\gradlew.bat bootRun
```

---

## Universidad del Quindío — Programación Avanzada
