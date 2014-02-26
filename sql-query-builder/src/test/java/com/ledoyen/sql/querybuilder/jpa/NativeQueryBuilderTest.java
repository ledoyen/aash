package com.ledoyen.sql.querybuilder.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.ledoyen.sql.querybuilder.UserClauses;
import com.ledoyen.tool.Dates;

public class NativeQueryBuilderTest implements UserClauses {

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
				.groupOrOrder("group by sp.var_name");

		System.out.println(nqb.getQueryAsString());
		List<?> results = nqb.query(getEntityManager()).getResultList();
		System.out.println(results);
	}

	private EntityManager getEntityManager() {
		// TODO stub the damned thing
		return null;
	}
}
