package com.ledoyen.aash.evaluator;

import com.ledoyen.aash.evaluator.valuestore.ValueStore;

public class EvaluatorUtils {

	public static Object[] getParametersFromStore(ValueStore store, String[] parameterNames) {
		if(parameterNames == null || parameterNames.length == 0) {
			return new Object[0];
		}
		Object[] values = new Object[parameterNames.length];
		for(int i = 0; i < parameterNames.length; i++) {
			values[i] = store.get(parameterNames[i]);
		}
		return values;
	}
}
