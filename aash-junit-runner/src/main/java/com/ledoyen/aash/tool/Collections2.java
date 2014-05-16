package com.ledoyen.aash.tool;

import java.util.Collection;

public final class Collections2 {

	private Collections2() {}

	public static <T> void foreach(Collection<T> coll, UnitFunction<T> function) {
		for(T o : coll) {
			function.apply(o);
		}
	}
}
