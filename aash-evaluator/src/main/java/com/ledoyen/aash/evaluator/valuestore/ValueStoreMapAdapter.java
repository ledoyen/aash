package com.ledoyen.aash.evaluator.valuestore;

import java.util.Map;

public class ValueStoreMapAdapter implements ValueStore {

	private Map<String, Object> delegate;

	public ValueStoreMapAdapter(Map<String, Object> delegate) {
		this.delegate = delegate;
	}

	public void put(String name, Object value) {
		delegate.put(name, value);
	}

	public Object get(String name) {
		return delegate.get(name);
	}

	public boolean containsKey(String name) {
		return delegate.containsKey(name);
	}
}
