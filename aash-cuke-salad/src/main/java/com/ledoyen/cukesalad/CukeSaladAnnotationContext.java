package com.ledoyen.cukesalad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

public class CukeSaladAnnotationContext extends AnnotationConfigApplicationContext {

	static final String MOCK_POST_PROCESSOR_NAME = "ioMockPostProcessor";
	static final String APPLICATION_PROPERTIES_ATTRIBUTE_NAME = "applicationProperties";

	private final Map<String, String> applicationProperties;
	private final List<String> propertySourcesLocations;

	public CukeSaladAnnotationContext(Map<String, String> applicationProperties, Class<?>... annotatedClasses) {
		super();
		this.applicationProperties = applicationProperties;
		this.propertySourcesLocations = listPropertySourcesLocations(annotatedClasses);

		register(annotatedClasses);
		refresh();
	}

	public Resource getResource(String location) {
		final String modifiedLocation;
		if (propertySourcesLocations.contains(location)) {
			modifiedLocation = "classpath:empty.properties";
		} else {
			modifiedLocation = location;
		}
		return super.getResource(modifiedLocation);
	}

	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.prepareBeanFactory(beanFactory);
		RootBeanDefinition def = new RootBeanDefinition(IoMockPostProcessor.class);
		def.setAttribute(APPLICATION_PROPERTIES_ATTRIBUTE_NAME, applicationProperties);
		registerBeanDefinition(MOCK_POST_PROCESSOR_NAME, def);
	}

	private static List<String> listPropertySourcesLocations(Class<?>[] annotatedClasses) {
		List<String> locations = new ArrayList<String>();
		for (Class<?> annotatedClass : annotatedClasses) {
			for (PropertySource ps : annotatedClass.getAnnotationsByType(PropertySource.class)) {
				locations.addAll(Arrays.asList(ps.value()));
			}
		}
		return locations;
	}
}
