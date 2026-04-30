package co.edu.uniquindio.Proyecto_Avanzada.application.security;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.LoginRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TokenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.domain.valueobjects.Rol;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.entity.UsuarioJPAEntity;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository.UsuarioSpringDataRepository;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.security.jwt.CustomUserDetails;
import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioSpringDataRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UsuarioJPAEntity usuario = repository.findByIdentificacion(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return TokenResponse.builder()
                .token(jwtService.getToken(new CustomUserDetails(usuario)))
                .build();
    }

    public TokenResponse register(CrearUsuarioRequest request){
        UsuarioJPAEntity usuario = UsuarioJPAEntity.builder()
                .nombre(request.nombre())
                .identificacion(request.identificacion())
                .correo(request.correo())
                .password(passwordEncoder.encode(request.password()))
                .activo(request.activo())
                .rol(toDomainRol(request.rol().codigo()))
                .build();

        repository.save(usuario);

        return TokenResponse.builder().token(jwtService.getToken(new CustomUserDetails(usuario))).build();
    }

    private Rol toDomainRol(String rolCodigo) {
        if (rolCodigo == null || rolCodigo.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        return switch (rolCodigo.trim().toUpperCase()) {
            case "ESTUDIANTE" -> Rol.ESTUDIANTE;
            case "ADMINISTRATIVO" -> Rol.ADMINISTRATIVO;
            case "COORDINADOR" -> Rol.COORDINADOR;
            case "DOCENTE" -> Rol.DOCENTE;
            default -> throw new IllegalArgumentException("Rol invalido: " + rolCodigo);
        };
    }
}
