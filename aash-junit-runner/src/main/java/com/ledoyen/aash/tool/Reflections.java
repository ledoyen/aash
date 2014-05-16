package com.ledoyen.aash.tool;

import java.util.ArrayList;
import java.util.List;

public final class Reflections {

	private Reflections() {}

	public static <T> List<T> instanciate(List<Class<? extends T>> clazzes) {
		List<T> result = new ArrayList<T>();
		for(Class<? extends T> clazz : clazzes) {
			try {
				result.add(clazz.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
