package com.ledoyen.sql.querybuilder.clause;

import java.util.Collection;
import java.util.Map;

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

	public static class CollectionSimpleBindedWhereClause extends SimpleBindedWhereClause {

		private boolean collection;

		public CollectionSimpleBindedWhereClause(String expression, String bindingName,
				Collection<?> value) {
			super(expression, bindingName, value);
			this.collection = true;
		}

		public CollectionSimpleBindedWhereClause(String expression, String bindingName,
				Map<?, ?> value) {
			super(expression, bindingName, value);
			this.collection = false;
		}

		@Override
		public boolean isApplicable() {
			boolean applicable = getValue() != null;
			if (applicable && collection) {
				applicable &= !((Collection<?>) getValue()).isEmpty();
			} else if (applicable && !collection) {
				applicable &= !((Map<?, ?>) getValue()).isEmpty();
			}
			return applicable;
		}
	}

	public static class CollectionSimpleBindedWhereClauseBuilder {
		private String expression;
		private String bindingName;

		public CollectionSimpleBindedWhereClauseBuilder(String expression, String bindingName) {
			this.expression = expression;
			this.bindingName = bindingName;
		}

		public CollectionSimpleBindedWhereClause with(Collection<?> value) {
			return new CollectionSimpleBindedWhereClause(expression, bindingName, value);
		}

		public CollectionSimpleBindedWhereClause with(Map<?, ?> value) {
			return new CollectionSimpleBindedWhereClause(expression, bindingName, value);
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
				return new CollectionSimpleBindedWhereClause(expression, bindingName,
						(Collection<?>) value);
			} else if (value instanceof Map) {
				return new CollectionSimpleBindedWhereClause(expression, bindingName,
						(Map<?, ?>) value);
			} else {
				return new SimpleBindedWhereClause(expression, bindingName, value);
			}
		}
	}
}
