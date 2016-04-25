package com.ledoyen.cukesalad.tools;

import java.util.HashMap;
import java.util.Map;

public abstract class StringMap {

	private StringMap() {
	}

	public static Map<String, String> of(String... keyAndValues) {
		Map<String, String> map = new HashMap<>();
		String key = null;
		for (int i = 0; i < keyAndValues.length; i++) {
			if (i % 2 == 0) {
				key = keyAndValues[i];
			} else {
				map.put(key, keyAndValues[i]);
			}
		}
		return map;
	}
}
