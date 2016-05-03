package com.ledoyen.cukesalad.automocker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ConfigurableApplicationContext;

import com.ledoyen.cukesalad.automocker.extension.mvc.ControllerMocker;
import com.ledoyen.cukesalad.automocker.extension.properties.PropertySourcesPlaceholderConfigurerMocker;
import com.ledoyen.cukesalad.automocker.extension.sql.DataSourceMocker;

public final class AutoMockerContextBuilder {

	private final Set<Class<?>> extensions = new HashSet<>();

	private final Map<String, String> mockedProperties = new HashMap<>();

	private AutoMockerContextBuilder() {
		extensions.add(ControllerMocker.class);
		extensions.add(PropertySourcesPlaceholderConfigurerMocker.class);
		extensions.add(DataSourceMocker.class);
	}

	public static AutoMockerContextBuilder newBuilder() {
		return new AutoMockerContextBuilder();
	}

	public AutoMockerContextBuilder mockPropertySources(Map<String, String> properties) {
		this.mockedProperties.putAll(properties);
		return this;
	}

	public ConfigurableApplicationContext buildWithJavaConfig(Class<?>... annotatedClasses) {
		return new AutoMockerAnnotationContext(mockedProperties, extensions, annotatedClasses);
	}
}
