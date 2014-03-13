package com.ledoyen.sql.querybuilder;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public final class QueryBuilder extends AbstractQueryBuilder {

	private QueryBuilder(String select) {
		super(select);
	}

	public static QueryBuilder select(String select) {
		return new QueryBuilder(select);
	}

	public QueryBuilder where(WhereClause... clauses) {
		return (QueryBuilder) super.where(clauses);
	}

	public QueryBuilder groupOrOrder(String groupOrOrderClause) {
		return (QueryBuilder) super.groupOrOrder(groupOrOrderClause);
	}

	public Query query(EntityManager em) {
		return prepareQuery(em.createQuery(getQueryAsString()));
	}

	public Query nativeQuery(EntityManager em) {
		return prepareQuery(em.createNativeQuery(getQueryAsString()));
	}

	private Query prepareQuery(Query query) {
		QueryAdapter queryAdapter = new  JpaQueryAdapter(query);
		for (WhereClause whereClause : clauses) {
			if (whereClause.isApplicable()) {
				whereClause.apply(queryAdapter);
			}
		}
		return query;
	}

	private static class JpaQueryAdapter implements QueryAdapter {
		private Query query;
		public JpaQueryAdapter(Query query) {
			this.query =query;
		}
		public void setParameter(String name, Object value) {
			query.setParameter(name, value);
		}
		
	}
}
