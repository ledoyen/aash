package com.ledoyen.cukesalad.automocker.extension.properties;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;

import com.ledoyen.cukesalad.automocker.api.AvailableBeanDefinitionAttributes;
import com.ledoyen.cukesalad.automocker.api.BeanDefinitionRegistryPostProcessorAdapter;

public class PropertySourcesPlaceholderConfigurerMocker extends BeanDefinitionRegistryPostProcessorAdapter
		implements BeanNameAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertySourcesPlaceholderConfigurerMocker.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Map<String, String> applicationProperties = getAttribute(beanFactory,
				AvailableBeanDefinitionAttributes.ATTRIBUTE_MOCKED_PROPERTY_SOURCE);
		ConfigurableEnvironment env = beanFactory.getBean(ConfigurableEnvironment.class);
		MockEnvironment mockEnv = new MockEnvironment();
		applicationProperties.entrySet().forEach(e -> mockEnv.setProperty(e.getKey(), e.getValue()));
		env.merge(mockEnv);
		beanFactory.registerSingleton("mockEnvironment", new MockEnvironmentHolder(mockEnv));
		LOGGER.debug("Mocking up " + ConfigurableEnvironment.class.getSimpleName());
	}
}
