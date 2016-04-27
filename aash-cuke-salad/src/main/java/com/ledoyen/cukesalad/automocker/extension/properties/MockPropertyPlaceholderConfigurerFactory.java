package com.ledoyen.cukesalad.automocker.extension.properties;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

class MockPropertyPlaceholderConfigurerFactory implements FactoryBean<PropertyPlaceholderConfigurer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockPropertyPlaceholderConfigurerFactory.class);

	private final Properties properties;
	private final PropertyPlaceholderConfigurer singleton;

	public MockPropertyPlaceholderConfigurerFactory(Map<String, String> applicationProperties) {
		this.singleton = new PropertyPlaceholderConfigurer();
		this.properties = new Properties();
		this.singleton.setProperties(this.properties);
		applicationProperties.entrySet().forEach(e -> properties.setProperty(e.getKey(), e.getValue()));
		LOGGER.info("Mocking up " + PropertyPlaceholderConfigurer.class.getSimpleName());
	}

	@Override
	public PropertyPlaceholderConfigurer getObject() throws Exception {
		return singleton;
	}

	@Override
	public Class<?> getObjectType() {
		return PropertyPlaceholderConfigurer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
