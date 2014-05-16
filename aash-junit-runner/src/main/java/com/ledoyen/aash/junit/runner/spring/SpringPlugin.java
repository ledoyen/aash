package com.ledoyen.aash.junit.runner.spring;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ledoyen.aash.junit.runner.PluginAdapter;

public class SpringPlugin extends PluginAdapter {

	private SpringJUnit4ClassRunnerExposer delegateRunner;

	public void init() {
		try {
			this.delegateRunner = new SpringJUnit4ClassRunnerExposer(context.getTestClazz());
		} catch (InitializationError e) {
			throw new RuntimeException(e);
		}
	}

	public void prepare(Object testInstance) {
		try {
			delegateRunner.getTCM().prepareTestInstance(testInstance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class SpringJUnit4ClassRunnerExposer extends SpringJUnit4ClassRunner {

		public SpringJUnit4ClassRunnerExposer(Class<?> clazz) throws InitializationError {
			super(clazz);
		}
		
		public TestContextManager getTCM() {
			return getTestContextManager();
		}
	}
}
