package pucp.e3c.redex_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pucp.e3c.redex_back.model.Algoritmo;

@SpringBootApplication
public class RedexBackApplication {

	public static void main(String[] args) {
		Algoritmo.procesarPaquetes();
		// SpringApplication.run(RedexBackApplication.class, args);
	}

}
