package com.ledoyen.cukesalad.automocker.extension.mvc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MockMvcFactory implements FactoryBean<MockMvc>, ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockMvcFactory.class);

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
		Map<String, Object> controllersByName = applicationContext.getBeansWithAnnotation(Controller.class);
		this.singleton = MockMvcBuilders.standaloneSetup(controllersByName.values().toArray()).build();
		LOGGER.info("Setting up " + MockMvc.class.getSimpleName() + " for controllers " + controllersByName.keySet());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
