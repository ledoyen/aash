package com.ledoyen.aash.evaluator.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ledoyen.aash.evaluator.util.ArrayUtils;
import com.ledoyen.aash.evaluator.util.Preconditions;

/**
 * Utilities about Compilation and Evaluation of Java expressions.
 * 
 * @author L.LEDOYEN
 * @since 2.2.4 (01/07/2014)
 */
public final class ExpressionUtils {

	private static final String ARGUMENT_PREFIX = "\\$\\{";

	private static final String ARGUMENT_SUFFIX = "\\}";

	/** Matches <b>${some var name}</b> */
	private static final Pattern ARGUMENT_PATTERN = Pattern.compile(ARGUMENT_PREFIX + ".*?" + ARGUMENT_SUFFIX);

	/**
	 * Get all argument names from an expression, where Arguments are matched by
	 * {@link ExpressionCompiler.ARGUMENT_PATTERN}
	 */
	public static Set<String> parseExpressionArguments(String expr) {
		Matcher m = ARGUMENT_PATTERN.matcher(expr);
		Set<String> arguments = new HashSet<>();
		while (m.find()) {
			String tokenWithDelimiters = m.group();
			arguments.add(tokenWithDelimiters.substring(2, tokenWithDelimiters.length() - 1));
		}
		return arguments;
	}

	/**
	 * Remove arguments (variables) delimiters and also replace simple quotes by
	 * double ones.<br>
	 * For example :
	 * 
	 * <pre>
	 * Evaluator.removeArgumentDelimiters("'hello' + ${WORLD}")    = "\"hello\" + world"
	 * 
	 * <pre>
	 */
	public static String removeArgumentDelimiters(String expr) {
		return expr.replaceAll("'", "\"").replaceAll(ARGUMENT_PREFIX, "").replaceAll(ARGUMENT_SUFFIX, "");
	}

	/** Convenient method to display names and types of arguments. */
	public static String buildParams(String[] parameterNames, Class<?>[] parameterTypes) {
		return buildParams(parameterNames, parameterTypes, null);
	}

	/**
	 * Convenient method to display names, types and values of arguments.<br>
	 * Display also value type if it is not the same as specified in given
	 * parameter types.
	 */
	public static String buildParams(String[] parameterNames, Class<?>[] parameterTypes, Object[] arguments) {
		Preconditions.checkArgument(ArrayUtils.getLength(parameterNames) == ArrayUtils.getLength(parameterTypes),
				String.format("names[%s] and types[%s] must be of length", ArrayUtils.getLength(parameterNames), ArrayUtils.getLength(parameterTypes)));
		if (arguments != null) {
			Preconditions.checkArgument(ArrayUtils.getLength(parameterNames) == ArrayUtils.getLength(arguments),
					String.format("names[%s] and arguments[%s] must be of length", ArrayUtils.getLength(parameterNames), ArrayUtils.getLength(arguments)));
		}
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ArrayUtils.getLength(parameterNames); i++) {
			sb.append(parameterNames[i]).append(": ");
			if (parameterTypes[i] != null) {
				sb.append(parameterTypes[i].getSimpleName());
			} else {
				sb.append("???");
			}
			if (arguments != null) {
				sb.append(" = ").append(arguments[i]);
				if (arguments[i] != null && (parameterTypes[i] == null || (parameterTypes[i] != null && !parameterTypes[i].equals(arguments[i].getClass())))) {
					sb.append(" (").append(arguments[i].getClass().getSimpleName()).append(")");
				}
			}
			sb.append(", ");
		}
		if (ArrayUtils.getLength(parameterNames) > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	/**
	 * Adapt a value to the given compatible type.<br>
	 * Supported adaptations :
	 * <ul>
	 * <li>null -> any type</li>
	 * <li>any value of type T -> T</li>
	 * <li>any value -> String</li>
	 * <li>Long value :
	 * <ul>
	 * <li>-> Integer</li>
	 * <li>-> Double</li>
	 * <li>-> Date</li>
	 * </ul>
	 * </li>
	 * <li>Double value :
	 * <ul>
	 * <li>-> Integer</li>
	 * <li>-> Long</li>
	 * </ul>
	 * </li>
	 * <li>Integer value :
	 * <ul>
	 * <li>-> Double</li>
	 * <li>-> Long</li>
	 * </ul>
	 * </li>
	 * <li>Date value :
	 * <ul>
	 * <li>-> Long</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param to
	 *            : destination type
	 * 
	 * @throws IllegalArgumentException
	 *             if type is not compatible with value
	 */
	@SuppressWarnings("unchecked")
	public static <U, V> V adaptToType(U value, Class<V> to) {
		Class<U> from = null;
		if (value == null) {
			return null;
		} else {
			from = (Class<U>) value.getClass();
			if (from.equals(to)) {
				return (V) value;
			} else if (String.class.equals(to)) {
				return (V) String.valueOf(value);
			} else if (Long.class.equals(from)) {
				if (long.class.equals(to)) {
					return (V) value;
				} else if (Double.class.equals(to)) {
					return (V) Double.valueOf(((Long) value).doubleValue());
				} else if (Date.class.equals(to)) {
					return (V) new Date((Long) value);
				} else if (Integer.class.equals(to)) {
					return (V) Integer.valueOf(((Long) value).intValue());
				}
			} else if (Double.class.equals(from)) {
				if (double.class.equals(to)) {
					return (V) value;
				}else if (Long.class.equals(to)) {
					return (V) Long.valueOf(((Double) value).longValue());
				} else if (Integer.class.equals(to)) {
					return (V) Integer.valueOf(((Double) value).intValue());
				}
			} else if (Integer.class.equals(from)) {
				if (int.class.equals(to)) {
					return (V) value;
				} else if (Double.class.equals(to)) {
					return (V) Double.valueOf(((Integer) value).doubleValue());
				} else if (Long.class.equals(to)) {
					return (V) Long.valueOf(((Integer) value).longValue());
				}
			} else if (Date.class.equals(from)) {
				if (Long.class.equals(to)) {
					return (V) Long.valueOf(((Date) value).getTime());
				}
			} else if (String.class.equals(from)) {
				if (Integer.class.equals(to)) {
					return (V) Integer.valueOf((String) value);
				} else if (Double.class.equals(to)) {
					return (V) Double.valueOf((String) value);
				} else if (Long.class.equals(to)) {
					return (V) Long.valueOf((String) value);
				}
			}
		}
		throw new IllegalArgumentException(String.format("Convertion from [%s] to [%s] is not handled", from, to));
	}

	/**
	 * Adapt values of the given value array to given type array (values and
	 * destination types must be compatible).
	 * 
	 * @see {@link #adaptToType}
	 */
	public static <T> Object[] adaptToTypes(Object[] values, Class<?>[] destinationTypes) {
		Preconditions.checkArgument(ArrayUtils.getLength(values) == ArrayUtils.getLength(destinationTypes),
				String.format("values[%s] and destinationTypes[%s] must be of length", ArrayUtils.getLength(values), ArrayUtils.getLength(destinationTypes)));
		Object[] result = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = adaptToType(values[i], destinationTypes[i]);
		}
		return result;
	}

	public static String[] declareStaticImports(Class<?>[] classes) {
		String[] imports = new String[classes.length];
		for(int i = 0; i < classes.length; i++) {
			imports[i] = "static " + classes[i].getName() + ".*";
		}
		return imports;
	}
}
