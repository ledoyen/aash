package com.ledoyen.cukesalad;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

public class IoMockPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		@SuppressWarnings("unchecked")
		Map<String, String> applicationProperties = (Map<String, String>) beanFactory
				.getBeanDefinition(CukeSaladAnnotationContext.MOCK_POST_PROCESSOR_NAME)
				.getAttribute(CukeSaladAnnotationContext.APPLICATION_PROPERTIES_ATTRIBUTE_NAME);
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
			if (beanDef instanceof RootBeanDefinition) {
				RootBeanDefinition abd = (RootBeanDefinition) beanDef;
				try {
					Class<?> beanClazz = abd.resolveBeanClass(beanClassLoader);
					if (beanName.equals("propertySourcesPlaceholderConfigurer")) {
						abd.setBeanClass(MockPropertyPlaceholderConfigurerFactory.class);
						abd.setFactoryMethodName(null);
						ConstructorArgumentValues cav = new ConstructorArgumentValues();
						cav.addGenericArgumentValue(applicationProperties);
						abd.setConstructorArgumentValues(cav);
						System.out.println("Replacing pspc by ppc");
					}
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Cannot load class: " + beanDef.getBeanClassName(), ex);
				}
			}
		}
		// ((AbstractBeanDefinition) beanFactory.getBeanDefinition("propertySourcesPlaceholderConfigurer")).getBeanClass();
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		boolean controllerRegistered = false;
		for (String beanName : registry.getBeanDefinitionNames()) {
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
			if (beanDef instanceof AbstractBeanDefinition) {
				AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDef;
				try {
					Class<?> beanClazz = abd.resolveBeanClass(beanClassLoader);
					if (beanClazz != null && AnnotationUtils.findAnnotation(beanClazz, Controller.class) != null) {
						controllerRegistered = true;
						System.out.println(beanName + abd);
					}
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Cannot load class: " + beanDef.getBeanClassName(), ex);
				}
			}
		}

		if (controllerRegistered) {
			registry.registerBeanDefinition("mockMvc", new RootBeanDefinition(MockMvcFactory.class));
		}
	}

}
