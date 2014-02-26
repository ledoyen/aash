package com.ledoyen.sql.querybuilder;

import java.util.List;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.ledoyen.tool.Lists2;

public final class WhereClauses {

	private WhereClauses() {}

	public static List<String> getParameterNames(WhereClause[] clauses) {
		return Lists2.flatten(Collections2.transform(Collections2.filter(Lists.newArrayList(clauses), new WhereClause.IsApplicableFilter()), new WhereClause.GetParameterNames()));
	}
}
