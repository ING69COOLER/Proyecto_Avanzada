package co.edu.uniquindio.Proyecto_Avanzada.application.security;

import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.LoginRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TokenResponse;

public interface SecurityService {
    TokenResponse login(LoginRequest request);
}
