package co.edu.uniquindio.Proyecto_Avanzada.application.dto.response;

import java.time.Instant;

public record TokenResponse(
        String token,
        String type,
        Instant expireAt,
        String role
) {
}
