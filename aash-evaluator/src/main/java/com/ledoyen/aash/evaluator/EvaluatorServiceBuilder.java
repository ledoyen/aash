package com.ledoyen.aash.evaluator;

import java.util.Map;

import com.ledoyen.aash.evaluator.cache.ExpressionCache;
import com.ledoyen.aash.evaluator.cache.ExpressionCacheMapAdapter;
import com.ledoyen.aash.evaluator.core.CompiledExpression;
import com.ledoyen.aash.evaluator.valuestore.ValueStore;
import com.ledoyen.aash.evaluator.valuestore.ValueStoreMapAdapter;

public final class EvaluatorServiceBuilder {

	private ExpressionCache cache;
	private ValueStore store;
	private Class<?>[] staticClasses;

	private EvaluatorServiceBuilder() {
	}

	public static EvaluatorServiceBuilder newBuilder() {
		return new EvaluatorServiceBuilder();
	}

	public EvaluatorServiceBuilder setCache(Map<String, CompiledExpression<?>> cache) {
		return setCache(new ExpressionCacheMapAdapter(cache));
	}

	public EvaluatorServiceBuilder setCache(ExpressionCache cache) {
		if (this.cache == null) {
			this.cache = cache;
			return this;
		} else {
			throw new IllegalArgumentException("Cache is already set");
		}
	}

	public EvaluatorServiceBuilder setStore(Map<String, Object> store) {
		return setStore(new ValueStoreMapAdapter(store));
	}

	public EvaluatorServiceBuilder setStore(ValueStore store) {
		if (this.store == null) {
			this.store = store;
			return this;
		} else {
			throw new IllegalArgumentException("Store is already set");
		}
	}

	public EvaluatorServiceBuilder setStaticClasses(Class<?>... staticClasses) {
		if (this.staticClasses == null) {
			this.staticClasses = staticClasses;
			return this;
		} else {
			throw new IllegalArgumentException("Static classes are already set");
		}
	}

	public EvaluatorService build() {
		EvaluatorService service = new EvaluatorService();
		if (cache != null) {
			service.setCache(cache);
		}
		if (store != null) {
			service.setStore(store);
		} else {
			throw new IllegalArgumentException("no valueStore is specified");
		}
		if (staticClasses != null) {
			service.setStaticClasses(staticClasses);
		}
		return service;
	}
}
