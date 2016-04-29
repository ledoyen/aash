# cuke-salad

Brings Spring **auto-mocker context** and **predefined stepDefs** to test your real `@Configuration` with Cucumber.

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


## Auto-mocker

Auto-mocker context replace or add mocks depending on Spring module your classes use.
Currently Auto-mocker context supports :
* mocking of any Property sources (`@PropertySource`, `PropertyPlaceHolderConfigurer`, `PropertyPlaceholderAutoConfiguration`, etc.)
* mocking of `@Controller` beans

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

> Test.java

```java
try (ConfigurableApplicationContext context = AutoMockerContextBuilder.newBuilder()
				.mockPropertySources(StringMap.of("test", "43")).buildWithJavaConfig(Application.class)) {
	context.start();

	context.getBean(MockMvc.class)
		.perform(MockMvcRequestBuilders.get("/test"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("43"));
}
```