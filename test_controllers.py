import requests

BASE_URL = "http://localhost:8082/api/solicitudes"

def test_filtros_controller():
    print(f"Probando Endpoint: GET {BASE_URL}")
    
    # 1. Filtros actuales y el nuevo de prioridad
    params = {
        "estadoSolicitud": "REGISTRADA",
        "tipoSolicitud": "HOMOLOGACION",
        "prioridadSolicitud": "ALTA",
        "identificacionResponsable": "80012345" # Valor requerido por el dominio actualmente.
    }
    
    print(f"Parametros enviados: {params}")
    
    try:
        response = requests.get(BASE_URL, params=params)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            solicitudes = response.json()
            print(f"Exito! Solicitudes encontradas: {len(solicitudes)}")
            for s in solicitudes:
                prioridad = s.get('prioridad', {})
                print(f"- Solicitud #{s.get('codigo')} | Estado: {s.get('estado')} | Prioridad enviada: {prioridad.get('nivel') if prioridad else 'N/A'}")
        else:
            print(f"Error en la peticion:\n{response.text}")
            
    except requests.exceptions.ConnectionError:
        print(f"Error: La conexion fue rechazada. Verifica que Spring Boot (/gradlew bootRun) este encendido.")
        
if __name__ == "__main__":
    test_filtros_controller()
