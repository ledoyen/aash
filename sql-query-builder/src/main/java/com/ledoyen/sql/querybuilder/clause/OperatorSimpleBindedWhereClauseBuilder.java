package com.ledoyen.sql.querybuilder.clause;

public class OperatorSimpleBindedWhereClauseBuilder {

	private String expression;
	private String bindingName;
	private Operator operator;

	public OperatorSimpleBindedWhereClauseBuilder(String expression, String bindingName,
			Operator operator) {
		this.expression = expression;
		this.bindingName = bindingName;
		this.operator = operator;
	}

	private static String buildExpression(String expression, Operator operator, boolean inclusive) {
		String stringOperator = operator.getExpression();
		if (inclusive) {
			stringOperator += "=";
		}
		return String.format(expression, stringOperator);
	}

	public SimpleBindedWhereClause with(Object value, boolean inclusive) {
		return new SimpleBindedWhereClause(buildExpression(expression, operator, inclusive), bindingName, value);
	}

	public static enum Operator {
		LESS("<"), MORE(">");

		private String expression;

		private Operator(String expression) {
			this.expression = expression;
		}

		String getExpression() {
			return expression;
		}
	}
}
