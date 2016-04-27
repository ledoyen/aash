package com.ledoyen.cukesalad.automocker.extension.mvc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

import com.ledoyen.cukesalad.automocker.api.BeanDefinitionRegistryPostProcessorAdapter;

@ConditionalOnClass(Controller.class)
public class ControllerMocker extends BeanDefinitionRegistryPostProcessorAdapter {

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
