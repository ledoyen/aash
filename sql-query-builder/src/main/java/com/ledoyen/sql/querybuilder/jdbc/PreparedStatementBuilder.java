package com.ledoyen.sql.querybuilder.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.ledoyen.sql.querybuilder.AbstractQueryBuilder;
import com.ledoyen.sql.querybuilder.WhereClause;

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

	public PreparedStatement preparedStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getQueryAsString());
		QueryAdapter queryAdapter = new PreparedStatementAdapter(preparedStatement, null);
		for (WhereClause whereClause : clauses) {
			if (whereClause.isApplicable()) {
				whereClause.apply(queryAdapter);
			}
		}
		return preparedStatement;
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
}
