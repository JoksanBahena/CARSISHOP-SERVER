package mx.edu.utez.carsishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class CarsiShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarsiShopApplication.class, args);
	}

}
