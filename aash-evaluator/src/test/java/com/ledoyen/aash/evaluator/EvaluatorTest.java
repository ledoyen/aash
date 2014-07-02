package com.ledoyen.aash.evaluator;

import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.compiler.CompileException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.ledoyen.aash.evaluator.core.EvaluationException;

public class EvaluatorTest {

	private Map<String, Object> valueStore;
	private EvaluatorService service;

	@Before
	public void setUp() {
		valueStore = new HashMap<>();
		service = EvaluatorServiceBuilder.newBuilder() //
				.setStore(valueStore) //
				.setStaticClasses(StringUtils.class, Math.class) //
				.build();
	}

	@Test
	public void testEvaluation() throws CompileException, EvaluationException {
		assertThat(valueStore.get("toto"), CoreMatchers.equalTo(null));
		assertThat(valueStore.get("titi"), CoreMatchers.equalTo(null));
		assertThat(valueStore.get("tutu"), CoreMatchers.equalTo(null));

		service.processEvaluations( //
				Evaluation.build("toto", "4", Integer.class), //
				Evaluation.build("titi", "toto + 3", new String[] { "toto" }, new Class<?>[] { Long.class }, Long.class), //
				// Function from Math.class
				Evaluation.build("tutu", "max(toto, 5)", new String[] { "toto" }, new Class<?>[] { Integer.class }, Double.class));

		assertThat((Integer) valueStore.get("toto"), CoreMatchers.equalTo(4));
		assertThat((Long) valueStore.get("titi"), CoreMatchers.equalTo(7l));
		assertThat((Double) valueStore.get("tutu"), CoreMatchers.equalTo(5d));

		service.processEvaluations(
				// Function from StringUtils.class
				Evaluation.build("text1", "repeat('1', 2)", String.class),
				Evaluation.build("text2", "text1 + \"22\"", new String[] { "text1" }, new Class<?>[] { String.class }, String.class), //
				Evaluation.build("textToLong", "text2", new String[] { "text2" }, new Class<?>[] { Long.class }, Long.class));

		assertThat((String) valueStore.get("text1"), CoreMatchers.equalTo("11"));
		assertThat((String) valueStore.get("text2"), CoreMatchers.equalTo("1122"));
		assertThat((Long) valueStore.get("textToLong"), CoreMatchers.equalTo(1122l));
	}
}
