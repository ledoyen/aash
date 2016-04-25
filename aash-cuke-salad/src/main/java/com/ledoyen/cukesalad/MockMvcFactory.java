package com.ledoyen.cukesalad;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MockMvcFactory implements FactoryBean<MockMvc>, ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;
	private MockMvc singleton;

	@Override
	public MockMvc getObject() throws Exception {
		return singleton;
	}

	@Override
	public Class<?> getObjectType() {
		return MockMvc.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.singleton = MockMvcBuilders
				.standaloneSetup(applicationContext.getBeansWithAnnotation(Controller.class).values().toArray())
				.build();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
