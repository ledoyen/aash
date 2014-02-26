package com.ledoyen.sql.querybuilder.jdbc;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.tool.Dates;

public class PreparedStatementBuilderTest implements UserClauses {

	private Connection connection;

	@BeforeClass
	public static void setUpClass() throws ClassNotFoundException {
		Class.forName("org.h2.Driver");
	}

	@Before
	public void setUp() throws SQLException {
		StringBuilder sb = new StringBuilder("jdbc:h2:mem:test");
		sb.append(";INIT=runscript from '");
		
		URL createFile = PreparedStatementBuilderTest.class.getClassLoader().getResource("create.sql");
		sb.append(createFile.getFile().substring(1));
		
		sb.append("'\\;runscript from '");

		URL populateFile = PreparedStatementBuilderTest.class.getClassLoader().getResource("populate.sql");
		sb.append(populateFile.getFile().substring(1));

		sb.append("'");
		connection = DriverManager.getConnection(sb.toString(), "sa", "");
	}

	@After
	public void tearDown() throws SQLException {
		connection.close();
	}

	@Test
	public void test() throws SQLException {
		Integer ageMin = 7, ageMax = 77;
		Double scoreMin = 0d, scoreMax = null;
		boolean scoreMinInclusive = true, scoreMaxInclusive = true;
		String civility = "M.", code = null;
		Date startDate = new Date(), endDate = null;
		List<String> variableNames = Lists.newArrayList("toto");

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
						BUSINESS_UNIT_CODE.with(code))
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
