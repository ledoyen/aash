package com.ledoyen.aash.evaluator;

public class Evaluation {

	private String name;

	private String expression;

	private String[] parameterNames;

	private Class<?>[] parameterTypes;

	private Class<?> returnType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public static Evaluation build(String name, String expression, Class<?> returnType) {
		return build(name, expression, null, null, returnType);
	}

	public static Evaluation build(String name, String expression, String[] parameterNames, Class<?>[] parameterTypes, Class<?> returnType) {
		Evaluation e = new Evaluation();
		e.setName(name);
		e.setExpression(expression);
		e.setParameterNames(parameterNames);
		e.setParameterTypes(parameterTypes);
		e.setReturnType(returnType);
		return e;
	}
}
