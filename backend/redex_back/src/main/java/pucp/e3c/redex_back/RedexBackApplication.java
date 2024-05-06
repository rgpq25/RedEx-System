package pucp.e3c.redex_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import pucp.e3c.redex_back.model.Algoritmo;

@SpringBootApplication
@EnableAsync
public class RedexBackApplication {

	public static void main(String[] args) {
		//Algoritmo.loopPrincipal();
		SpringApplication.run(RedexBackApplication.class, args);
	}

}
