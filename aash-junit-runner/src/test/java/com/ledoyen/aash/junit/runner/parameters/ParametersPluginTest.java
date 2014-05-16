package com.ledoyen.aash.junit.runner.parameters;

import org.junit.runner.RunWith;

import com.ledoyen.aash.junit.runner.AashJUnitRunner;
import com.ledoyen.aash.junit.runner.PluginConfiguration;

@RunWith(AashJUnitRunner.class)
@PluginConfiguration(ParametersPlugin.class)
public class ParametersPluginTest extends AbstractParametersTest {

	public ParametersPluginTest(final String phoneNumber, final boolean isValidPhoneNumber) {
		super(phoneNumber, isValidPhoneNumber);
	}
}
