package com.ledoyen.sql.querybuilder.clause;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.AbstractQueryBuilder.QueryAdapter;
import com.ledoyen.sql.querybuilder.WhereClause;

public class DoubleBindedWhereClause implements WhereClause {

	private String expression;
	private String bindingName;
	private String bindingName2;
	private Object value;
	private Object value2;

	public DoubleBindedWhereClause(String expression, String bindingName, String bindingName2,
			Object value, Object value2) {
		this.expression = expression;
		this.bindingName = bindingName;
		this.bindingName2 = bindingName2;
		this.value = value;
		this.value2 = value2;
	}

	public String getExpression() {
		return expression;
	}

	public boolean isApplicable() {
		return value != null && value2 != null;
	}

	public void apply(QueryAdapter q) {
		q.setParameter(bindingName, value);
		q.setParameter(bindingName2, value2);
	}

	public Collection<String> getParameterNames() {
		return Lists.newArrayList(bindingName, bindingName2);
	}

	public static class DoubleBindedWhereClauseBuilder {
		private String expression;
		private String bindingName;
		private String bindingName2;

		public DoubleBindedWhereClauseBuilder(String expression, String bindingName,
				String bindingName2) {
			this.expression = expression;
			this.bindingName = bindingName;
			this.bindingName2 = bindingName2;
		}

		public DoubleBindedWhereClause with(Object value, Object value2) {
			return new DoubleBindedWhereClause(expression, bindingName, bindingName2, value, value2);
		}
	}
}
