package com.ledoyen.sql.querybuilder.jpa;

import static com.ledoyen.sql.querybuilder.WhereClauses.conditional;
import static com.ledoyen.sql.querybuilder.WhereClauses.or;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.ledoyen.sql.querybuilder.AbstractTest;
import com.ledoyen.sql.querybuilder.QueryBuilder;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.tool.Dates;

@RunWith(Parameterized.class)
public class QueryBuilderTest extends AbstractTest implements UserClauses {

	private static EntityManager entityManager;

	@BeforeClass
	public static void setUpClass() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", new Properties());
		entityManager = emf.createEntityManager();
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		if(entityManager != null) {
			entityManager.close();
		}
	}

	public QueryBuilderTest(Integer ageMin, Integer ageMax, Double scoreMin, Double scoreMax,
			boolean scoreMinInclusive, boolean scoreMaxInclusive, String civility,
			List<String> variableNames, Date startDate, Date endDate, String code, String category) {
		super(ageMin, ageMax, scoreMin, scoreMax, scoreMinInclusive, scoreMaxInclusive, civility,
				variableNames, startDate, endDate, code, category);
	}

	@Test(expected=IllegalArgumentException.class)
	public void duplicateParameterNames() {
		Integer ageMin = 7, ageMax = 77;

		QueryBuilder
				.select(INITIAL_SELECT)
				.where(USER_AGE.with(ageMin, ageMax),
						USER_AGE.with(ageMin, ageMax),
						SCORE_HISTO.with(null),
						SCORE_HISTO.with(new Object[]{null}));
	}

	@Test
	public void whereWordNotPresentIfNoneMatchs() {
		QueryBuilder nqb = QueryBuilder
				.select(INITIAL_SELECT)
				.where(USER_AGE.with(null, null),
						REGION_CODE.with((String) null));
		assertThat(nqb.getQueryAsString(), CoreMatchers.not(CoreMatchers.containsString("where")));
	}

	@Test
	public void testNativeQueryBuilder() {
		QueryBuilder nqb = QueryBuilder
				.select(INITIAL_SELECT)
				.where(JOIN_USER_CIVILITY,
						JOIN_USER_SCORE,
						JOIN_USER_REGION,
						SCORE_HISTO.with(false),
						USER_AGE.with(ageMin, ageMax),
						SCORE_MIN.with(scoreMin, scoreMinInclusive),
						SCORE_MAX.with(scoreMax, scoreMaxInclusive),
						conditional(civility != null, "M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR),
						SCORE_NAMES.with(names),
						CREATION_DATE.betweenDates(startDate, endDate),
						or(REGION_CODE.with(code),
								REGION_CATEGORY.with(category)))
				.groupOrOrder("group by s.name");

		System.out.println(nqb.getQueryAsString());
		List<?> results = nqb.nativeQuery(entityManager).getResultList();
		System.out.println(results);
	}

	@Test
	public void testClassicWay() {
		StringBuilder request = new StringBuilder(UserClauses.INITIAL_SELECT);
		request.append(" where (c.id = u.civility_id) and (s.user_id = u.id) ");
		request.append(" and u.region_id = r.id ");
		request.append(" and (s.is_histo is false) ");

		if(ageMin != null && ageMax!=null) {
			request.append(" and timestampdiff(YEAR, u.birth_date, sysdate()) between :ageMin and :ageMax ");
		}
		if(scoreMin != null) {
			request.append(" and s.score ");
			if (scoreMinInclusive) {
				request.append(">=");
			} else {
				request.append(">");
			}
			request.append(" :scoreMin ");
		}
		if(scoreMax != null) {
			request.append(" and s.score ");
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
		if (names != null && !names.isEmpty()) {
			request.append("and s.name in (:names) ");
		}
		if (startDate != null) {
			request.append(" and u.creation_date between :startDate and :endDate ");
		}
		if (code != null) {
			request.append(" and r.code = :code ");
		}
		if (category != null && category.length() > 0) {
			request.append(" and r.category = :category ");
		}
		request.append(" group by s.name");
		
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
		if (names != null && !names.isEmpty()) {
			query.setParameter("names", names);
		}
		if (startDate != null) {
		    query.setParameter("startDate", Dates.floor(startDate));
		    query.setParameter("endDate", endDate != null ? Dates.ceiling(endDate) : Dates.ceiling(startDate));
		}
		if (code != null) {
		    query.setParameter("code", code);
		}
		if (category != null && category.length() > 0) {
		    query.setParameter("category", category);
		}
		
		List<?> results = query.getResultList();
		System.out.println(results);
	}

	// TODO test equivalent with querydsl
}
