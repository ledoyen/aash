package com.ledoyen.sql.querybuilder.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.ledoyen.sql.querybuilder.AbstractTest;
import com.ledoyen.sql.querybuilder.PreparedStatementBuilder;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.sql.querybuilder.WhereClauses;

@RunWith(Parameterized.class)
public class PreparedStatementBuilderTest extends AbstractTest implements UserClauses {

	private static Connection connection;

	@BeforeClass
	public static void setUpClass() throws ClassNotFoundException, SQLException {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", new Properties());
		JdbcServicesImpl c = (JdbcServicesImpl) ((EntityManagerFactoryImpl)emf).getSessionFactory().getJdbcServices();
		connection = c.getConnectionProvider().getConnection();
	}

	public PreparedStatementBuilderTest(Integer ageMin, Integer ageMax, Double scoreMin, Double scoreMax,
			boolean scoreMinInclusive, boolean scoreMaxInclusive, String civility,
			List<String> variableNames, Date startDate, Date endDate, String code, String category) {
		super(ageMin, ageMax, scoreMin, scoreMax, scoreMinInclusive, scoreMaxInclusive, civility,
				variableNames, startDate, endDate, code, category);
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		connection.close();
	}

	@Test
	public void testPreparedStatementBuilder() throws SQLException {
		PreparedStatementBuilder psb = PreparedStatementBuilder
				.select(INITIAL_SELECT)
				.where(JOIN_USER_CIVILITY,
						JOIN_USER_SCORE,
						JOIN_USER_REGION,
						SCORE_HISTO.with(false),
						USER_AGE.with(ageMin, ageMax),
						SCORE_MIN.with(scoreMin, scoreMinInclusive),
						SCORE_MAX.with(scoreMax, scoreMaxInclusive),
						WhereClauses.conditional(civility != null, "M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR),
						SCORE_NAMES.with(names),
						CREATION_DATE.betweenDates(startDate, endDate),
						REGION_CODE.with(code),
						REGION_CATEGORY.with(category))
				.groupOrOrder("group by s.name");

		System.out.println(psb.getQueryAsString());
		ResultSet results = psb.preparedStatement(connection).executeQuery();
		while(results.next()) {
			System.out.println(results.getObject(1) + " " + results.getObject(2));
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void duplicateParameterNames() {
		Integer ageMin = 7, ageMax = 77;

		PreparedStatementBuilder
				.select(INITIAL_SELECT)
				.where(USER_AGE.with(ageMin, ageMax),
						USER_AGE.with(ageMin, ageMax));
	}
}
