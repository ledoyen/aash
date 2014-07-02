package com.ledoyen.aash.evaluator.core;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import com.ledoyen.aash.evaluator.util.ArrayUtils;
import com.ledoyen.aash.evaluator.util.Preconditions;

/**
 * Compiler for java expression.<br>
 * 
 * @author L.LEDOYEN
 */
public final class ExpressionCompiler {

	private final static ClassLoader defaultClassLoader = ExpressionCompiler.class.getClassLoader();

	private final ClassLoader CL;

	private Class<?>[] staticClasses;

	public static ExpressionCompiler build(Class<?>... staticClasses) {
		return build(defaultClassLoader, staticClasses);
	}

	public static ExpressionCompiler build(ClassLoader cl, Class<?>... staticClasses) {
		return new ExpressionCompiler(defaultClassLoader, staticClasses);
	}

	private ExpressionCompiler(ClassLoader cl, Class<?>... staticClasses) {
		CL = cl;
		this.staticClasses = staticClasses;
	}

	public <T> CompiledExpression<T> compile(String expr, Class<T> returnType) throws CompileException {
		return compile(expr, null, null, returnType);
	}

	/**
	 * Compile given expression.<br>
	 * 
	 * @param parameterNames
	 *            must have the same length than <b>parameterTypes</b>
	 * @param returnType
	 *            can be null (or @ link Void} ), but evaluation of the
	 *            resulting expression will return <i><b>null</b></i>.
	 */
	public <T> CompiledExpression<T> compile(String expr, String[] parameterNames, Class<?>[] parameterTypes, Class<T> returnType) throws CompileException {
		Preconditions.checkArgument(ArrayUtils.getLength(parameterNames) == ArrayUtils.getLength(parameterTypes), "parameter names and types must be of the same length");
		Preconditions.checkArgument(noEmptyCell(parameterTypes), "Parameter types must not contains null cells [%s]",
				ExpressionUtils.buildParams(parameterNames, parameterTypes));

		// Avoid-compilation optimization
		if (ArrayUtils.getLength(parameterNames) == 0) {
			T value = null;
			if (Number.class.equals(returnType) || Integer.class.equals(returnType)) {
				value = getInteger(expr);
			} else if (Double.class.equals(returnType)) {
				value = getDouble(expr);
			} else if (Long.class.equals(returnType)) {
				value = getLong(expr);
			} else if (Boolean.class.equals(returnType)) {
				value = getBoolean(expr);
			}
			if (value != null) {
				return new CompiledExpression<T>(value, expr, parameterNames, parameterTypes);
			}
		}

		ScriptEvaluator se = new ScriptEvaluator();
		se.setParentClassLoader(CL);
		if (staticClasses != null) {
			se.setDefaultImports(ExpressionUtils.declareStaticImports(staticClasses));
		}

		if (parameterNames != null && parameterNames.length > 0) {
			se.setParameters(parameterNames, parameterTypes);
		}
		if (returnType == null || Void.class.equals(returnType)) {
			se.cook(String.format("%s;", expr));
		} else {
			if(Number.class.isAssignableFrom(returnType)) {
				se.setReturnType(Number.class);
			} else {
				se.setReturnType(returnType);
			}
			se.cook(String.format("return %s;", expr));
		}

		return new CompiledExpression<T>(se, expr, parameterNames, parameterTypes, returnType);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getInteger(String expr) {
		try {
			return (T) Integer.valueOf(expr);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getDouble(String expr) {
		try {
			return (T) Double.valueOf(expr);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getLong(String expr) {
		try {
			return (T) Long.valueOf(expr);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getBoolean(String expr) {
		if ("true".equalsIgnoreCase(expr) || "false".equalsIgnoreCase(expr)) {
			return (T) Boolean.valueOf(expr);
		}
		return null;
	}

	private static boolean noEmptyCell(Object[] objs) {
		if (objs == null)
			return true;
		for (Object o : objs) {
			if (o == null)
				return false;
		}
		return true;
	}
}
