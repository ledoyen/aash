package cucumber.runtime;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.ledoyen.cukesalad.automocker.AutoMockerContextBuilder;
import com.ledoyen.cukesalad.tools.StringMap;

import cucumber.api.java.ObjectFactory;

public class CukeSaladObjectFactory implements ObjectFactory {

	private ConfigurableApplicationContext context;

	private Map<Class<?>, Object> instanceCache = new HashMap<>();

	@Override
	public void start() {
		context = AutoMockerContextBuilder.newBuilder().mockPropertySources(StringMap.of("test", "43"))
				.buildWithJavaConfig(CukeSalad.TEST_CLASS.getAnnotation(ContextConfiguration.class).classes());
	}

	@Override
	public void stop() {
	}

	@Override
	public void addClass(Class<?> glueClass) {
	}

	@Override
	public <T> T getInstance(Class<T> glueClass) {
		if (!instanceCache.containsKey(glueClass)) {
			try {
				T instance = glueClass.newInstance();
				context.getAutowireCapableBeanFactory().autowireBean(instance);
				instanceCache.put(glueClass, instance);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		return (T) instanceCache.get(glueClass);
	}

}
