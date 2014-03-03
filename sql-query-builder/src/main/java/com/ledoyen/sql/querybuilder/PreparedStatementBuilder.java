package com.ledoyen.sql.querybuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public final class PreparedStatementBuilder extends AbstractQueryBuilder {

	private PreparedStatementBuilder(String select) {
		super(select);
	}

	public static PreparedStatementBuilder select(String select) {
		return new PreparedStatementBuilder(select);
	}

	public PreparedStatementBuilder where(WhereClause... clauses) {
		return (PreparedStatementBuilder) super.where(clauses);
	}

	public PreparedStatementBuilder groupOrOrder(String groupOrOrderClause) {
		return (PreparedStatementBuilder) super.groupOrOrder(groupOrOrderClause);
	}

	public String getQueryAsString() {
		return buildPositionQueryAndMap(super.getQueryAsString(), WhereClauses.getParameterNames(clauses)).positionQuery;
	}

	public PreparedStatement preparedStatement(Connection connection) throws SQLException {
		String queryWithNamedParameters = super.getQueryAsString();
		
		PositionQueryAndMap positionQueryAndMap = buildPositionQueryAndMap(queryWithNamedParameters, WhereClauses.getParameterNames(clauses));
		PreparedStatement preparedStatement = connection.prepareStatement(positionQueryAndMap.positionQuery);
		QueryAdapter queryAdapter = new PreparedStatementAdapter(preparedStatement, positionQueryAndMap.positionMap);
		for (WhereClause whereClause : clauses) {
			if (whereClause.isApplicable()) {
				whereClause.apply(queryAdapter);
			}
		}
		return preparedStatement;
	}

	private static PositionQueryAndMap buildPositionQueryAndMap(final String queryWithNamedParameters, List<String> parameterNames) {
		String positionQuery = queryWithNamedParameters;
		Map<String, Integer> positionMap = Maps.newHashMap();
		int position = 0;
		for(String parameterName : parameterNames) {
			position++;
			positionQuery = positionQuery.replaceFirst(":" + parameterName, "?");
			positionMap.put(parameterName, position);
		}
		return new PositionQueryAndMap(positionQuery, positionMap);
	}

	private static class PreparedStatementAdapter implements QueryAdapter {
		private PreparedStatement preparedStatement;
		private Map<String, Integer> positionMap;

		public PreparedStatementAdapter(PreparedStatement preparedStatement, Map<String, Integer> positionMap) {
			this.preparedStatement = preparedStatement;
			this.positionMap = positionMap;
		}

		public void setParameter(String name, Object value) {
			try {
				preparedStatement.setObject(positionMap.get(name), value);
			} catch (SQLException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	private static class PositionQueryAndMap {
		private String positionQuery;
		private Map<String, Integer> positionMap;
		
		public PositionQueryAndMap(String positionQuery, Map<String, Integer> positionMap) {
			this.positionQuery = positionQuery;
			this.positionMap = positionMap;
		}
	}
}
