package com.ledoyen.cukesalad.sampleApp.app2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ledoyen.cukesalad.sampleApp.app2.customer.Customer;
import com.ledoyen.cukesalad.sampleApp.app2.customer.CustomerRepository;

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

	@Autowired
	private CustomerRepository repo;

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

	@RequestMapping("/says/{message}")
	String says(@PathVariable String message) {
		return message.substring(4);
	}

	@RequestMapping("/create_user")
	String createUser(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
		Customer c = new Customer(firstName, lastName);
		c = repo.save(c);
		return "User saved : id=" + c.getId();
	}

	@RequestMapping("/list_users")
	List<String> listUsers() {
		return makeCollection(repo.findAll(new Sort(Direction.ASC, "id"))).stream()
				.map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList());
	}

	public static <E> Collection<E> makeCollection(Iterable<E> iter) {
		Collection<E> list = new ArrayList<E>();
		for (E item : iter) {
			list.add(item);
		}
		return list;
	}
}
