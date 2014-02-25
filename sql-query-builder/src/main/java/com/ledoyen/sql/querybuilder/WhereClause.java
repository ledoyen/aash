package com.ledoyen.sql.querybuilder;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.ledoyen.sql.querybuilder.AbstractQueryBuilder.QueryAdapter;

public interface WhereClause {

	String getExpression();

	boolean isApplicable();

	void apply(QueryAdapter q);

	Collection<String> getParameterNames();

	public static class GetExpression implements Function<WhereClause, String> {
		public String apply(WhereClause input) {
			return input.getExpression();
		}
	}

	public static class IsApplicableFilter implements Predicate<WhereClause> {
		public boolean apply(WhereClause input) {
			return input.isApplicable();
		}
	}

	public static class GetParameterNames implements Function<WhereClause, Collection<String>> {
		public Collection<String> apply(WhereClause input) {
			return input.getParameterNames();
		}
	}
}
