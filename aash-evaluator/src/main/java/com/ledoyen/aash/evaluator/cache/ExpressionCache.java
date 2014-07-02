package com.ledoyen.aash.evaluator.cache;

import com.ledoyen.aash.evaluator.core.CompiledExpression;

public interface ExpressionCache {

	void put(String expression, CompiledExpression<?> compiledExpression);

	CompiledExpression<?> get(String expression);

	boolean containsKey(String expression);
}
