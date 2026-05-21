# 🎨 Configuración Frontend - Sistema de Gestión de Solicitudes

## ✅ Implementación Completada

### 1. **Autenticación y Autorización**
- ✓ Login con JWT
- ✓ Gestión de token en localStorage
- ✓ AuthGuard para protección de rutas
- ✓ Interceptor HTTP para agregar Bearer token

### 2. **Componentes**
- ✓ Login mejorado con validación reactiva
- ✓ Admin Dashboard (ADMINISTRATIVO/COORDINADOR)
- ✓ Student Dashboard (ESTUDIANTE)
- ✓ Unauthorized (403) page

### 3. **Seguridad**
- ✓ JWT token almacenado en localStorage
- ✓ Rutas protegidas por rol
- ✓ Interceptor de autenticación
- ✓ Decodificación de token con jwt-decode

## 🚀 Iniciar Frontend

```bash
cd Front
npm install
npm start
```

Acceder a: http://localhost:4200

## 📝 Credenciales de Prueba

| Usuario | Contraseña | Rol | Destino |
|---------|-----------|-----|---------|
| admin   | password123 | ADMINISTRATIVO | /admin-dashboard |
| 1001    | password123 | ESTUDIANTE | /student-dashboard |

*Estas credenciales deben existir en la BD del backend*

## 🔗 Endpoints Requeridos del Backend

El frontend espera estos endpoints:

```
POST /auth/login
  Request: { username: string, password: string }
  Response: { token: string }

POST /auth/register
  Request: { nombre, identificacion, correo, password, rol }
  Response: { token: string }
```

## 📊 Flujo de Roles

```
┌─────────────────────────────────────────────────┐
│           SISTEMA DE GESTIÓN                    │
└─────────────────────────────────────────────────┘
         ↓              ↓              ↓
    ┌────────┐    ┌────────────────┐  ┌────────┐
    │Student │    │ ADMINISTRATIVO │  │ Docente│
    └────────┘    │   COORDINADOR  │  └────────┘
       │           └────────────────┘     │
       │                 │                 │
       ↓                 ↓                 ↓
  CREAR/VER        GESTIONAR TODAS   CALIFICAR
  MIS SOLICITUDES   SOLICITUDES       SOLICITUDES
```

## 🔐 Seguridad en Production

Antes de deployar:

- [ ] Cambiar SECRET_KEY en backend (JwtService.java)
- [ ] Configurar HTTPS
- [ ] Agregar CORS correcto
- [ ] Usar SameSite cookies
- [ ] Implementar refresh token
- [ ] Agregar rate limiting

## 📂 Estructura Final

```
Front/
├── src/
│   ├── app/
│   │   ├── guards/
│   │   │   └── auth.guard.ts
│   │   ├── interceptors/
│   │   │   └── jwt.interceptor.ts
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── token.service.ts
│   │   │   ├── solicitud.service.ts
│   │   │   └── usuario.service.ts
│   │   ├── componentes/
│   │   │   ├── login/
│   │   │   ├── admin-dashboard/
│   │   │   ├── student-dashboard/
│   │   │   ├── unauthorized/
│   │   │   └── solicitudes/
│   │   └── app.routes.ts
│   └── main.ts
├── package.json
└── LOGIN_GUIDE.md
```

## 🧪 Testing

```bash
# Compilar
npm run build

# Tests (si existen)
npm run test

# Ver en browser
npm start
```

## 🐛 Debugging

### Verificar token en localStorage
```javascript
// En console del navegador
localStorage.getItem('auth_token')
localStorage.getItem('user_role')
localStorage.getItem('user_id')
```

### Verificar decodificación de token
```typescript
import { jwtDecode } from 'jwt-decode';
const token = localStorage.getItem('auth_token');
console.log(jwtDecode(token));
```

## 📞 Soporte

Para issues con:
- **Login fallido:** Verificar credenciales en BD del backend
- **Token inválido:** Revisar expiración en JwtService.java
- **Rutas protegidas:** Confirmar rol en token
- **CORS:** Configurar en Spring Security

---

**Versión:** 1.0  
**Fecha:** 11 de mayo de 2026  
**Stack:** Angular 21 + Spring Boot 3.2
