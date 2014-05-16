package com.ledoyen.aash.junit.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;

import com.ledoyen.aash.tool.Collections2;
import com.ledoyen.aash.tool.Reflections;
import com.ledoyen.aash.tool.UnitFunction;

public class AashCoreRunner extends BlockJUnit4ClassRunner {

	private List<Plugin> plugins = new ArrayList<Plugin>();

	private TestContext context = new TestContext();

	public AashCoreRunner(Class<?> klass) throws InitializationError {
		super(klass);
		context.setTestClazz(klass);
		init();
	}

	/**
	 * Delegates to the parent implementation for creating the test instance and
	 * then allows the {@link #getTestContextManager() TestContextManager} to
	 * prepare the test instance before returning it.
	 * @see TestContextManager#prepareTestInstance(Object)
	 */
	@Override
	protected Object createTest() throws Exception {
		Object testInstance = super.createTest();
		for(Plugin plugin : plugins) {
			plugin.prepare(testInstance);
		}
		return testInstance;
	}

	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> rules = super.getTestRules(target);
		for(Plugin plugin : plugins) {
			plugin.beforeWithRules(target);
		}
		return rules;
	}

	private void init() {
		PluginConfiguration configuration = context.getTestClazz().getAnnotation(PluginConfiguration.class);
		if(configuration != null) {
			Class<? extends Plugin>[] clazzes = configuration.value();
			plugins = Reflections.instanciate(Arrays.asList(clazzes));
			
			Collections2.foreach(plugins, new UnitFunction<Plugin>() {
				public void apply(Plugin input) {
					input.setTestContext(context);
				}
			});
			
			Collections2.foreach(plugins, new UnitFunction<Plugin>() {
				public void apply(Plugin input) {
					input.init();
				}
			});
		}
	}
}
