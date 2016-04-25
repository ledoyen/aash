package com.ledoyen.cukesalad.sampleApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:app.properties")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
		// try (ConfigurableApplicationContext context = new
		// AnnotationConfigApplicationContext(Application.class)) {
		// context.start();
		// }
	}
}
