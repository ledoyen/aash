package com.ledoyen.sql.querybuilder.clause;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.AbstractQueryBuilder.QueryAdapter;
import com.ledoyen.sql.querybuilder.WhereClause;

public class SimpleBindedWhereClause implements WhereClause {

	private String expression;
	private String bindingName;
	private Object value;

	public SimpleBindedWhereClause(String expression, String bindingName, Object value) {
		this.expression = expression;
		this.bindingName = bindingName;
		this.value = value;
	}

	protected Object getValue() {
		return value;
	}

	public String getExpression() {
		return expression;
	}

	public boolean isApplicable() {
		return value != null;
	}

	public void apply(QueryAdapter q) {
		q.setParameter(bindingName, value);
	}

	public Collection<String> getParameterNames() {
		return Lists.newArrayList(bindingName);
	}

	public static class CollectionSimpleBindedWhereClauseBuilder {
		private String expression;
		private String bindingName;

		public CollectionSimpleBindedWhereClauseBuilder(String expression, String bindingName) {
			this.expression = expression;
			this.bindingName = bindingName;
		}

		public SimpleBindedWhereClause with(final Collection<?> value) {
			return new SimpleBindedWhereClause(expression, bindingName, value) {
				public boolean isApplicable() {
					return value != null && !value.isEmpty();
				}
			};
		}

		public SimpleBindedWhereClause with(final Map<?, ?> value) {
			return new SimpleBindedWhereClause(expression, bindingName, value) {
				public boolean isApplicable() {
					return value != null && !value.isEmpty();
				}
			};
		}
	}

	public static class SimpleBindedWhereClauseBuilder {
		private String expression;
		private String bindingName;

		public SimpleBindedWhereClauseBuilder(String expression, String bindingName) {
			this.expression = expression;
			this.bindingName = bindingName;
		}

		public SimpleBindedWhereClause with(Object value) {
			if (value instanceof Collection) {
				return new CollectionSimpleBindedWhereClauseBuilder(expression, bindingName)
						.with((Collection<?>) value);
			} else if (value instanceof Map) {
				return new CollectionSimpleBindedWhereClauseBuilder(expression, bindingName)
						.with((Map<?, ?>) value);
			} else {
				return new SimpleBindedWhereClause(expression, bindingName, value);
			}
		}

		public SimpleBindedWhereClause withString(final String value) {
			return new SimpleBindedWhereClause(expression, bindingName, value) {
				public boolean isApplicable() {
					return !Strings.isNullOrEmpty(value);
				}
			};
		}
	}
}
