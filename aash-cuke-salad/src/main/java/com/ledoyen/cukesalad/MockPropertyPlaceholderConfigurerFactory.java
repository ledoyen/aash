package com.ledoyen.cukesalad;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class MockPropertyPlaceholderConfigurerFactory implements FactoryBean<PropertyPlaceholderConfigurer> {

	private Properties properties;

	private PropertyPlaceholderConfigurer singleton;

	public MockPropertyPlaceholderConfigurerFactory(Map<String, String> applicationProperties) {
		this.singleton = new PropertyPlaceholderConfigurer();
		this.properties = new Properties();
		this.singleton.setProperties(this.properties);
		applicationProperties.entrySet().forEach(e -> properties.setProperty(e.getKey(), e.getValue()));
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
