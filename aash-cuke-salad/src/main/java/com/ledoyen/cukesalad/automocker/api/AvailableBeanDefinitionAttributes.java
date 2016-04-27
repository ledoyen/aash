package com.ledoyen.cukesalad.automocker.api;

public interface AvailableBeanDefinitionAttributes {

	/**
	 * Attribute containing a {@link java.util.Map Map[String, String]} of properties supplied by tester as
	 * {@link org.springframework.context.annotation.PropertySource @PropertySource} replacement.
	 */
	String ATTRIBUTE_MOCKED_PROPERTY_SOURCE = "mockedPropertySource";
}
