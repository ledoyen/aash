package com.ledoyen.cukesalad.automocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.ledoyen.cukesalad.automocker.api.AvailableBeanDefinitionAttributes;

class AutoMockerAnnotationContext extends AnnotationConfigApplicationContext {

	private static final ByteArrayResource EMPTY_RESOURCE = new ByteArrayResource(new byte[] {});

	private final Map<String, String> applicationProperties;
	private final List<String> propertySourcesLocations;
	private final Set<Class<?>> extensions;

	public AutoMockerAnnotationContext(Map<String, String> applicationProperties, Set<Class<?>> extensions,
			Class<?>... annotatedClasses) {
		this.applicationProperties = applicationProperties;
		this.extensions = extensions;
		// @PropertySource(s) locations are identified here as it is resolved at first by ConfigurationClassParser
		// this class is then able to substitute an EMPTY_RESOURCE when used as ResourceLoader
		this.propertySourcesLocations = listPropertySourcesLocations(annotatedClasses);

		register(annotatedClasses);
		refresh();
	}

	public Resource getResource(String location) {
		final Resource modifiedLocation;
		if (propertySourcesLocations.contains(location)) {
			modifiedLocation = EMPTY_RESOURCE;
		} else {
			modifiedLocation = super.getResource(location);
		}
		return modifiedLocation;
	}

	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.prepareBeanFactory(beanFactory);
		for (Class<?> extentionClass : extensions) {
			RootBeanDefinition def = new RootBeanDefinition(extentionClass);
			def.setAttribute(AvailableBeanDefinitionAttributes.ATTRIBUTE_MOCKED_PROPERTY_SOURCE, applicationProperties);
			registerBeanDefinition(extentionClass.getSimpleName(), def);
		}
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
