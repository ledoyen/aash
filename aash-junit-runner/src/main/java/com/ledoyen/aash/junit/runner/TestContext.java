package com.ledoyen.aash.junit.runner;

public class TestContext {

	private Class<?> testClazz;

	public void setTestClazz(Class<?> testClazz) {
		this.testClazz = testClazz;
	}

	public Class<?> getTestClazz() {
		return testClazz;
	}
}
