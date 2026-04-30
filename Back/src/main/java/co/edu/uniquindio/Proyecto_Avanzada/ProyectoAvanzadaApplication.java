package co.edu.uniquindio.Proyecto_Avanzada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = "co.edu.uniquindio.Proyecto_Avanzada.infrastructure.outbound.database.jpa.repository"
)
public class ProyectoAvanzadaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoAvanzadaApplication.class, args);
    }
}
