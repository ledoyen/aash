package com.ledoyen.sql.querybuilder.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.ledoyen.sql.querybuilder.AbstractTest;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.tool.Dates;

@RunWith(Parameterized.class)
public class PreparedStatementBuilderTest extends AbstractTest implements UserClauses {

	private static Connection connection;

	@BeforeClass
	public static void setUpClass() throws ClassNotFoundException, SQLException {
		Class.forName(getDbDriver());
		connection = DriverManager.getConnection(getDbUrl(), "sa", "");
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
						BUSINESS_UNIT_CODE.with(code),
						BUSINESS_UNIT_CATEGORY.with(category))
				.groupOrOrder("group by pv.var_name");

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
