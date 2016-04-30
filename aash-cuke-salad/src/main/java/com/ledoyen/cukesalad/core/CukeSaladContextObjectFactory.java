package com.ledoyen.cukesalad.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import com.ledoyen.cukesalad.CukeSalad;
import com.ledoyen.cukesalad.CukeSaladConfiguration;
import com.ledoyen.cukesalad.MockProperties;
import com.ledoyen.cukesalad.automocker.AutoMockerContextBuilder;

import cucumber.api.java.ObjectFactory;

public class CukeSaladContextObjectFactory implements ObjectFactory, CukeSaladContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(CukeSaladContextObjectFactory.class);

	private ConfigurableApplicationContext context;

	private Map<Class<?>, Object> instanceCache = new HashMap<>();

	private Class<?> baseClass;
	private CukeSaladConfiguration configuration;
	private Map<String, String> properties;
	private Map<String, Class<?>> classesBySimpleName;

	public Map<String, Class<?>> getClassesBySimpleName() {
		return classesBySimpleName;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		baseClass = CukeSalad.getBaseClass();
		configuration = baseClass.getAnnotation(CukeSaladConfiguration.class);

		properties = new HashMap<>();
		for (Method m : baseClass.getDeclaredMethods()) {
			if (m.isAnnotationPresent(MockProperties.class)) {
				if (!Modifier.isStatic(m.getModifiers()) || m.getParameterCount() > 0
						|| !Map.class.isAssignableFrom(m.getReturnType())) {
					throw new IllegalStateException("Methods annotated with @" + MockProperties.class.getSimpleName()
							+ " should be static, take no parameters and return a Map of strings");
				}
				m.setAccessible(true);
				try {
					properties.putAll((Map<? extends String, ? extends String>) m.invoke(null));
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new IllegalStateException(
							"Cannot invoke method annotated with @" + MockProperties.class.getSimpleName(), e);
				}
			}
		}
	}

	@Override
	public void start() {
		if (baseClass == null) {
			init();
		}

		if (context == null || configuration.reloadBeforeTest()) {
			refreshContext();
		}
		context.start();
	}

	private void refreshContext() {
		StopWatch sw = new StopWatch();
		sw.start();
		context = AutoMockerContextBuilder.newBuilder().mockPropertySources(properties)
				.buildWithJavaConfig(configuration.classes());
		classesBySimpleName = new HashMap<>();

		context.getBeanFactory().registerSingleton("cukeSaladContext", this);
		for (String beanName : context.getBeanDefinitionNames()) {
			Class<?> classUsedBySpring = context.getBean(beanName).getClass();
			extractSimpleNames(classesBySimpleName, classUsedBySpring);
		}
		classesBySimpleName.put(String.class.getSimpleName(), String.class);
		sw.stop();
		LOGGER.info("Context started in " + sw.getTotalTimeSeconds() + " seconds");
	}

	private static void extractSimpleNames(Map<String, Class<?>> classesBySimpleName, Class<?> clazz) {
		if (clazz == null || Object.class.equals(clazz) || clazz.isPrimitive()) {
			return;
		} else {
			classesBySimpleName.put(clazz.getSimpleName(), clazz);
			extractSimpleNames(classesBySimpleName, clazz.getSuperclass());
			for (Class<?> intertface : clazz.getInterfaces()) {
				extractSimpleNames(classesBySimpleName, intertface);
			}
		}

	}

	@Override
	public void stop() {
		context.stop();
	}

	@Override
	@SuppressWarnings("unchecked")
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

	@Override
	public void addClass(Class<?> glueClass) {
	}
}
