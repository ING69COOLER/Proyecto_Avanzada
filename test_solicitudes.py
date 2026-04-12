import requests

BASE_URL = "http://localhost:8080/api/solicitudes"

def test_consultar_solicitudes():
    print(f"Probando GET a {BASE_URL} con filtros...")
    
    # Parametros de prueba, incluyendo el nuevo 'prioridadSolicitud'
    params = {
        "estadoSolicitud": "REGISTRADA",
        "tipoSolicitud": "HOMOLOGACION",
        "prioridadSolicitud": "ALTA"
    }
    
    try:
        response = requests.get(BASE_URL, params=params)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            print("Exito! Solicitudes devueltas:")
            for s in response.json():
                print(f"- Solicitud #{s.get('codigo')} | Estado: {s.get('estado')} | Prioridad: {s.get('prioridad')}")
        else:
            print(f"Error {response.status_code}: {response.text}")
            
    except requests.exceptions.ConnectionError:
        print(f"Error: No se pudo conectar a {BASE_URL}. Asegurate que la aplicacion Spring Boot este corriendo.")

if __name__ == "__main__":
    test_consultar_solicitudes()
