package cucumber.runtime;

import java.io.IOException;

import org.junit.runners.model.InitializationError;

import cucumber.api.junit.Cucumber;

public class CukeSalad extends Cucumber {

	static Class<?> TEST_CLASS;

	public CukeSalad(Class<?> clazz) throws InitializationError, IOException {
		super(clazz);
		TEST_CLASS = getTestClass().getJavaClass();
	}
}
