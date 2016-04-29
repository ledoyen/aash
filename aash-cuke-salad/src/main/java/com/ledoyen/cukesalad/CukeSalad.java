package com.ledoyen.cukesalad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.runners.model.InitializationError;

import com.ledoyen.cukesalad.core.CukeSaladContext;
import com.ledoyen.cukesalad.tools.StringMap;

import cucumber.api.java.ObjectFactory;
import cucumber.api.junit.Cucumber;

public class CukeSalad extends Cucumber {

	private static Class<?> TEST_CLASS;
	static {
		List<String> args = new ArrayList<>();
		args.add("-g classpath:com/ledoyen/cukesalad/stepdef");
		System.setProperty(ObjectFactory.class.getName(), CukeSaladContext.class.getName());
		System.setProperty("cucumber.options", args.stream().collect(Collectors.joining(" ")));
	}

	public CukeSalad(Class<?> clazz) throws InitializationError, IOException {
		super(clazz);
		TEST_CLASS = getTestClass().getJavaClass();
	}

	public static Map<String, String> map(String... keysAndValues) {
		return StringMap.of(keysAndValues);
	}

	public static Class<?> getBaseClass() {
		return TEST_CLASS;
	}
}
