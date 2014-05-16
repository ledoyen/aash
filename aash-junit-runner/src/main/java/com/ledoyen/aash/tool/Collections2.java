package com.ledoyen.aash.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Collections2 {

	private Collections2() {
	}

	public static <T> void foreach(Collection<T> coll, UnitFunction<T> function) {
		for (T o : coll) {
			function.apply(o);
		}
	}

	public static <U, V> List<V> map(List<U> col, Function<U, V> mappingFunction) {
		List<V> result = new ArrayList<V>();
		for (U item : col) {
			result.add(mappingFunction.apply(item));
		}
		return result;
	}
}
