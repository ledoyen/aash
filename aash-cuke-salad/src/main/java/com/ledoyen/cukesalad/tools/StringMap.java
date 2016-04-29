package com.ledoyen.cukesalad.tools;

import java.util.HashMap;
import java.util.Map;

public abstract class StringMap {

	private StringMap() {
	}

	public static Map<String, String> of(String... keysAndValues) {
		Map<String, String> map = new HashMap<>();
		String key = null;
		for (int i = 0; i < keysAndValues.length; i++) {
			if (i % 2 == 0) {
				key = keysAndValues[i];
			} else {
				map.put(key, keysAndValues[i]);
			}
		}
		return map;
	}
}
