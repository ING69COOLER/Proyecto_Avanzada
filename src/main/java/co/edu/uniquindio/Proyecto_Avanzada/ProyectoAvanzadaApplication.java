package co.edu.uniquindio.Proyecto_Avanzada;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.edu.uniquindio.Proyecto_Avanzada.domain.Prueba.Prueba;




@SpringBootApplication
public class ProyectoAvanzadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoAvanzadaApplication.class, args);
		Prueba prueba = new Prueba();
		prueba.demostracionCompleta();
	}

}
