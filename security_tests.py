import requests
import json

BASE_URL = "http://localhost:8082"

def test_security():
    print("--- INICIANDO PRUEBAS DE SEGURIDAD ---")

    # 1. Registro de Estudiante
    print("\n1. Registrando Estudiante...")
    estudiante_payload = {
        "nombre": "Estudiante Prueba",
        "identificacion": "50001",
        "correo": "estudiante@test.com",
        "password": "password123",
        "rol": { "codigo": "ESTUDIANTE" },
        "activo": True
    }
    resp = requests.post(f"{BASE_URL}/auth/register", json=estudiante_payload)
    print(f"Status: {resp.status_code}")
    token_estudiante = resp.json().get("token")
    if token_estudiante:
        print("Token Estudiante obtenido correctamente.")

    # 2. Registro de Coordinador
    print("\n2. Registrando Coordinador...")
    coord_payload = {
        "nombre": "Coordinador Prueba",
        "identificacion": "50002",
        "correo": "coord@test.com",
        "password": "password123",
        "rol": { "codigo": "COORDINADOR" },
        "activo": True
    }
    resp = requests.post(f"{BASE_URL}/auth/register", json=coord_payload)
    print(f"Status: {resp.status_code}")
    token_coord = resp.json().get("token")
    if token_coord:
        print("Token Coordinador obtenido correctamente.")

    # 3. Crear solicitud con Estudiante (Debe funcionar)
    print("\n3. Creando solicitud (como Estudiante)...")
    sol_payload = {
        "tipoSolicitud": { "codigo": "CONSULTA_ACADEMICA" },
        "descripcion": "Solicitud de prueba seguridad",
        "canalOrigen": { "codigo": "PORTAL_WEB" }
    }
    headers_est = {"Authorization": f"Bearer {token_estudiante}"}
    resp = requests.post(f"{BASE_URL}/api/solicitudes", json=sol_payload, headers=headers_est)
    print(f"Status: {resp.status_code}")
    solicitud_id = None
    if resp.status_code == 201:
        solicitud_id = resp.json().get("codigo")
        print(f"Solicitud creada con ID: {solicitud_id}")
    else:
        print(f"Error al crear: {resp.text}")

    # 4. Intentar clasificar como Estudiante (Debe ser 403 Forbidden)
    if solicitud_id:
        print("\n4. Intentando clasificar solicitud como ESTUDIANTE (Debe fallar)...")
        clas_payload = {
            "tipoSolicitud": { "codigo": "HOMOLOGACION" },
            "observacion": "Intento no autorizado"
        }
        resp = requests.patch(f"{BASE_URL}/api/solicitudes/{solicitud_id}/clasificacion", json=clas_payload, headers=headers_est)
        print(f"Status: {resp.status_code} (Esperado: 403)")
        if resp.status_code == 403:
            print("Resultado exitoso: Acceso denegado correctamente.")

        # 5. Clasificar como Coordinador (Debe funcionar)
        print("\n5. Clasificando solicitud como COORDINADOR (Debe funcionar)...")
        headers_coord = {"Authorization": f"Bearer {token_coord}"}
        resp = requests.patch(f"{BASE_URL}/api/solicitudes/{solicitud_id}/clasificacion", json=clas_payload, headers=headers_coord)
        print(f"Status: {resp.status_code}")
        if resp.status_code == 200:
            print("Resultado exitoso: Clasificación realizada.")

    print("\n--- PRUEBAS FINALIZADAS ---")

if __name__ == "__main__":
    try:
        test_security()
    except Exception as e:
        print(f"Error durante las pruebas: {e}")
