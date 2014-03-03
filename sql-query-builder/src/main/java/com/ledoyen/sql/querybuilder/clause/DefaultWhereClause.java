package com.ledoyen.sql.querybuilder.clause;

import java.util.Collection;
import java.util.Collections;

import com.ledoyen.sql.querybuilder.AbstractQueryBuilder.QueryAdapter;
import com.ledoyen.sql.querybuilder.WhereClause;

public class DefaultWhereClause implements WhereClause {

	private String expression;

	public DefaultWhereClause(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	public boolean isApplicable() {
		return true;
	}

	public void apply(QueryAdapter q) {
		// Do nothing has there is no binding parameter
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getParameterNames() {
		return Collections.EMPTY_SET;
	}

	public static class FormattedDefaultWhereClauseBuilder {
		private String expresssion;

		public FormattedDefaultWhereClauseBuilder(String expresssion) {
			this.expresssion = expresssion;
		}

		public DefaultWhereClause with(Object... value) {
			return new DefaultWhereClause(String.format(expresssion, value));
		}
	}
}
