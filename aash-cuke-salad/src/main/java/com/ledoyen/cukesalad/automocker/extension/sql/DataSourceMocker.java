package com.ledoyen.cukesalad.automocker.extension.sql;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.MethodMetadata;

import com.ledoyen.cukesalad.automocker.api.BeanDefinitionRegistryPostProcessorAdapter;

public class DataSourceMocker extends BeanDefinitionRegistryPostProcessorAdapter {

	private boolean checked = false;

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		for (String beanName : registry.getBeanDefinitionNames()) {
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
			if (beanDef instanceof RootBeanDefinition) {
				RootBeanDefinition abd = (RootBeanDefinition) beanDef;
				try {
					Class<?> beanClazz = abd.resolveBeanClass(beanClassLoader);
					MethodMetadata factoryMetadata = getMethodMetadata(beanDef);
					boolean directReference = beanClazz != null && DataSource.class.isAssignableFrom(beanClazz);
					boolean factoryReference = factoryMetadata != null
							&& DataSource.class.isAssignableFrom(Class.forName(factoryMetadata.getReturnTypeName()));
					if (directReference || factoryReference) {
						checkH2Available();
						abd.setBeanClass(MockDataSourceFactory.class);
						abd.setFactoryBeanName(null);
						abd.setFactoryMethodName(null);
						abd.setPropertyValues(null);
						abd.setConstructorArgumentValues(null);
					}
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Cannot load class: " + beanDef.getBeanClassName(), ex);
				}
			}
		}
	}

	private void checkH2Available() {
		if (!checked) {
			checked = true;
			try {
				Class.forName("org.h2.jdbcx.JdbcConnectionPool");
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(
						"Could not mock " + DataSource.class.getSimpleName() + ": missing h2 dependency");
			}
		}
	}

	private static MethodMetadata getMethodMetadata(BeanDefinition beanDef) {
		return AnnotatedBeanDefinition.class.isAssignableFrom(beanDef.getClass())
				? ((AnnotatedBeanDefinition) beanDef).getFactoryMethodMetadata() : null;
	}
}
