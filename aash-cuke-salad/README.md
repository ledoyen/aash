# cuke-salad

Brings Spring **auto-mocker context** and **predefined stepDefs** to test your real `@Configuration` with JUnit and Cucumber.

Consider a web application defined as :

> Application.java

```java
@SpringBootApplication
@PropertySource("classpath:app.properties")
@RestController
public class Application {

	@Value("${test}")
	private String test;

	@RequestMapping("/test")
	String test() {
		return test;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}
```

With a file app.properties containing `test=106`.
Simply add the following test class :

> RunCukeSalad.java

```java
@RunWith(CukeSalad.class)
@CukeSaladConfiguration(classes = Application.class)
public class RunCukeSalad {

	@MockProperties
	public static Map<String, String> configuration() {
		return map("key1", "43", "key2", "value");
	}
}
```

This configuration allows you to directly write Gherkin features such as :

> rest.feature

```gherkin
Feature: A test REST service
 
Scenario: Get on some resource and test the result
  When a GET request is made on /test resource
  Then the response code should be OK
  Then the response body should be 43
```

:smiling_imp:

