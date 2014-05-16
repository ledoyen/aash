package com.ledoyen.aash.junit.runner.mockito;

import org.mockito.MockitoAnnotations;

import com.ledoyen.aash.junit.runner.PluginAdapter;

public class MockitoPlugin extends PluginAdapter {

	public void beforeWithRules(Object testInstance) {
		MockitoAnnotations.initMocks(testInstance);
	}
}
