package com.ledoyen.cukesalad.automocker.extension.properties;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.MethodMetadata;

import com.ledoyen.cukesalad.automocker.api.AvailableBeanDefinitionAttributes;
import com.ledoyen.cukesalad.automocker.api.BeanDefinitionRegistryPostProcessorAdapter;

public class PropertySourcesPlaceholderConfigurerMocker extends BeanDefinitionRegistryPostProcessorAdapter
		implements BeanNameAware {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Map<String, String> applicationProperties = getAttribute(beanFactory,
				AvailableBeanDefinitionAttributes.ATTRIBUTE_MOCKED_PROPERTY_SOURCE);
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
			if (beanDef instanceof RootBeanDefinition) {
				RootBeanDefinition abd = (RootBeanDefinition) beanDef;
				try {
					Class<?> beanClazz = abd.resolveBeanClass(beanClassLoader);
					MethodMetadata factoryMetadata = getMethodMetadata(beanDef);
					boolean directReference = beanClazz != null
							&& PropertySourcesPlaceholderConfigurer.class.isAssignableFrom(beanClazz);
					boolean factoryReference = factoryMetadata != null && PropertySourcesPlaceholderConfigurer.class
							.isAssignableFrom(Class.forName(factoryMetadata.getReturnTypeName()));
					if (directReference || factoryReference) {
						abd.setBeanClass(MockPropertyPlaceholderConfigurerFactory.class);
						abd.setFactoryMethodName(null);
						ConstructorArgumentValues cav = new ConstructorArgumentValues();
						cav.addGenericArgumentValue(applicationProperties);
						abd.setConstructorArgumentValues(cav);
					}
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Cannot load class: " + beanDef.getBeanClassName(), ex);
				}
			}
		}
	}

	private static MethodMetadata getMethodMetadata(BeanDefinition beanDef) {
		return AnnotatedBeanDefinition.class.isAssignableFrom(beanDef.getClass())
				? ((AnnotatedBeanDefinition) beanDef).getFactoryMethodMetadata() : null;
	}
}
