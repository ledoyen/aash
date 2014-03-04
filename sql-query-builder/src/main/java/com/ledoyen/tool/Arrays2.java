package com.ledoyen.tool;

import com.google.common.base.Objects;

public final class Arrays2 {

	private Arrays2() {
	}

	public static boolean contains(Object[] array, Object value) {
		boolean result = false;
		for (Object v : array) {
			if (Objects.equal(value, v)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
