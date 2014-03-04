package com.ledoyen.sql.querybuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.ledoyen.tool.CountFunction;
import com.ledoyen.tool.GreaterThan;
import com.ledoyen.tool.Multimaps2;

public class AbstractQueryBuilder {

	protected String select;
	protected WhereClause[] clauses;
	protected String groupOrOrderClause;

	public AbstractQueryBuilder(String select) {
		this.select = select;
	}

	protected AbstractQueryBuilder where(WhereClause... clauses) {
		if (this.clauses != null) {
			throw new UnsupportedOperationException("#where already called");
		}
		checkParameterNameConflicts(clauses);
		this.clauses = clauses;
		return this;
	}

	private static void checkParameterNameConflicts(WhereClause[] clauses) {
		List<String> parameterNames = WhereClauses.getParameterNames(clauses);
		Map<String, Integer> parameterNamesWithCount = Maps.filterValues(Multimaps2.toMap(Multimaps.index(parameterNames, new StringToString()), new CountFunction<String>()), new GreaterThan(1));
		if(parameterNamesWithCount.size() > 0) {
			throw new IllegalArgumentException("Parameter names in WHERE clauses are in conflict : " + Joiner.on(", ").join(parameterNamesWithCount.keySet()));
		}
	}

	protected AbstractQueryBuilder groupOrOrder(String groupOrOrderClause) {
		if (this.groupOrOrderClause != null) {
			throw new UnsupportedOperationException("#groupOrOrderClause already called");
		}
		this.groupOrOrderClause = groupOrOrderClause;
		return this;
	}

	public String getQueryAsString() {
		return (Strings.nullToEmpty(select) + buildWhereClause(clauses) + Strings.nullToEmpty(groupOrOrderClause)).trim();
	}

	private static String buildWhereClause(WhereClause[] clauses) {
		String whereClause = "";
		if (clauses != null) {
			Collection<WhereClause> filteredClauses = Collections2.filter(
					Lists.newArrayList(clauses), new WhereClause.IsApplicableFilter());
			if (filteredClauses.size() > 0) {
				whereClause = " where "
						+ Joiner.on(" and ").join(
								Collections2.transform(filteredClauses,
										new WhereClause.GetExpression())) + " ";
			}
		}
		return whereClause;
	}

	public static interface QueryAdapter {
		void setParameter(String name, Object value);
	}

	private static class StringToString implements Function<String, String> {
		public String apply(String input) {
			return input;
		}
	}
}
