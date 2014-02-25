package com.ledoyen.sql.querybuilder.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ledoyen.sql.querybuilder.AbstractQueryBuilder;
import com.ledoyen.sql.querybuilder.WhereClause;

public final class NativeQueryBuilder extends AbstractQueryBuilder {

	private NativeQueryBuilder(String select) {
		super(select);
	}

	public static NativeQueryBuilder select(String select) {
		return new NativeQueryBuilder(select);
	}

	public NativeQueryBuilder where(WhereClause... clauses) {
		return (NativeQueryBuilder) super.where(clauses);
	}

	public NativeQueryBuilder groupOrOrder(String groupOrOrderClause) {
		return (NativeQueryBuilder) super.groupOrOrder(groupOrOrderClause);
	}

	public Query query(EntityManager em) {
		Query query = em.createNativeQuery(getQueryAsString());
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
