package com.ledoyen.aash.evaluator.valuestore;

public interface ValueStore {

	void put(String name, Object value);

	Object get(String name);

	boolean containsKey(String name);
}
