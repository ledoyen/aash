package com.ledoyen.cukesalad.sampleApp.app2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample web application.<br/>
 * Run {@link #main(String[])} to launch.
 */
@SpringBootApplication
@PropertySource("classpath:app.properties")
@RestController
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Value("${test}")
	private String test;

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	public Application() {
		LOGGER.info("Initiating web server");
	}

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@RequestMapping("/post/{message}")
	String post(@PathVariable String message) {
		return "Message posted : " + message;
	}

	@RequestMapping("/test")
	String test() {
		return test + test;
	}
}
