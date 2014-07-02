package com.ledoyen.aash.evaluator.cache;

import java.util.Map;

import com.ledoyen.aash.evaluator.core.CompiledExpression;

public class ExpressionCacheMapAdapter implements ExpressionCache {

	private Map<String, CompiledExpression<?>> delegate;

	public ExpressionCacheMapAdapter(Map<String, CompiledExpression<?>> delegate) {
		this.delegate = delegate;
	}

	public void put(String expression, CompiledExpression<?> compiledExpression) {
		delegate.put(expression, compiledExpression);
	}

	public CompiledExpression<?> get(String expression) {
		return delegate.get(expression);
	}

	public boolean containsKey(String expression) {
		return delegate.containsKey(expression);
	}
}
