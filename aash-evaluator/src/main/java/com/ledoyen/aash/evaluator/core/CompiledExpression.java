package com.ledoyen.aash.evaluator.core;

import java.lang.reflect.InvocationTargetException;

import org.codehaus.janino.ScriptEvaluator;

import com.ledoyen.aash.evaluator.util.ArrayUtils;
import com.ledoyen.aash.evaluator.util.Preconditions;

/**
 * Represents a compiled expression used for condition and formula.<br>
 * 
 * May wrap some optimization like direct evaluation (for example expressions
 * '2' is not evaluated using the compiler, but directly rendered as a
 * compatible return type.
 * 
 * @author L.LEDOYEN
 */
public class CompiledExpression<T> {

	/**
	 * if true, value is already calculated, {@link EvaluationException.se} may
	 * be null}
	 */
	private boolean softCompiled = false;

	private T value;
	private ScriptEvaluator se;

	private String expr;
	private String[] parameterNames;
	private Class<?>[] parameterTypes;
	private Class<T> returnType;

	CompiledExpression(ScriptEvaluator se, String expr, String[] parameterNames, Class<?>[] parameterTypes, Class<T> returnType) {
		this.se = se;

		this.expr = expr;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
		this.returnType = returnType;
	}

	CompiledExpression(T value, String expr, String[] parameterNames, Class<?>[] parameterTypes) {
		this.value = value;
		this.softCompiled = true;

		this.expr = expr;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Evaluate the previously compiled expression against given arguments.
	 * 
	 * @throws IllegalArgumentException
	 *             if argument array length is not equal to expression parameter
	 *             number
	 * @throws EvaluationException
	 *             if any issue occurs during evaluation. Beware of
	 *             NullPointerException generated by null arguments used as
	 *             primitive types (for example : (arg0: Double = null) -> arg0
	 *             / 10)
	 */
	public T evaluate(Object... arguments) throws EvaluationException {
		if (softCompiled) {
			return value;
		}
		Preconditions.checkArgument(ArrayUtils.getLength(arguments) == ArrayUtils.getLength(parameterNames),
				String.format("arguments must be of length %s", ArrayUtils.getLength(parameterNames)));
		if (arguments == null) {
			arguments = new Object[0];
		}
		try {
			Object[] adaptedArguments = ExpressionUtils.adaptToTypes(arguments, parameterTypes);
			return (T) ExpressionUtils.adaptToType(se.evaluate(adaptedArguments), returnType);
		} catch (IllegalArgumentException e) {
			throw new EvaluationException(String.format("%s : [%s]", e.getMessage(), ExpressionUtils.buildParams(parameterNames, parameterTypes, arguments)), e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof NullPointerException) {
				throw new EvaluationException(String.format("NullPointerException : [%s] is not compliant with arguments [%s]", expr,
						ExpressionUtils.buildParams(parameterNames, parameterTypes, arguments)));
			}
			throw new EvaluationException(e);
		}
	}
}