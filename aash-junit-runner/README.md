# Aash JUnit Runner

__Requires Junit4 or Higher__

This is a tool to use together all features from multiple well-known JUnit Runners such as :
- SpringJUnit4ClassRunner
- MockitoJUnitRunner

To enable on-demand features, Aash JUnit Runner uses user-defined plugins through the `@PluginConfiguration` annotation :

```java
@RunWith(AashJUnitRunner.class)
@PluginConfiguration({SpringPlugin.class, MockitoPlugin.class})
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringAndMockitoTest {

	@Autowired
	private Pojo2 pojo2;

	@Value("${end.of.sentence}")
	private String endOfSentence;

	@Mock
	private Pojo pojo;

	@Test
	public void hellotest() {
		Mockito.when(pojo.getName()).thenReturn("lo ");
		assertThat(pojo2.getName() + pojo.getName() + endOfSentence, CoreMatchers.equalTo("Hello world."));
	}

	...
}
```
