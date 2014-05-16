package com.ledoyen.aash.junit.runner.parameters;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ParametersRunnerTest extends AbstractParametersTest {

	public ParametersRunnerTest(final String phoneNumber, final boolean isValidPhoneNumber) {
		super(phoneNumber, isValidPhoneNumber);
	}
}
