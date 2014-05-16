package com.ledoyen.aash.junit.runner;

public interface Plugin {

	void setTestContext(TestContext context);

	void init();

	/** Executed just after test is created by {@link org.junit.runners.BlockJUnit4ClassRunner#createTest}. */
	void prepare(Object testInstance);

	/** Executed just before rules are identified by {@link org.junit.runners.BlockJUnit4ClassRunner#getTestRules}. */
	void beforeWithRules(Object testInstance);
}
