package com.ledoyen.sql.querybuilder;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.AbstractQueryBuilder.QueryAdapter;
import com.ledoyen.tool.Lists2;

public final class WhereClauses {

	private WhereClauses() {
	}

	static List<String> getParameterNames(WhereClause[] clauses) {
		return Lists2.flatten(Collections2.transform(Collections2.filter(
				Lists.newArrayList(clauses), new WhereClause.IsApplicableFilter()),
				new WhereClause.GetParameterNames()));
	}

	public static WhereClause ifTrue(boolean condition, WhereClause clause) {
		if (condition) {
			return clause;
		} else {
			return new WhereClause() {

				public boolean isApplicable() {
					return false;
				}

				public Collection<String> getParameterNames() {
					return null;
				}

				public String getExpression() {
					return null;
				}

				public void apply(QueryAdapter q) {
				}
			};
		}
	}
}
