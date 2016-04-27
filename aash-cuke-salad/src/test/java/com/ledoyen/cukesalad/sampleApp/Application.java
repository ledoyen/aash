package com.ledoyen.cukesalad.sampleApp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Sample web application.<br/>
 * Run {@link #main(String[])} to launch.
 */
@SpringBootApplication
@PropertySource("classpath:app.properties")
public class Application {

	@Value("${test}")
	private String test;

	@Bean(name = "stringBean")
	public String stringBean() {
		return test;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}
