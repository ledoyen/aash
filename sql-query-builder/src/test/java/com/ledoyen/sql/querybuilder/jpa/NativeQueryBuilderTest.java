package com.ledoyen.sql.querybuilder.jpa;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.ledoyen.sql.querybuilder.AbstractTest;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.tool.Dates;

@RunWith(Parameterized.class)
public class NativeQueryBuilderTest extends AbstractTest implements UserClauses {

	private static EntityManager entityManager;

	@BeforeClass
	public static void setUpClass() {
		Properties emfProps = new Properties();
		emfProps.setProperty("javax.persistence.jdbc.driver", getDbDriver());
		emfProps.setProperty("javax.persistence.jdbc.url", getDbUrl());
		emfProps.setProperty("javax.persistence.jdbc.user", "sa");
		emfProps.setProperty("javax.persistence.jdbc.password", "");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", emfProps);
		entityManager = emf.createEntityManager();
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		entityManager.close();
	}

	public NativeQueryBuilderTest(Integer ageMin, Integer ageMax, Double scoreMin, Double scoreMax,
			boolean scoreMinInclusive, boolean scoreMaxInclusive, String civility,
			List<String> variableNames, Date startDate, Date endDate, String code, String category) {
		super(ageMin, ageMax, scoreMin, scoreMax, scoreMinInclusive, scoreMaxInclusive, civility,
				variableNames, startDate, endDate, code, category);
	}

	@Test
	public void testNativeQueryBuilder() {
		NativeQueryBuilder nqb = NativeQueryBuilder
				.select(INITIAL_SELECT)
				.where(JOIN_USER_CIVILITY,
						JOIN_USER_PROFILE_VALUE,
						JOIN_USER_BUSINESS_UNIT,
						PROVILE_VALUE_HISTO.with(false),
						USER_AGE.with(ageMin, ageMax),
						PROFILE_VALUE_SCORE_MIN.with(scoreMin, scoreMinInclusive),
						PROFILE_VALUE_SCORE_MAX.with(scoreMax, scoreMaxInclusive),
						"M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR,
						PROFILE_VALUE_VARIABLE_NAMES.with(variableNames),
						CREATION_DATE.with(Dates.floor(startDate),
								Dates.ceiling(endDate != null ? endDate : startDate)),
						BUSINESS_UNIT_CODE.with(code))
				.groupOrOrder("group by pv.var_name");

		System.out.println(nqb.getQueryAsString());
		List<?> results = nqb.query(entityManager).getResultList();
		System.out.println(results);
	}

	@Test(expected=IllegalArgumentException.class)
	public void duplicateParameterNames() {
		Integer ageMin = 7, ageMax = 77;

		NativeQueryBuilder
				.select(INITIAL_SELECT)
				.where(USER_AGE.with(ageMin, ageMax),
						USER_AGE.with(ageMin, ageMax));
	}

	@Test
	public void testClassicWay() {
		StringBuilder request = new StringBuilder(UserClauses.INITIAL_SELECT);
		request.append(" where (c.id = u.civility_id) and (pv.user_id = u.id) ");
		request.append(" and u.business_id = bu.id ");
		request.append(" and (pv.is_histo is false) ");

		if(ageMin != null && ageMax!=null) {
			request.append(" and timestampdiff(YEAR, u.birth_date, sysdate()) between :ageMin and :ageMax ");
		}
		if(scoreMin != null) {
			request.append(" and pv.double_value ");
			if (scoreMinInclusive) {
				request.append(">=");
			} else {
				request.append(">");
			}
			request.append(" :scoreMin ");
		}
		if(scoreMax != null) {
			request.append(" and pv.double_value ");
			if (scoreMaxInclusive) {
				request.append("<=");
			} else {
				request.append("<");
			}
			request.append(" :scoreMax ");
		}
		if ("M.".equals(civility)) {
			request.append(" and c.name = 'M.' ");
		} else {
			request.append(" and c.name != 'M.' ");
		}
		if (variableNames != null && !variableNames.isEmpty()) {
			request.append("and pv.var_name in (:variableNames) ");
		}
		if (startDate != null) {
			request.append(" and u.creation_date between :startDate and :endDate ");
		}
		if (code != null) {
			request.append(" and bu.code = :code ");
		}
		if (category != null) {
			request.append(" and bu.category = :category ");
		}
		request.append(" group by pv.var_name");
		
		Query query = entityManager.createNativeQuery(request.toString());
		if(ageMin != null && ageMax!=null) {
			query.setParameter("ageMin", ageMin).setParameter("ageMax", ageMax);
		}
		if(scoreMin != null) {
			query.setParameter("scoreMin", scoreMin);
		}
		if(scoreMax != null) {
			query.setParameter("scoreMax", scoreMax);
		}
		if (variableNames != null && !variableNames.isEmpty()) {
			query.setParameter("variableNames", variableNames);
		}
		if (startDate != null) {
		    query.setParameter("startDate", Dates.floor(startDate));
		    query.setParameter("endDate", endDate != null ? Dates.ceiling(endDate) : Dates.ceiling(startDate));
		}
		if (code != null) {
		    query.setParameter("code", code);
		}
		if (category != null) {
		    query.setParameter("category", category);
		}
		
		List<?> results = query.getResultList();
		System.out.println(results);
	}
}
