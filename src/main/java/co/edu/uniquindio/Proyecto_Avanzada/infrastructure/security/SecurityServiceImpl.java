package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.security;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.LoginRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TokenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        JwtTokenProvider.GeneratedToken generatedToken = jwtTokenProvider.generateToken(authentication);

        return new TokenResponse(
                generatedToken.token(),
                "Bearer",
                generatedToken.expiresAt(),
                generatedToken.role()
        );
    }
}
