package com.ledoyen.aash.evaluator.cache;

import com.google.common.cache.Cache;
import com.ledoyen.aash.evaluator.core.CompiledExpression;

public class ExpressionCacheGuavaAdapter implements ExpressionCache {

	private Cache<String, CompiledExpression<?>> delegate;

	public ExpressionCacheGuavaAdapter(Cache<String, CompiledExpression<?>> delegate) {
		this.delegate = delegate;
	}

	public void put(String expression, CompiledExpression<?> compiledExpression) {
		delegate.put(expression, compiledExpression);
	}

	public CompiledExpression<?> get(String expression) {
		return delegate.getIfPresent(expression);
	}

	public boolean containsKey(String expression) {
		return delegate.getIfPresent(expression) != null;
	}
}
