package com.ledoyen.aash.evaluator.core;

import static org.junit.Assert.assertThat;

import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.commons.compiler.CompileException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class CoreTest {

	@Test
	@Parameters
	public <T> void testCompilationAndEvaluation(String expr, String[] parameterNames, Class<?>[] parameterTypes, Object[] parameterValues, Class<T> returnType,
			T expectedValue) throws CompileException, EvaluationException {
		CompiledExpression<T> result = ExpressionCompiler.build(Math.class, DateUtils.class).compile(expr, parameterNames, parameterTypes, returnType);
		assertThat(result.evaluate(parameterValues), CoreMatchers.equalTo(expectedValue));
	}

	/** Used by junitparams */
	@SuppressWarnings("unused")
	private Object[] parametersForTestCompilationAndEvaluation() {
		return new Object[][] { //
				// addDays
				{ "true", null, null, null, Boolean.class, true }, //
				{ "1 == 1", null, null, null, Boolean.class, true }, //
				{ "1 == 0", null, null, null, Boolean.class, false }, //
				{ "2", null, null, null, Integer.class, 2 }, //
				{ "1 + 2", null, null, null, Integer.class, 3 }, //
				{ "abs(-4)", null, null, null, Integer.class, 4 }, //
				{ "var1 == 2 || var1 == 3", new String[] { "var1" }, new Class<?>[] { Integer.class }, new Object[] { 3 }, Boolean.class, true }, //
				{ "(var1 == 2 || var1 == 3) ? var2 : 2d", new String[] { "var1", "var2" }, new Class<?>[] { Integer.class, double.class },
						new Object[] { 3, 5d }, double.class, 5d }, //
				{ "addDays(var1, 3)", new String[] { "var1" }, new Class<?>[] { Date.class }, new Object[] { DateUtils.addDays(new Date(0l), 3) }, Date.class, DateUtils.addDays(new Date(0l), 6) }, //
				{ "addDays(var1, 3)", new String[] { "var1" }, new Class<?>[] { Date.class }, new Object[] { DateUtils.addDays(new Date(0l), 3).getTime() }, Date.class, DateUtils.addDays(new Date(0l), 6) }, //
						
		};
	}
}
