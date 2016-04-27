package com.ledoyen.cukesalad.automocker.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.ClassUtils;

public abstract class BeanDefinitionRegistryPostProcessorAdapter
		implements BeanDefinitionRegistryPostProcessor, BeanNameAware {

	protected final ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	protected String beanName;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// used by sub classes
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// used by sub classes
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(ConfigurableListableBeanFactory beanDefinitionFactory, String attributeName) {
		return (T) beanDefinitionFactory.getBeanDefinition(beanName).getAttribute(attributeName);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(BeanDefinitionRegistry beanDefinitionRegistry, String attributeName) {
		return (T) beanDefinitionRegistry.getBeanDefinition(beanName).getAttribute(attributeName);
	}
}
