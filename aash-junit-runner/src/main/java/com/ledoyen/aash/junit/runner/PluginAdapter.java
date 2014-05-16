package com.ledoyen.aash.junit.runner;

public abstract class PluginAdapter implements Plugin {

	protected TestContext context;

	public void setTestContext(TestContext context) {
		this.context = context;
	}

	public void init() {		
	}

	public void prepare(Object testInstance) {
	}

	public void beforeWithRules(Object testInstance) {
	}
}
