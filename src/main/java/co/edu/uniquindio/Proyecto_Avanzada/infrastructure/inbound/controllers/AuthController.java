package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.inbound.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.subst.Token;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.CrearUsuarioRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.request.LoginRequest;
import co.edu.uniquindio.Proyecto_Avanzada.application.dto.response.TokenResponse;
import co.edu.uniquindio.Proyecto_Avanzada.application.security.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest entity) {
        
        return ResponseEntity.ok(authService.login(entity)) ;
    }


    @PostMapping(value = "register")
    public ResponseEntity<TokenResponse> register(@RequestBody CrearUsuarioRequest entity) {

        return ResponseEntity.ok(authService.register(entity));
    }
}
