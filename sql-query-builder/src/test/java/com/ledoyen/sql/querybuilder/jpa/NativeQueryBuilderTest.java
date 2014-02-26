package com.ledoyen.sql.querybuilder.jpa;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.sql.querybuilder.jdbc.PreparedStatementBuilderTest;
import com.ledoyen.tool.Dates;

public class NativeQueryBuilderTest implements UserClauses {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		String driver;
		// Starting Derby in Network (multi JVM) mode:
		driver = "org.h2.Driver";
		
		StringBuilder sb = new StringBuilder("jdbc:h2:mem:test");
		sb.append(";INIT=runscript from '");
		URL createFile = PreparedStatementBuilderTest.class.getClassLoader().getResource("create.sql");
		sb.append(createFile.getFile().substring(1));
		sb.append("'\\;runscript from '");
		URL populateFile = PreparedStatementBuilderTest.class.getClassLoader().getResource("populate.sql");
		sb.append(populateFile.getFile().substring(1));
		sb.append("'");
		
		Properties emfProps = new Properties();
		emfProps.setProperty("javax.persistence.jdbc.driver", driver);
		emfProps.setProperty("javax.persistence.jdbc.url", sb.toString());
		emfProps.setProperty("javax.persistence.jdbc.user", "sa");
		emfProps.setProperty("javax.persistence.jdbc.password", "");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", emfProps);
		entityManager = emf.createEntityManager();
	}

	@Test
	public void test() {
		Integer ageMin = 7, ageMax = 77;
		Double scoreMin = 0d, scoreMax = null;
		boolean scoreMinInclusive = true, scoreMaxInclusive = true;
		String civility = "M.", code = null;
		Date startDate = new Date(), endDate = null;
		List<String> variableNames = Lists.newArrayList("toto");

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
}
