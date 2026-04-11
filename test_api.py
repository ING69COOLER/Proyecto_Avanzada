import urllib.request, json

base = "http://localhost:8082"

def post(path, body):
    try:
        req = urllib.request.Request(base + path, data=json.dumps(body).encode(),
                                     headers={"Content-Type": "application/json"}, method="POST")
        return json.loads(urllib.request.urlopen(req).read().decode())
    except urllib.error.HTTPError as e:
        return {"ERROR": e.code, "body": json.loads(e.read().decode())}

def patch(path, body):
    try:
        req = urllib.request.Request(base + path, data=json.dumps(body).encode(),
                                     headers={"Content-Type": "application/json"}, method="PATCH")
        return json.loads(urllib.request.urlopen(req).read().decode())
    except urllib.error.HTTPError as e:
        return {"ERROR": e.code, "body": json.loads(e.read().decode())}

def get(path):
    try:
        return json.loads(urllib.request.urlopen(base + path).read().decode())
    except urllib.error.HTTPError as e:
        return {"ERROR": e.code, "body": json.loads(e.read().decode())}

def ok(label, resp):
    if "ERROR" in resp:
        print(f"  [FAIL] {label} -> {resp['body'].get('message', resp)}")
    else:
        print(f"  [OK]   {label} -> OK")
    return resp

print("=" * 60)
print("  PRUEBA COMPLETA DE LA API - CICLO DE VIDA SOLICITUD")
print("=" * 60)

# --- SETUP ---
print("\n[SETUP] Creando usuarios...")
coord = post("/api/usuarios/registro", {"nombre": "Luis Coord","identificacion": "COORD999","correo": "coord@uni.edu.co","activo": True,"rol": "COORDINADOR"})
docente = post("/api/usuarios/registro", {"nombre": "Maria Docente","identificacion": "DOC999","correo": "doc@uni.edu.co","activo": True,"rol": "DOCENTE"})
est = post("/api/usuarios/registro", {"nombre": "Pedro Student","identificacion": "EST999","correo": "est@uni.edu.co","activo": True,"rol": "ESTUDIANTE"})
print(f"  Coordinador: {coord.get('nombre', coord.get('body',{}).get('message','?'))}")
print(f"  Docente:     {docente.get('nombre', docente.get('body',{}).get('message','?'))}")
print(f"  Estudiante:  {est.get('nombre', est.get('body',{}).get('message','?'))}")

# --- RF-01: Crear solicitud ---
print("\n[RF-01] Crear solicitud (POST /api/solicitudes)")
sol = ok("Crear solicitud", post("/api/solicitudes", {
    "tipoSolicitud": "CONSULTA_ACADEMICA",
    "descripcion": "Consulta sobre pensum del programa",
    "canalOrigen": "EMAIL",
    "identificacionSolicitante": "EST999"
}))
codigo = sol.get("codigo")
print(f"         codigo={codigo}, estado={sol.get('estado')}")

# --- RF-02: Clasificar ---
print("\n[RF-02] Clasificar solicitud (PATCH /api/solicitudes/{codigo}/clasificacion)")
resp = ok("Clasificar solicitud", patch(f"/api/solicitudes/{codigo}/clasificacion", {
    "tipoSolicitud": "HOMOLOGACION",
    "identificacionUsuario": "COORD999",
    "observacion": "Reclasificada a homologacion"
}))
print(f"         estado={resp.get('estado')}")

# --- RF-03: Priorizar ---
print("\n[RF-03] Priorizar solicitud (PATCH /api/solicitudes/{codigo}/prioridad)")
resp = ok("Priorizar solicitud", patch(f"/api/solicitudes/{codigo}/prioridad", {
    "nivelPrioridad": "ALTA",
    "identificacionUsuario": "COORD999",
    "justificacion": "Solicitud urgente de grado"
}))
print(f"         prioridad={resp.get('prioridad')}")

# --- RF-04: Asignar responsable ---
print("\n[RF-04] Asignar responsable (PATCH /api/solicitudes/{codigo}/asignacion)")
resp = ok("Asignar responsable", patch(f"/api/solicitudes/{codigo}/asignacion", {
    "identificacionCoordinador": "COORD999",
    "identificacionResponsable": "DOC999",
    "observacion": "Asignado al docente para revision"
}))
print(f"         estado={resp.get('estado')}")

# --- RF-06: Cambiar estado a ATENDIDA ---
print("\n[RF-06] Cambiar estado (PATCH /api/solicitudes/{codigo}/estado)")
resp = ok("Cambiar estado -> ATENDIDA", patch(f"/api/solicitudes/{codigo}/estado", {
    "nuevoEstado": "ATENDIDA",
    "identificacionUsuario": "DOC999",
    "observacion": "Solicitud atendida satisfactoriamente"
}))
print(f"         estado={resp.get('estado')}")

# --- RF-08: Cerrar solicitud ---
print("\n[RF-08] Cerrar solicitud (PATCH /api/solicitudes/{codigo}/cierre)")
resp = ok("Cerrar solicitud", patch(f"/api/solicitudes/{codigo}/cierre", {
    "identificacionUsuario": "COORD999",
    "observacionCierre": "Caso resuelto y cerrado"
}))
print(f"         estado={resp.get('estado')}, fechaCierre={resp.get('fechaCierre')}")

# --- GET detalle ---
print("\n[GET] Detalle final (GET /api/solicitudes/{codigo})")
detalle = get(f"/api/solicitudes/{codigo}")
if "ERROR" not in detalle:
    print(f"  [OK]   estado={detalle.get('estado')}")
    hist = detalle.get("historial", [])
    print(f"         historial ({len(hist)} eventos):")
    for h in hist:
        print(f"           [{h.get('accion')}] {h.get('observacion')[:50]}")
else:
    print(f"  [FAIL] {detalle}")

# --- GET historial ---
print("\n[GET] Historial (GET /api/solicitudes/{codigo}/historial)")
hist = get(f"/api/solicitudes/{codigo}/historial")
if isinstance(hist, list):
    print(f"  [OK]   {len(hist)} eventos en historial")
else:
    print(f"  [FAIL] {hist}")

# --- GET con filtros ---
print("\n[GET] Filtrar por estado=CERRADA")
filtrados = get("/api/solicitudes?estado=CERRADA")
if isinstance(filtrados, list):
    print(f"  [OK]   {len(filtrados)} solicitudes CERRADAS")
else:
    print(f"  [FAIL] {filtrados}")

print("\n" + "=" * 60)
print("  FIN DE PRUEBAS")
print("=" * 60)
