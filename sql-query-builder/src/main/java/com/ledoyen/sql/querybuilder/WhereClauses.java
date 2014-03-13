package com.ledoyen.sql.querybuilder;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
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

	public static WhereClause conditional(boolean condition, WhereClause clause) {
		if (condition) {
			return clause;
		} else {
			return new WhereClause() {

				public boolean isApplicable() {
					return false;
				}

				// Will never be called
				public Collection<String> getParameterNames() {
					return null;
				}

				// Will never be called
				public String getExpression() {
					return null;
				}

				// Will never be called
				public void apply(QueryAdapter q) {
				}
			};
		}
	}

	public static WhereClause or(final WhereClause... clauses) {
		return new WhereClause() {
			public boolean isApplicable() {
				return Collections2.filter(Lists.newArrayList(clauses), new WhereClause.IsApplicableFilter()).size() > 0;
			}

			public Collection<String> getParameterNames() {
				return WhereClauses.getParameterNames(clauses);
			}

			public String getExpression() {
				return "((" + Joiner.on(") or (").join(Collections2.transform(Collections2.filter(Lists.newArrayList(clauses), new WhereClause.IsApplicableFilter()), new WhereClause.GetExpression())) + "))";
			}
			public void apply(QueryAdapter q) {
				for(WhereClause clause : clauses) {
					clause.apply(q);
				}
			}
		};
	}
}
