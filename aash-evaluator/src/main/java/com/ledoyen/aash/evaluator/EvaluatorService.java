package com.ledoyen.aash.evaluator;

import java.util.Arrays;
import java.util.List;

import org.codehaus.commons.compiler.CompileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ledoyen.aash.evaluator.cache.ExpressionCache;
import com.ledoyen.aash.evaluator.core.CompiledExpression;
import com.ledoyen.aash.evaluator.core.EvaluationException;
import com.ledoyen.aash.evaluator.core.ExpressionCompiler;
import com.ledoyen.aash.evaluator.valuestore.ValueStore;

public class EvaluatorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorService.class);

	private ExpressionCache cache;
	private ValueStore store;
	private Class<?>[] staticClasses;

	public void processEvaluations(Evaluation... evaluations) throws CompileException, EvaluationException {
		processEvaluations(Arrays.asList(evaluations));
	}

	public void processEvaluations(List<Evaluation> evaluations) throws CompileException, EvaluationException {
		ExpressionCompiler compiler = ExpressionCompiler.build(staticClasses);
		for (int i = 0; i < evaluations.size(); i++) {
			Evaluation evaluation = evaluations.get(i);
			CompiledExpression<?> compiledExpression = compiler.compile(evaluation.getExpression(), evaluation.getParameterNames(), evaluation.getParameterTypes(), evaluation.getReturnType());
			Object value = compiledExpression.evaluate(EvaluatorUtils.getParametersFromStore(store, evaluation.getParameterNames()));
			store.put(evaluation.getName(), value);
		}
	}

	public void setCache(ExpressionCache cache) {
		this.cache = cache;
	}

	public void setStore(ValueStore store) {
		this.store = store;
	}

	public void setStaticClasses(Class<?>[] staticClasses) {
		this.staticClasses = staticClasses;
	}
}
