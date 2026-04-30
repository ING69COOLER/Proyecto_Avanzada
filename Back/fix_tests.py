import re

files = {
    "src/test/java/co/edu/uniquindio/Proyecto_Avanzada/domain/entities/UsuarioPermisosTest.java": [
        (r'new Usuario\("Estudiante 1"', 'new Usuario(1L, "Estudiante 1"'),
        (r'new Usuario\("Coordinador 1"', 'new Usuario(2L, "Coordinador 1"'),
        (r'new Usuario\("Docente 1"', 'new Usuario(3L, "Docente 1"'),
        (r'coordinador\.puedeEmitirDecisiones\(\)', 'coordinador.puedeClasificarSolicitud() && coordinador.puedeCerrarSolicitud()'),
        (r'docente\.puedeEmitirDecisiones\(\)', 'docente.puedeClasificarSolicitud() || docente.puedeCerrarSolicitud()')
    ],
    "src/test/java/co/edu/uniquindio/Proyecto_Avanzada/domain/services/ServiciosDominioTest.java": [
        (r'new Usuario\("Estudiante Activo"', 'new Usuario(1L, "Estudiante Activo"'),
        (r'new Usuario\("Estudiante Inactivo"', 'new Usuario(2L, "Estudiante Inactivo"'),
        (r'TipoSolicitud\.REVISION_CALIFICACION', 'TipoSolicitud.CONSULTA_ACADEMICA'),
        (r'CanalOrigen\.SISTEMA_ACADEMICO', 'CanalOrigen.SAC'),
        (r'\.getSolicitante\(\)', '.getUsuarioSolicitante()')
    ],
    "src/test/java/co/edu/uniquindio/Proyecto_Avanzada/domain/entities/SolicitudInvariantesYFlujoTest.java": [
        (r'new Usuario\("Estudiante 1"', 'new Usuario(1L, "Estudiante 1"'),
        (r'new Usuario\("Coordinador 1"', 'new Usuario(2L, "Coordinador 1"'),
        (r'new Usuario\("Docente 1"', 'new Usuario(3L, "Docente 1"'),
        (r'new Usuario\("El Intruso"', 'new Usuario(4L, "El Intruso"')
    ]
}

for path, replacements in files.items():
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    
    for old, new in replacements:
        content = re.sub(old, new, content)
        
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
