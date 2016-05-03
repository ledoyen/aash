package com.ledoyen.cukesalad.automocker.extension.properties;

import org.springframework.mock.env.MockEnvironment;

public class MockEnvironmentHolder {

	private final MockEnvironment mockEnv;

	public MockEnvironmentHolder(MockEnvironment env) {
		this.mockEnv = env;
	}

	public MockEnvironment getMockEnv() {
		return mockEnv;
	}
}
