# cuke-salad

Brings Spring auto-mocker context and predefined stepDefs to test your real @Configuration with BDD.

## Auto-mocker

By using the Auto-mocker context, you let cuke-salad replace or add mocks depending on Spring module your classes use.
Currently Auto-mocker context suports :
* mocking of any Property sources (`@PropertySource`, `PropertyPlaceHolderConfigurer`, `PropertyPlaceholderAutoConfiguration`, etc.)
* mocking of @Controller beans

```java
try (ConfigurableApplicationContext context = AutoMockerContextBuilder.newBuilder()
				.mockPropertySources(StringMap.of("test", "43")).buildWithJavaConfig(Application.class)) {
	context.start();

	context.getBean(MockMvc.class)
		.perform(MockMvcRequestBuilders.get("/post/message1"))
		.andExpect(MockMvcResultMatchers.status().isOk());
}
```