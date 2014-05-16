package com.ledoyen.aash.junit.runner;

import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;

public class AashJUnitSuiteExposer extends Suite {

	public AashJUnitSuiteExposer(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner> emptyList());
	}

	public List<Runner> getC() {
		return getChildren();
	}

	public Description describeC(Runner child) {
		return describeChild(child);
	}

	public void runC(Runner runner, RunNotifier notifier) {
		runChild(runner, notifier);
	}
}
