package com.ledoyen.tool;

import java.util.Collection;

import com.google.common.base.Function;

public class CountFunction<T> implements Function<Collection<T>, Integer>{

	public Integer apply(Collection<T> input) {
		return input != null ? input.size() : 0;
	}
}
