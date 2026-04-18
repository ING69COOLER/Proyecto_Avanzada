package co.edu.uniquindio.Proyecto_Avanzada.infrastructure.security;

import co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository.UsuarioSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class UserConfig {

    private final UsuarioSpringDataRepository usuarioSpringDataRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioSpringDataRepository.findByIdentificacion(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
