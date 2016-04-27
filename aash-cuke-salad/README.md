# cuke-salad

Brings Spring auto-mocker context and predefined stepDefs to test your real @Configuration with BDD.

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